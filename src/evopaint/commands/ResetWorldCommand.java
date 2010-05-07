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

package evopaint.commands;

import evopaint.Configuration;
import evopaint.interfaces.IChangeListener;
import evopaint.pixel.rulebased.Rule;
import javax.swing.SwingUtilities;

/**
 * Command to reset the world to its original state
 * 
 * @author Markus Echterhoff <tam@edu.uni-klu.ac.at>
 */
public class ResetWorldCommand extends AbstractCommand {

    private Configuration configuration;

    public ResetWorldCommand(Configuration configuration) {
        this.configuration = configuration;
    }

    public void execute() {
        ClearSelectionCommand command = new ClearSelectionCommand(configuration.mainFrame.getShowcase());
        command.execute();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                configuration.world.addChangeListener(new IChangeListener() {

                    public void changed() {
                        configuration.world.reset();
                        configuration.usedActions.clear();
                        // re-add actions used in currently selected paint
                        if (configuration.paint.getCurrentRuleSet() != null) {
                            for (Rule rule : configuration.paint.getCurrentRuleSet().getRules()) {
                                configuration.usedActions.add(rule.getAction());
                            }
                        }
                    }
                });
            }
        });
    }

}
