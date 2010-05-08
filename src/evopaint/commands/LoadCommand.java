package evopaint.commands;

import com.thoughtworks.xstream.XStream;
import evopaint.Configuration;
import evopaint.World;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 08.05.2010
 * Time: 19:46:11
 * To change this template use File | Settings | File Templates.
 */
public class LoadCommand extends AbstractCommand {
    private Configuration config;

    public LoadCommand(Configuration config) {
        this.config = config;
    }

    @Override
    public void execute() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("*.evo", "evo"));
        int option = fileChooser.showOpenDialog(config.mainFrame);
        if (option == JFileChooser.APPROVE_OPTION) {
            LoadEvolution(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void LoadEvolution(final String absolutePath) {
        int runLevel = config.runLevel;
        config.runLevel = Configuration.RUNLEVEL_STOP;

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    FileInputStream fis = new FileInputStream(absolutePath);
                    XStream xStream = new XStream();
                    xStream.processAnnotations(World.class);
                    World world1 = new World(config);
                    World world = (World) xStream.fromXML(fis, world1);
                    world.setConfiguration(config);
                    config.world = world;
                    config.saveFilePath = absolutePath;
                    fis.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });

        config.runLevel = runLevel;
    }
}
