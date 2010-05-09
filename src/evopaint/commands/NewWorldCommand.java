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
import evopaint.EvoPaint;

/**
 * Command to create a new world
 * 
 * @author Markus Echterhoff <tam@edu.uni-klu.ac.at>
 */
public class NewWorldCommand extends AbstractCommand {
    private Configuration configuration;

    public NewWorldCommand(Configuration configuration) {
        this.configuration = configuration;
    }

    public void execute() {
        Thread t = new Thread() {

            @Override
            public void run() {
                EvoPaint evoPaint = new EvoPaint();
                evoPaint.work();
            }
        };
        t.start();
    }

}

