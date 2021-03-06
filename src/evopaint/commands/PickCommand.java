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

import java.awt.Point;

import evopaint.Configuration;
import evopaint.interfaces.IChangeListener;
import evopaint.pixel.rulebased.RuleBasedPixel;
import evopaint.pixel.rulebased.RuleSet;
import javax.swing.SwingUtilities;

/**
 * Command to pick the color of a pixel on the canvas. "Color" here means the
 * actual color of the pixel as well as its rule set.
 *
 * @author Markus Echterhoff <tam@edu.uni-klu.ac.at>
 * @author Daniel Hoelbling (http://www.tigraine.at)
 */
public class PickCommand extends AbstractCommand {

    private Point location;
    private final Configuration configuration;

    public PickCommand(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void execute() {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                configuration.world.addChangeListener(new IChangeListener() {

                    public void changed() {
                        RuleBasedPixel pixel = configuration.world.get(location.x, location.y);
                        if (pixel == null) {
                            return;
                        }
                        configuration.paint.changeCurrentColor(pixel.getPixelColor());

                        if (pixel instanceof RuleBasedPixel) {
                            RuleSet ruleSet = pixel.createPickedRuleSet();
                            if (ruleSet != null) {
                                configuration.paint.changeCurrentRuleSet(ruleSet);
                            }
                        }
                    }
                });
            }
        });
    }

    public void setLocation(Point location) {
        this.location = location;

    }
}
