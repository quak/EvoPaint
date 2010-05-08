package evopaint.commands;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import evopaint.Configuration;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 08.05.2010
 * Time: 18:48:03
 * To change this template use File | Settings | File Templates.
 */
public class SaveCommand extends SaveAsCommand {

    public SaveCommand(Configuration configuration) {
        super(configuration);
    }

    @Override
    public void execute() {
        if(configuration.saveFilePath == null)
            super.execute();
        else
            SaveEvolution();
    }
}
