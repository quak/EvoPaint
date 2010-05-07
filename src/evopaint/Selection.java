/*
 *  Copyright (C) 2010 Markus Echterhoff <tam@edu.uni-klu.ac.at>,
 *                      Daniel Hoelbling (http://www.tigraine.at)
 *                      
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

package evopaint;

import java.awt.*;
import java.util.Observable;

import evopaint.gui.HighlightedSelectionOverlay;
import evopaint.gui.util.IOverlay;
import evopaint.gui.util.WrappingScalableCanvas;

/**
 * Represents a selection of pixels on the canvas
 *
 * @author Daniel Hoelbling (http://www.tigraine.at)
 * @author Markus Echterhoff <tam@edu.uni-klu.ac.at>
 */
public class Selection extends Observable implements IOverlay {
	private Point startPoint;
	private Point endPoint;
	private String selectionName;
	private boolean highlighted;
	private final WrappingScalableCanvas canvas;
	private HighlightedSelectionOverlay overlay;
	private Rectangle rect;
	
        /**
         * Constructor
         *
         * @param startPoint The origin of the selection-box drag in user space
         * @param endPoint The destination of the selection-box drag in user space
         * @param canvas The canvas that is painted on
         */
        public Selection(Point startPoint, Point endPoint,
			WrappingScalableCanvas canvas) {
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.rect = new Rectangle(startPoint, new Dimension(endPoint.x - startPoint.x, endPoint.y - startPoint.y));
		this.canvas = canvas;
		overlay = new HighlightedSelectionOverlay(this, canvas);
	}

        /**
         * (Un-)subscribes this selection to be called back whenever the canvas
         * is painted
         *
         * @param highlighted true if selection is to be highlighted, false
         *      otherwise
         */
        public void setHighlighted(boolean highlighted) {
		if (highlighted) {
			canvas.subscribe(overlay);
		} else {
			canvas.unsubscribe(overlay);
		}
		this.highlighted = highlighted;
	}	

        public String getSelectionName() {
		return selectionName;
	}

        public void setSelectionName(String selectionName) {
		this.selectionName = selectionName;
		setChanged();
		notifyObservers();
	}

        public Point getStartPoint() {
		return startPoint;
	}

        public Point getEndPoint() {
		return endPoint;
	}

        public boolean isHighlighted() {
		return highlighted;
	}
	
        public Rectangle getRectangle() {
		return rect;
	}

	public void paint(Graphics2D g2) {
		g2.setXORMode(new Color(0x505050));

		canvas.draw(new Rectangle(this.getStartPoint(), new Dimension(this
				.getEndPoint().x
				- this.getStartPoint().x, this.getEndPoint().y
				- this.getStartPoint().y)));
	}
}
