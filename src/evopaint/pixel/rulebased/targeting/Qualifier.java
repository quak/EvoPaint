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

package evopaint.pixel.rulebased.targeting;

import evopaint.Configuration;
import evopaint.pixel.Pixel;
import evopaint.pixel.rulebased.interfaces.INamed;
import evopaint.util.mapping.RelativeCoordinate;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Markus Echterhoff <tam@edu.uni-klu.ac.at>
 */
public abstract class Qualifier implements INamed, Serializable {

    public RelativeCoordinate qualify(Pixel actor, List<RelativeCoordinate> directions, Configuration configuration) {
        List<RelativeCoordinate> qualifyingDirections = getCandidates(actor, directions, configuration);
        if (qualifyingDirections.size() == 0) {
            return null;
        }
        if (qualifyingDirections.size() == 1) {
            return qualifyingDirections.get(0);
        }
        return qualifyingDirections.get(configuration.rng.nextPositiveInt(qualifyingDirections.size()));
    }
    
    public abstract List<RelativeCoordinate> getCandidates(Pixel actor, List<RelativeCoordinate> directions, Configuration configuration);
}