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


import evopaint.gui.util.WrappingScalableCanvas;

/**
 * command to zoom either in or out
 * 
 * @author Markus Echterhoff <tam@edu.uni-klu.ac.at>
 * @author Daniel Hoelbling (http://www.tigraine.at)
 */
public class ZoomCommand extends AbstractCommand {
    private boolean zoomIn;
    private WrappingScalableCanvas canvas;

    protected ZoomCommand(WrappingScalableCanvas canvas, boolean zoomIn) {
        this.zoomIn = zoomIn;
        this.canvas = canvas;
    }

    public void execute() {
         if (this.zoomIn) {
            this.canvas.scaleUp();
        } else {
            this.canvas.scaleDown();
        }
    }
}

