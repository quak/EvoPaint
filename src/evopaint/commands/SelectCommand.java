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
import evopaint.gui.util.IOverlay;
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
    private SelectionDrawingIndicatorOverlay overlay;
    private Point mouseLocation;

    public void startDragging() {
        if (overlay != null)
            canvas.subscribe(overlay);
    }

    public void stopDragging() {
        canvas.unsubscribe(overlay);
    }

    public void setStartPoint() {
        startPoint = mouseLocation;
    }

    private Point findTopLeftPoint(Point p1, Point p2, Point p3, Point p4) {
        Point left = p1;
        int coord = ToCoord(p1);
        if (ToCoord(p2) < coord) {
            left = p2;
            coord = ToCoord(p2);
        }
        if (ToCoord(p3) < coord) {
            left= p3;
            coord = ToCoord(p3);
        }
        if (ToCoord(p4) < coord) {
            left = p4;
            coord = ToCoord(p4);
        }
        return left;
    }
    private int ToCoord(Point p) {
        return p.x + p.y;
    }

    private Point findBottomRightPoint(Point p1, Point p2, Point p3, Point p4) {
        Point right = p1;
        int coord = ToCoord(p1);
        if (ToCoord(p2) > coord) {
            right = p2;
            coord = ToCoord(p2);
        }
        if (ToCoord(p3) > coord) {
            right= p3;
            coord = ToCoord(p3);
        }
        if (ToCoord(p4) > coord) {
            right = p4;
            coord = ToCoord(p4);
        }
        return right;
    }
    
    private final WrappingScalableCanvas canvas;
    private Configuration configuration;
    private Point startPoint;
    private Point leftPoint;
    private Point endPoint;
    private Point rightPoint;
    private int nextSelectionId = 0;
    private SelectionList observableSelectionList;

    public SelectCommand(SelectionList list, WrappingScalableCanvas canvas, Configuration configuration) {
        observableSelectionList = list;
        this.canvas = canvas;
        this.configuration = configuration;
    }

    public void setLocation(Point location) {
        if (startPoint == null) startPoint = location;
        mouseLocation = location;
        leftPoint = new Point(startPoint.x, mouseLocation.y);
        rightPoint = new Point(mouseLocation.x, startPoint.y);
        Point leftPoint1 = findTopLeftPoint(startPoint, leftPoint, rightPoint, mouseLocation);
        Point rightPoint1 = findBottomRightPoint(startPoint, leftPoint, rightPoint, mouseLocation);

        Rectangle bounds = new Rectangle(leftPoint1.x, leftPoint1.y, rightPoint1.x - leftPoint1.x, rightPoint1.y - leftPoint1.y);
        if (overlay == null) overlay = new SelectionDrawingIndicatorOverlay(bounds);
        overlay.setBounds(bounds);
    }

    public void execute() {
        startPoint = null;
        String s = (String) JOptionPane.showInputDialog(configuration.mainFrame, "Remember selection as:", "Save Selection", JOptionPane.PLAIN_MESSAGE, null, null, "New Selection " + nextSelectionId);
        if (s == null) return;
        Selection selection = new Selection(new Point(overlay.x, overlay.y), new Point(overlay.x + overlay.width, overlay.y + overlay.height), canvas);
        selection.setSelectionName(s);
        nextSelectionId++;
        observableSelectionList.add(selection);
    }

    private class SelectionDrawingIndicatorOverlay extends Rectangle implements IOverlay {

        @Override
        public void setBounds(Rectangle bounds) {
            super.setBounds(bounds);
            this.x = bounds.x;
            this.y = bounds.y;
            this.width = bounds.width;
            this.height = bounds.height;
        }

        public SelectionDrawingIndicatorOverlay(Rectangle bounds) {
            super(bounds);
        }

        public void paint(Graphics2D g2) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f));
            canvas.fill(this);
        }
    }
}
