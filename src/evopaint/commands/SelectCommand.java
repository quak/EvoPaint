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

import evopaint.Configuration;
import evopaint.Selection;
import evopaint.gui.SelectionList;
import evopaint.gui.util.WrappingScalableCanvas;

import javax.swing.*;
import java.awt.*;

/**
 * Command to select a rectangular area on the canvas
 *
 * @author Markus Echterhoff <tam@edu.uni-klu.ac.at>
 * @author Daniel Hoelbling (http://www.tigraine.at)
 */
public class SelectCommand extends AbstractCommand {

    public enum State {

        IDLE, STARTED
    }
    private State CurrentState = State.IDLE;
    private Point mouseLocation;
    private final WrappingScalableCanvas canvas;
    private Configuration configuration;
    private Point startPoint;
    private Point endPoint;
    private int nextSelectionId = 0;
    private SelectionList observableSelectionList;

    public SelectCommand(SelectionList list, WrappingScalableCanvas canvas, Configuration configuration) {
        observableSelectionList = list;
        this.canvas = canvas;
        this.configuration = configuration;
    }

    public void setLocation(Point location) {
        mouseLocation = location;
    }

    public void execute() {
        if (CurrentState == State.IDLE) {
            startPoint = mouseLocation;
            CurrentState = State.STARTED;
        } else if (CurrentState == State.STARTED) {
            endPoint = mouseLocation;
            CurrentState = State.IDLE;
            String s = (String)JOptionPane.showInputDialog(configuration.mainFrame, "Do you want your selection with the following name?", "Save Selection", JOptionPane.PLAIN_MESSAGE, null, null, "New Selection " + nextSelectionId);
            if (s == null) return;
            SwapPoints();
            Selection selection = new Selection(startPoint, endPoint, canvas);
            selection.setSelectionName(s);
            nextSelectionId++;
            observableSelectionList.add(selection);
        }
    }

    private void SwapPoints() {
        if (startPoint.x > endPoint.x || startPoint.y > endPoint.y) {
            Point temp = endPoint;
            endPoint = startPoint;
            startPoint = temp;
        }
    }
}
