/*
 *  Copyright (C) 2010 Markus Echterhoff <tam@edu.uni-klu.ac.at>
 *
 *  This file is part of EvoPaint.
 *
 *  EvoPaint is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with EvoPaint.  If not, see <http://www.gnu.org/licenses/>.
 */

package evopaint;

import evopaint.interfaces.IChangeListener;
import evopaint.pixel.Pixel;
import evopaint.util.ExceptionHandler;
import evopaint.util.avi.AVIOutputStream;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Semaphore;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Markus Echterhoff <tam@edu.uni-klu.ac.at>
 */
public class Perception {
    private Configuration configuration;
    private BufferedImage image;
    private int[] internalImage;
    File videoFile;
    AVIOutputStream videoOut;

    public BufferedImage getImage() {
        return image;
    }

    public void createImage() {
        for (int i = 0; i < internalImage.length; i++) {
            Pixel pixie = configuration.world.getUnclamped(i);
            internalImage[i] =
                        pixie == null ?
                        configuration.backgroundColor :
                        pixie.getPixelColor().getInteger();
        }
        if (videoOut != null) {
            try {
                videoOut.writeFrame(configuration.mainFrame.getShowcase().translate(image));
            } catch (IOException ex) {
                ExceptionHandler.handle(ex, false, "Cannot write to video file anymore... delete some porn or go buy a harddisk!?");
            }
        }
    }

    public synchronized boolean startRecording() {
        if (videoOut != null) {
            return false;
        }
        try {
            videoFile = new File(Configuration.FILE_HANDLER.getHomeDir(),
                    "EvoPaint-recording.avi");
            for (int i = 1; videoFile.exists(); i++) {
                videoFile = new File(Configuration.FILE_HANDLER.getHomeDir(),
                    "EvoPaint-recording_" + i + ".avi");
            }
            videoOut = new AVIOutputStream(
                    videoFile, AVIOutputStream.VideoFormat.PNG);
            videoOut.setTimeScale(1);
            videoOut.setFrameRate(configuration.fpsVideo);
        } catch (IOException ex) {
            ExceptionHandler.handle(ex, false, "Cannot open your video file. Ya well, plenty of reasons possible... you fix it!?");
            videoOut = null;
            return false;
        }
        return true;
    }

    public synchronized void stopRecording() {
        SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    configuration.world.addChangeListener(new IChangeListener() {

                            public void changed() {
                                boolean finishedOK = false; // if we call showSaveDialog() after videoOut.finish(), the video consists of one long same frame only
                                try {
                                    videoOut.finish();
                                    finishedOK = true;
                                } catch (IOException ex) {
                                    ExceptionHandler.handle(ex, false);
                                } finally {
                                    videoOut = null;
                                }
                                if (finishedOK) {
                                    JFileChooser fileChooser = new JFileChooser();
                                    fileChooser.setFileFilter(new FileNameExtensionFilter("*.avi", "avi"));
                                    int option = fileChooser.showSaveDialog(configuration.mainFrame);
                                    
                                    String selectedFilePath = fileChooser.getSelectedFile().getAbsolutePath();
                                    if (option == JFileChooser.APPROVE_OPTION) {
                                        EncoderThread t = new EncoderThread(selectedFilePath);
                                        t.start();
                                    }
                                }
                            }
                    });
                }
        });
    }

    private class EncoderThread extends Thread {

        private String saveLocationPath;
        private Semaphore semaphore;

        public EncoderThread(String saveLocationPath) {
            this.saveLocationPath = saveLocationPath;
        }

        @Override
        public void run() {
            encodeVideo();
        }

        private void encodeVideo() {

            boolean deleteUncompressed = true;

            File saveLocation = new File(saveLocationPath);
            File tmpLocation = new File(saveLocation.getAbsolutePath() + ".part");
            if (false == saveLocation.getName().endsWith(".avi")) {
                saveLocation = new File(saveLocation.getAbsolutePath() + ".avi");
            }
            try {
                // this might look more complicated than you might think it should be
                // but under windows the backslash in file.getAbsolutePath() causes
                // problems when used as replacement argument in string.replaceAll()
                String [] inputSplitted = Configuration.ENCODER_COMMAND.split("INPUT_FILE", 2);
                String inputInserted = inputSplitted[0] + videoFile.getAbsolutePath() + inputSplitted[1];
                String [] outputSplitted = inputInserted.split("OUTPUT_FILE", 2);
                String outputInserted = outputSplitted[0] + tmpLocation.getAbsolutePath() + outputSplitted[1];
                Process proc = Runtime.getRuntime().exec(outputInserted);
                // NOTE mencoder produces a lot of output, which will fill java's buffers and cause
                // mencoder to hang indefinitely. to prevent this, I added the "-quiet" option
                // to the options string, but apparently that is not enough for Java on Windows
                // so here we go:
                OutputDestroyer destroyerOfOutputs1 = new OutputDestroyer(proc.getErrorStream());
                destroyerOfOutputs1.start();
                OutputDestroyer destroyerOfOutputs2 = new OutputDestroyer(proc.getInputStream());
                destroyerOfOutputs2.start();
                try {
                    proc.waitFor();
                } catch (InterruptedException ex) {
                    ExceptionHandler.handle(ex, true);
                }
            } catch (IOException ex) {
                ExceptionHandler.handle(ex, false, "<p>Failed to encode the video due to an IO Exception. Your video is saved in MPNG format in the same folder EvoPaint resides in, go and convert it yourself!</p>");
                deleteUncompressed = false;
            }

            if (false == tmpLocation.exists()) {
                deleteUncompressed = false;
                ExceptionHandler.handle(new Exception(), false, "<p>I failed to encode your video using the encoding command" +
                        "\"" + Configuration.ENCODER_COMMAND + "\"" +
                        " called from working directory " +
                        "\"" + System.getProperty("user.dir") + "\"" +
                        ", if you are on Windows, this is most likely a bug, if you are on a unix style OS: do you have mencoder (mplayer) installed? You can find the recorded video in MPNG format in the same folder EvoPaint resides in if you want to compress it manually.</p>");
            }
            else if (false == tmpLocation.renameTo(saveLocation)) {
                deleteUncompressed = false;
                ExceptionHandler.handle(new Exception(), false, "<p>Hello there, I have good news and bad news. The good news is, I successfully recorded and encoded your video. The bad news is I could not rename it from \"" + tmpLocation.getName() + "\" to \"" + saveLocation.getName() + "\" for some reason.</p>");
            }

            if (deleteUncompressed == true) { // success
                JOptionPane.showMessageDialog(configuration.mainFrame, "I finished encoding your video, are you proud of me? I know I am!");
            }

            if (deleteUncompressed) {
                videoFile.delete();
            }
            videoFile = null;
        }

        private class OutputDestroyer extends Thread {
            private InputStream in;

            public OutputDestroyer(InputStream in) {
                this.in = in;
            }

            @Override
            public void run() {
                try {
                    while (in.read() != -1) {
                    }
                } catch (IOException ex) {
                    ExceptionHandler.handle(ex, false);
                } finally {
                    try {
                        in.close();
                    } catch (IOException ex) {
                        ExceptionHandler.handle(ex, false);
                    }
                }
            }
        }
    }

    public Perception(Configuration configuration) {
        this.configuration = configuration;
        this.image = new BufferedImage(configuration.dimension.width, configuration.dimension.height,
                BufferedImage.TYPE_INT_RGB);
        this.internalImage = ((DataBufferInt)this.image.getRaster().getDataBuffer()).getData();

    }
}
