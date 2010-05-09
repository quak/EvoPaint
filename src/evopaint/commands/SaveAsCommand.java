package evopaint.commands;

import SevenZip.LzmaAlone;
import com.thoughtworks.xstream.XStream;
import evopaint.Configuration;
import evopaint.World;
import evopaint.gui.util.JProgressDialog;
import evopaint.interfaces.IChangeListener;
import evopaint.util.ExceptionHandler;
import java.io.File;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 08.05.2010
 * Time: 18:48:12
 * To change this template use File | Settings | File Templates.
 */
public class SaveAsCommand extends AbstractCommand {
    protected Configuration configuration;
    private JProgressDialog progressDialog;

    public SaveAsCommand(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void execute() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("*.evo", "evo"));
        final int option = fileChooser.showSaveDialog(configuration.mainFrame);
        if (option == JFileChooser.APPROVE_OPTION) {
            String selectedFilePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!selectedFilePath.endsWith(".evo")) selectedFilePath = selectedFilePath + ".evo";
            configuration.saveFilePath = selectedFilePath + ".tmp";
            SaveEvolution();
        }
    }

    protected void SaveEvolution()
    {
        int runlevel = configuration.runLevel;
        disableEvolution();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                configuration.world.addChangeListener(new IChangeListener() {

                    public void changed() {

                        progressDialog = new JProgressDialog("save your evolution", "This happens in two stages: a few seconds of saving in which EvoPaint is not usable followed by some more seconds of compression during which you can use EvoPaint as usual.");
                        progressDialog.pack();
                        progressDialog.setVisible(true);

                        try {
                            OutputStream outputStream = new FileOutputStream(configuration.saveFilePath);

                            XStream stream = new XStream();
                            stream.processAnnotations(World.class);
                            stream.toXML(configuration.world, outputStream);
                            outputStream.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        LZMACompressor compressor = new LZMACompressor(configuration.saveFilePath);
                        compressor.start();
                    }
                });
            }
        });

        //Resume evo
        configuration.runLevel = runlevel;
    }

    private void disableEvolution() {
        configuration.runLevel = Configuration.RUNLEVEL_STOP;
    }

    private class LZMACompressor extends Thread {
        private String inputPath;

        public LZMACompressor(String inputPath) {
            this.inputPath = inputPath;
        }

        @Override
        public void run() {
            try {
                String[] args = { "e", inputPath, inputPath.substring(0, inputPath.length() - 4) };
                LzmaAlone.main(args);
                new File(inputPath).delete();
                progressDialog.done();
            } catch (Exception ex) {
                ExceptionHandler.handle(ex, true);
            }
        }
    }
}
