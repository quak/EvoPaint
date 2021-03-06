/*
 *  Copyright (C) 2010 Markus Echterhoff <tam@edu.uni-klu.ac.at>,
 *                      Daniel Hoelbling (http://www.tigraine.at)
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

package evopaint.commands;

import SevenZip.LzmaAlone;
import com.thoughtworks.xstream.XStream;
import evopaint.Configuration;
import evopaint.SaveWrapper;
import evopaint.World;
import evopaint.util.ExceptionHandler;
import java.io.File;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Loads a saved evolution
 *
 * @author Markus Echterhoff <tam@edu.uni-klu.ac.at>
 * @author Daniel Hoelbling (http://www.tigraine.at)
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


        try {
            String outputPath = absolutePath.concat(".tmp");
            String[] args = { "d", absolutePath, outputPath};
            try {
                LzmaAlone.main(args);
            } catch (Exception ex) {
                ExceptionHandler.handle(ex, true);
            }
            FileInputStream fis = new FileInputStream(outputPath);
            XStream xStream = new XStream();
            xStream.processAnnotations(World.class);
            SaveWrapper loaded = (SaveWrapper) xStream.fromXML(fis);
            loaded.Apply(config);
            config.saveFilePath = absolutePath;
            fis.close();
            new File(absolutePath.concat(".tmp")).delete();
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        config.runLevel = runLevel;
    }
}
