/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package evopaint.pixel;

import evopaint.World;
import evopaint.pixel.interfaces.ICondition;
import evopaint.util.mapping.AbsoluteCoordinate;
import evopaint.util.mapping.RelativeCoordinate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author tam
 */
public abstract class AbstractPixelCondition implements ICondition {

    private List<RelativeCoordinate> directions;
    
    @Override
    public String toString() {
        String ret = "[";
        for (Iterator<RelativeCoordinate> ii = directions.iterator(); ii.hasNext();) {
            ret += ii.next().toString();
            if (ii.hasNext()) {
                ret += " & ";
            }
        }
        ret += "]";
        return ret;
    }

    public abstract boolean isMet(Pixel us, World world);

    public List<RelativeCoordinate> getDirections() {
        return directions;
    }

    public AbstractPixelCondition(List<RelativeCoordinate> directions) {
        this.directions = directions;
    }
}