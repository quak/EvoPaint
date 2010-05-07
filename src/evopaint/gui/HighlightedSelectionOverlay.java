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

package evopaint.gui;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import evopaint.Selection;
import evopaint.gui.util.IOverlay;
import evopaint.gui.util.WrappingScalableCanvas;

/**
 * Overlay for a highlighted selection. This class will draw the outline of
 * the current selection onto the canvas.
 *
 * @author Markus Echterhoff <tam@edu.uni-klu.ac.at>
 * @author Daniel Hoelbling (http://www.tigraine.at)
 */
public class HighlightedSelectionOverlay implements IOverlay {

    private WrappingScalableCanvas canvas;
    private Rectangle rect;

    public HighlightedSelectionOverlay(Selection selection, WrappingScalableCanvas canvas) {
        this.canvas = canvas;

        rect = new Rectangle(selection.getStartPoint(),
                new Dimension(selection.getEndPoint().x - selection.getStartPoint().x, selection.getEndPoint().y - selection.getStartPoint().y));
    }

    @Override
    public void paint(Graphics2D g2) {
        g2.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, .5f));

        canvas.draw(rect);
    }
}
