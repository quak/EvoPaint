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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import evopaint.Configuration;
import evopaint.Selection;
import evopaint.gui.util.IOverlay;
import evopaint.pixel.Pixel;
import evopaint.pixel.PixelColor;
import evopaint.pixel.rulebased.Rule;
import evopaint.pixel.rulebased.RuleBasedPixel;
import evopaint.util.mapping.AbsoluteCoordinate;

/**
 * Command to copy the current selection
 *
 * @author Markus Echterhoff <tam@edu.uni-klu.ac.at>
 * @author Daniel Hoelbling (http://www.tigraine.at)
 */
public class CopySelectionCommand extends AbstractCommand {

    private final Configuration config;
    private SelectionCopyOverlay overlay;
    private Point location;
    private Rectangle rect;
    private RuleBasedPixel[][] pixels;

    public CopySelectionCommand(Configuration config) {
        this.config = config;
    }

    public void setLocation(Point p) {
        location = p;
    }

    public void copyCurrentSelection() {
        Selection activeSelection = config.mainFrame.getShowcase().getActiveSelection();
        rect = activeSelection.getRectangle();
        pixels = new RuleBasedPixel[rect.width][rect.height];
        overlay = new SelectionCopyOverlay(rect.width, rect.height);
        for (int x = 0; x < rect.width; x++) {
            for (int y = 0; y < rect.height; y++) {
                Pixel pixel = config.world.get(rect.x + x, rect.y + y);
                if (pixel == null) {
                    continue;
                }
                if (pixel instanceof RuleBasedPixel) {
                    pixels[x][y] = (RuleBasedPixel) pixel;
                }
                overlay.setRGB(x, y, pixel.getPixelColor().getInteger());
            }
        }
    }

    public void startDragging() {
        config.mainFrame.getShowcase().subscribe(overlay);
    }

    public void stopDragging() {
        config.mainFrame.getShowcase().unsubscribe(overlay);
    }

    public void submitPaste() {
        stopDragging();

        for (int x = 0; x < overlay.getWidth(); x++) {
            for (int y = 0; y < overlay.getHeight(); y++) {
                if (!pixelExistsInSource(x, y)) {
                    continue;
                }
                RuleBasedPixel pixel = config.world.get(location.x + x, location.y + y);
                if (pixel != null) {
                    pixel.getPixelColor().setInteger(overlay.getRGB(x, y));
                    if (pixelExistsInSource(x, y)) {
                        pixel.setRules(pixels[x][y].getRules());
                    }
                } else {
                    PixelColor color = new PixelColor(overlay.getRGB(x, y));
                    List<Rule> rules = new ArrayList<Rule>();
                    if (pixelExistsInSource(x, y)) {
                        rules = pixels[x][y].getRules();
                    }
                    RuleBasedPixel pix = new RuleBasedPixel(color, new AbsoluteCoordinate(location.x + x, location.y + y, config.world), config.startingEnergy, rules);
                    config.world.set(pix);
                }
            }
        }
        config.mainFrame.setActiveTool(null);
    }

    private boolean pixelExistsInSource(int x, int y) {
        return pixels[x][y] != null;
    }

    @Override
    public void execute() {
        if (config.mainFrame.getShowcase().getActiveSelection() == null) {
            return;
        }
        config.mainFrame.setActiveTool(CopySelectionCommand.class);
        copyCurrentSelection();
    }

    private class SelectionCopyOverlay extends BufferedImage implements IOverlay {

        public SelectionCopyOverlay(int width, int height) {
            super(width, height, BufferedImage.TYPE_INT_RGB);
        }

        @Override
        public void paint(Graphics2D g2) {
            g2.drawImage(this, location.x, location.y, config.mainFrame.getShowcase());
        }
    }
}
