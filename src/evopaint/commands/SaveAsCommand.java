package evopaint.commands;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import evopaint.Configuration;
import evopaint.World;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 08.05.2010
 * Time: 18:48:12
 * To change this template use File | Settings | File Templates.
 */
public class SaveAsCommand extends AbstractCommand {
    protected Configuration configuration;
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
            configuration.saveFilePath = selectedFilePath;
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
                try {
                    OutputStream outputStream = new FileOutputStream(configuration.saveFilePath);
                    //outputStream = new GZIPOutputStream(outputStream);

                    XStream stream = new XStream(new DomDriver());
                    stream.processAnnotations(World.class);
                    stream.toXML(configuration.world, outputStream);
                    outputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //Resume evo
        configuration.runLevel = runlevel;
    }

    private void disableEvolution() {
        configuration.runLevel = Configuration.RUNLEVEL_STOP;
    }
}
