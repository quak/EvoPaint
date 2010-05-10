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

import evopaint.gui.util.JProgressDialog;
import evopaint.interfaces.IChangeListener;
import evopaint.pixel.Pixel;
import evopaint.util.ExceptionHandler;
import evopaint.util.avi.AVIOutputStream;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * This class is used to bridge the internals of the world to any outut. It
 * currently creates a <code>BufferedImage</code> of the world for display and
 * creates videos.
 *
 * @author Markus Echterhoff <tam@edu.uni-klu.ac.at>
 */
public class Perception {
    private Configuration configuration;
    private BufferedImage image;
    private int[] internalImage;
    File videoFile;
    AVIOutputStream videoOut;

    /**
     * get the current image representation of the world that was created
     *
     * @return the most recent snapshot of the world
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * updates the image representation of the world
     */
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

    /**
     * starts video recording if possible
     *
     * @return true if video-recording was successfully started, false otherwise
     */
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

    /**
     * stops video recording, called from GUI
     */
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

                                    if (option == JFileChooser.APPROVE_OPTION) {
                                        String selectedFilePath = fileChooser.getSelectedFile().getAbsolutePath();
                                        EncoderThread t = new EncoderThread(selectedFilePath);
                                        t.start();
                                    } else {
                                        videoFile.delete();
                                        videoFile = null;
                                    }
                                }
                            }
                    });
                }
        });
    }

    private class EncoderThread extends Thread {

        private String saveLocationPath;
        private JProgressDialog progressDialog;

        public EncoderThread(String saveLocationPath) {
            this.saveLocationPath = saveLocationPath;
        }

        @Override
        public void run() {
            encodeVideo();
        }

        private void encodeVideo() {
            progressDialog = new JProgressDialog("encode your video",
                            "This may take a long time depending on the length of your video. I hope you have got enough CPUs to continue painting while I encode.");
            progressDialog.pack();
            progressDialog.setVisible(true);

            boolean deleteUncompressed = true;

            File saveLocation = new File(saveLocationPath);
            File tmpLocation = new File(saveLocation.getAbsolutePath() + ".part");
            if (false == saveLocation.getName().endsWith(".avi")) {
                saveLocation = new File(saveLocation.getAbsolutePath() + ".avi");
            }
            String encoderCommand = null;
            try {
                String [] cmdArray = Configuration.ENCODER_COMMAND.split("\\s+");
                System.out.println(cmdArray[0] + " " + cmdArray[1]);
                for (int i = 0; i < cmdArray.length; i++) {
                    if (cmdArray[i].equals("INPUT_FILE")) {
                        cmdArray[i] = videoFile.getAbsolutePath();
                    }
                    else if (cmdArray[i].equals("OUTPUT_FILE")) {
                        cmdArray[i] = tmpLocation.getAbsolutePath();
                    }
                }
                Process proc = Runtime.getRuntime().exec(cmdArray);
                
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
                ExceptionHandler.handle(new Exception(), false, "<p>I failed to encode your video using the encoding command " +
                        "\"" + encoderCommand + "\"" +
                        " called from evopaint home directory " +
                        "\"" + Configuration.FILE_HANDLER.getHomeDir() + "\"" +
                        ", if you are on Windows, this is most likely a bug, if you are on a unix style OS: do you have mencoder (mplayer) installed? You can find the recorded video in MPNG format in the evopaint home folder (default is the same folder EvoPaint resides in) in case you want to compress it manually.</p>");
            }
            else if (false == tmpLocation.renameTo(saveLocation)) {
                deleteUncompressed = false;
                ExceptionHandler.handle(new Exception(), false, "<p>Hello there, I have good news and bad news. The good news is, I successfully recorded and encoded your video. The bad news is I could not rename it from \"" + tmpLocation.getName() + "\" to \"" + saveLocation.getName() + "\" for some reason.</p>");
            }

            if (deleteUncompressed) {
                videoFile.delete();
                // delete log file of mencoder run
                new File(Configuration.FILE_HANDLER.getHomeDir(), "divx2pass.log").delete();
                progressDialog.done();
            } else {
                progressDialog.dispose();
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
                    //Reader r = new InputStreamReader(in);
                    //char [] buf = new char[100];
                    //while (r.read(buf) != -1) {
                    //    System.out.print(buf);
                    //}
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

    /**
     * Sets the dimension of the produced image. Since resizing operations will
     * disturb the video creation, this method will fail if a video is
     * currently being recorded
     *
     * @param dimension
     * @return true if setting a new dimension was possible, false otherwise
     */
    public boolean setDimension(Dimension dimension) {
        if (videoFile != null) {
            ExceptionHandler.handle(new Exception(), false, "I cannot resize while I record a video");
            return false;
        }
        this.image = new BufferedImage(dimension.width, dimension.height,
                BufferedImage.TYPE_INT_RGB);
        this.internalImage = ((DataBufferInt)this.image.getRaster().getDataBuffer()).getData();
        return true;
    }

    /**
     * Constructor
     *
     * @param configuration
     */
    public Perception(Configuration configuration) {
        this.configuration = configuration;
        this.image = new BufferedImage(configuration.getDimension().width, configuration.getDimension().height,
                BufferedImage.TYPE_INT_RGB);
        this.internalImage = ((DataBufferInt)this.image.getRaster().getDataBuffer()).getData();
    }
    
}
