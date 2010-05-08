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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

import evopaint.Configuration;
import evopaint.pixel.PixelColor;
import evopaint.pixel.rulebased.Rule;
import evopaint.pixel.rulebased.RuleBasedPixel;
import evopaint.util.mapping.AbsoluteCoordinate;

/**
 * Command to import an image into EvoPaint
 *
 * @author Markus Echterhoff <tam@edu.uni-klu.ac.at>
 * @author Daniel Hoelbling (http://www.tigraine.at)
 */
public class ImportCommand extends AbstractCommand {

    private final Configuration configuration;

    public ImportCommand(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void execute() {
        JFileChooser jFileChooser = new JFileChooser();
        int result = jFileChooser.showOpenDialog(jFileChooser);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = jFileChooser.getSelectedFile();
            BufferedImage img = null;
            try {
                img = ImageIO.read(file);
                int width = configuration.world.getWidth();
                if (img.getWidth() < width) {
                    width = img.getWidth();
                }
                int height = configuration.world.getHeight();
                if (img.getHeight() < height) {
                    height = img.getHeight();
                }

                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        int rgb = img.getRGB(x, y);
                        createPixel(x, y, rgb);
                    }
                }
            } catch (IOException ex) {
            }
        }
    }

    private void createPixel(int x, int y, int rgb) {
        RuleBasedPixel pixel = configuration.world.get(x, y);
        if (pixel == null) {
            AbsoluteCoordinate coordinate = new AbsoluteCoordinate(x, y, configuration.world);
            PixelColor pixelColor = new PixelColor(rgb);
            configuration.world.set(new RuleBasedPixel(pixelColor, coordinate,
                    configuration.startingEnergy, new ArrayList<Rule>()));
        } else {
            pixel.getPixelColor().setInteger(rgb);
        }
    }
}
