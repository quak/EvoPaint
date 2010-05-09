/*
 *  Copyright (C) 2010 Markus Echterhoff <tam@edu.uni-klu.ac.at>,
 *                      Augustin Malle
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

package evopaint.gui;

import evopaint.Configuration;
import evopaint.util.ExceptionHandler;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * World -> Export dialog
 *
 * @author Markus Echterhoff <tam@edu.uni-klu.ac.at>
 * @author Augustin Malle
 */
public class ExportDialog implements ActionListener {

    private Configuration configuration;
    private MainFrame frame;
    private File file;
    private String path;

    public ExportDialog(Configuration configuration) {
        this.configuration = configuration;
    }

    public void actionPerformed(java.awt.event.ActionEvent e) {

        configuration.runLevel = Configuration.RUNLEVEL_STOP;
        BufferedImage img = configuration.mainFrame.getShowcase().scaleAndTranslate(configuration.perception.getImage());


        try {
            FileNameExtensionFilter jpgFilter = new FileNameExtensionFilter("*.jpg", "jpg", "jpeg");
            FileNameExtensionFilter pngFilter = new FileNameExtensionFilter("*.png", "png");
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(jpgFilter);
            chooser.setFileFilter(pngFilter);
            int option = chooser.showSaveDialog(frame);

            if (option == JFileChooser.APPROVE_OPTION) {
                if (chooser.getSelectedFile() != null) {
                    file = chooser.getSelectedFile();

                    if ((chooser.getFileFilter().getDescription()).compareTo("*.jpg") == 0) {
                        checkExtension(".jpg");
                        FileOutputStream fos = new FileOutputStream(path);
                        ImageIO.write(img, "jpg", fos);
                        fos.close();
                    } else {
                        checkExtension(".png");
                        ImageIO.write(img, "png", new File(path));
                    }
                }
            }
        } catch (IOException e1) {
            ExceptionHandler.handle(e1, true);
        }

        configuration.runLevel = Configuration.RUNLEVEL_RUNNING;
    }

    public void checkExtension(String ending) {
        if (file.getPath().endsWith(ending)) {
            path = file.getPath();
        } else {
            path = file.getPath().concat(ending);
        }
    }
}
