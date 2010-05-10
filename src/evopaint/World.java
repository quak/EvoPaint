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

package evopaint;

import com.thoughtworks.xstream.annotations.XStreamOmitField;
import evopaint.interfaces.IChanging;
import evopaint.interfaces.IChangeListener;
import evopaint.pixel.rulebased.RuleBasedPixel;

import evopaint.util.mapping.ParallaxMap;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

/**
 * The world of EvoPaint. Represents the universe the system development of
 * EvoPaint takes place in. It is derived from ParallaxMap, so its surface is
 * endless. Other than space it also manages the time. You can think of a call
 * to step() as the speed of light.
 *
 * @author Markus Echterhoff <tam@edu.uni-klu.ac.at>
 */
public class World extends ParallaxMap<RuleBasedPixel> implements IChanging {

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @XStreamOmitField
    private Configuration configuration;
    @XStreamOmitField
    private List<IChangeListener> pendingOperations = new ArrayList();

    /**
     * Creates a new World
     *
     * @param configuration the configuration to use for this world
     */
    public World(Configuration configuration) {
        super(new RuleBasedPixel [
                configuration.getDimension().width * configuration.getDimension().height],
                configuration.getDimension().width, configuration.getDimension().height);
        this.configuration = configuration;
    }

    /**
     * Updates the dimension of the world. If the new dimensions are smaller
     * than the current ones, the world will be cut off by the neccessary amount
     * on the right and bottom hand respectively. If the new dimensions are
     * bigger, the world will be padded by empty space at the right and bottom
     * edges.
     *
     * @param dimension The new dimensions
     */
    public void setDimension(Dimension dimension) {
        RuleBasedPixel [] data = getData();
        RuleBasedPixel [] newData = new RuleBasedPixel[
                dimension.width * dimension.height];

        int minHeight = Math.min(height, dimension.height);
        int minWidth = Math.min(width, dimension.width);

        for (int y = 0; y < minHeight; y++) {
            for (int x = 0; x < minWidth; x++) {
                newData[y * dimension.width + x] = data[y * width + x];
            }
        }
        
        setData(newData);
        width = dimension.width;
        height = dimension.height;
        recount();
    }

    /**
     * Let every pixel do stuff. This is the smallest possible time slice in
     * the universe of EvoPaint.
     */
    public void step() {

        if (pendingOperations.size() > 0) {
            synchronized(pendingOperations) { // synched because the awt thread adds elements and this one deletes
                for (IChangeListener listener : pendingOperations) {
                    listener.changed();
                }
                pendingOperations.clear();
            }
        }

        if (configuration.runLevel != Configuration.RUNLEVEL_RUNNING) {
            // if painting, return so we can paint
            if (configuration.runLevel == Configuration.RUNLEVEL_PAINTING_ONLY) {
                return;
            }
            // if stopped, sleep
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
            }
            return;
        }

        if (configuration.operationMode == Configuration.OPERATIONMODE_AGENT_SIMULATION) {
            this.stepAgents();
        } else {
            this.stepCellularAutomaton();
        }
    }

    public void set(RuleBasedPixel pixel) {
        super.set(pixel.getLocation().x, pixel.getLocation().y, pixel);
    }

    private void stepAgents() {
        int [] indices = getShuffledIndices(configuration.rng);
        
        for (int i = 0; i < indices.length; i++) {
            RuleBasedPixel pixie = getUnclamped(indices[i]);
            if (pixie.isAlive()) {                  // only act when alive
                pixie.act(this.configuration);
            }
            if (false == pixie.isAlive()) {     // immediate removal on death
                set(indices[i], null);
            }
        }
    }

    private void stepCellularAutomaton() {
        RuleBasedPixel [] currentData = getData();
        RuleBasedPixel [] newData = new RuleBasedPixel [currentData.length];

        for (int i = 0; i < currentData.length; i++) {
            RuleBasedPixel pixie = currentData[i];
            if (pixie != null) {
                RuleBasedPixel oldPixie = new RuleBasedPixel(pixie);
                pixie.act(this.configuration);
                newData[i] = pixie;
                currentData[i] = oldPixie;
            }
        }
        
        setData(newData);
    }

    /**
     * Implements IChanging
     *
     * @param subscriber
     */
    public void addChangeListener(IChangeListener subscriber) {
        synchronized(pendingOperations) {
            pendingOperations.add(subscriber);
        }
    }

    /**
     * Implements IChanging
     *
     * @param subscriber
     */
    public void removeChangeListener(IChangeListener subscriber) {
        assert (false); // should not be called since pending operations are cleared automatically
    }

    public void resetPendingOperations() {
        pendingOperations = new ArrayList();
    }
}
