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

package evopaint.pixel.rulebased.actions;

import evopaint.Configuration;
import evopaint.gui.util.AutoSelectOnFocusSpinner;
import evopaint.pixel.rulebased.Action;
import evopaint.pixel.rulebased.RuleBasedPixel;
import evopaint.pixel.rulebased.targeting.ActionMetaTarget;
import evopaint.util.mapping.AbsoluteCoordinate;
import evopaint.util.mapping.RelativeCoordinate;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Markus Echterhoff <tam@edu.uni-klu.ac.at>
 */
public class CopyAction extends Action {

    private int copyEnergy;

    public CopyAction(int energyChange, ActionMetaTarget target) {
        super(energyChange, target);
    }

    public int getType() {
        return Action.COPY;
    }

    public CopyAction() {
    }

    public CopyAction(CopyAction copyAction) {
        super(copyAction);
    }

    public String getName() {
        return "copy";
    }

    public int execute(RuleBasedPixel actor, RelativeCoordinate direction, Configuration configuration) {
        RuleBasedPixel target = configuration.world.get(actor.getLocation(), direction);
        if (target != null) {
            return 0;
        }

        RuleBasedPixel newPixel = new RuleBasedPixel(actor);
        newPixel.setLocation(new AbsoluteCoordinate(actor.getLocation(), direction, configuration.world));
        newPixel.setEnergy(copyEnergy);
        configuration.world.set(newPixel);

        if (configuration.rng.nextDouble() <= configuration.mutationRate) {
            newPixel.mutate(configuration);
        }

        return energyChange;
    }

    @Override
    public Map<String, String>addParametersString(Map<String, String> parametersMap) {
        parametersMap = super.addParametersString(parametersMap);

        parametersMap.put("starting-energy of copy", Integer.toString(copyEnergy));

        return parametersMap;
    }

    @Override
    public Map<String, String>addParametersHTML(Map<String, String> parametersMap) {
        parametersMap = super.addParametersHTML(parametersMap);

        parametersMap.put("starting-energy of copy", Integer.toString(copyEnergy));

        return parametersMap;
    }

    @Override
    public LinkedHashMap<String,JComponent> addParametersGUI(LinkedHashMap<String, JComponent> parametersMap) {
        parametersMap = super.addParametersGUI(parametersMap);


        SpinnerNumberModel copyEnergyModel = new SpinnerNumberModel(copyEnergy, 0, Integer.MAX_VALUE, 1);
        JSpinner copyEnergySpinner = new AutoSelectOnFocusSpinner(copyEnergyModel);
        copyEnergySpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                copyEnergy = (Integer) ((JSpinner) e.getSource()).getValue();
            }
        });
        parametersMap.put("Starting-Energy Of Copy", copyEnergySpinner);



        return parametersMap;
    }
    
}
