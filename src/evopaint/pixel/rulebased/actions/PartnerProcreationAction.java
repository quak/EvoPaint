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
import evopaint.gui.rulesetmanager.util.DimensionsListener;
import evopaint.gui.util.AutoSelectOnFocusSpinner;
import evopaint.interfaces.IRandomNumberGenerator;
import evopaint.pixel.ColorDimensions;
import evopaint.pixel.PixelColor;
import evopaint.pixel.rulebased.Action;
import evopaint.pixel.rulebased.RuleBasedPixel;
import evopaint.pixel.rulebased.targeting.ActionMetaTarget;
import evopaint.util.mapping.AbsoluteCoordinate;
import evopaint.util.mapping.RelativeCoordinate;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Creates a new pixel from the acting pixel and the target of this action
 * and places it into a free neighboring spot
 *
 * @author Markus Echterhoff <tam@edu.uni-klu.ac.at>
 */
public class PartnerProcreationAction extends Action {
    private int partnerEnergyChange;
    private int offspringEnergy;
    private ColorDimensions dimensions;
    private float ourShare;
    private boolean mixRules;

    public PartnerProcreationAction(int energyChange, int partnerEnergyChange, int offspringEnergy, ActionMetaTarget partner, ColorDimensions dimensions, float ourShare, boolean mixRuleSet) {
        super(energyChange, partner);
        this.partnerEnergyChange = partnerEnergyChange;
        this.offspringEnergy = offspringEnergy;
        this.dimensions = dimensions;
        this.ourShare = ourShare;
        this.mixRules = mixRuleSet;
    }

    public PartnerProcreationAction() {
        this.dimensions = new ColorDimensions(true, true, true);
        ourShare = 0.5f;
        this.mixRules = true;
    }

    public PartnerProcreationAction(PartnerProcreationAction partnerProcreationAction) {
        super(partnerProcreationAction);
        this.partnerEnergyChange = partnerProcreationAction.partnerEnergyChange;
        this.offspringEnergy = partnerProcreationAction.offspringEnergy;
        this.dimensions = partnerProcreationAction.dimensions;
        this.ourShare = partnerProcreationAction.ourShare;
        this.mixRules = partnerProcreationAction.mixRules;
    }

    public int getType() {
        return Action.PARTNER_PROCREATION;
    }

    @Override
    public int countGenes() {
        // partner energy change and mix rules are not genes, but options
        return super.countGenes() + dimensions.countGenes() + 1;
    }

    @Override
    public void mutate(int mutatedGene, IRandomNumberGenerator rng) {
        int numGenesSuper = super.countGenes();
        if (mutatedGene < numGenesSuper) {
            super.mutate(mutatedGene, rng);
            return;
        }
        mutatedGene -= numGenesSuper;

        int numGenesDimensions = dimensions.countGenes();
        if (mutatedGene < numGenesDimensions) {
            dimensions = new ColorDimensions(dimensions);
            dimensions.mutate(mutatedGene, rng);
            return;
        }
        mutatedGene -= numGenesDimensions;

        if (mutatedGene == 0) {
            ourShare = rng.nextFloat();
            return;
        }

        assert false; // we have an error in our mutatedGene calculation
    }

    @Override
    public void mixWith(Action theirAction, float theirShare, IRandomNumberGenerator rng) {
        super.mixWith(theirAction, theirShare, rng);

        PartnerProcreationAction a = (PartnerProcreationAction)theirAction;

        dimensions = new ColorDimensions(dimensions);
        dimensions.mixWith(a.dimensions, theirShare, rng);

        if (rng.nextFloat() < theirShare) {
            partnerEnergyChange = a.partnerEnergyChange;
        }

        if (rng.nextFloat() < theirShare) {
            ourShare = a.ourShare;
        }
    }

    public ColorDimensions getDimensions() {
        return dimensions;
    }

    public void setDimensions(ColorDimensions dimensionsToMix) {
        this.dimensions = dimensionsToMix;
    }

    public float getOurShare() {
        return ourShare;
    }

    public void setOurShare(float ourShare) {
        this.ourShare = ourShare;
    }

    public String getName() {
        return "procreate with partner";
    }

    public int execute(RuleBasedPixel actor, RelativeCoordinate direction, Configuration configuration) {
        RuleBasedPixel partner = configuration.world.get(actor.getLocation(), direction);
        if (partner == null) {
            return 0;
        }

        if (partner.getEnergy() + partnerEnergyChange < 0) {
            return 0;
        }

        AbsoluteCoordinate randomFreeSpot =
                configuration.world.getRandomFreeNeighborCoordinateOf(
                actor.getLocation(), configuration.rng);

        if (randomFreeSpot == null) {
            return 0;
        }

        RuleBasedPixel newPixel = null;
        if (mixRules) {
            newPixel = new RuleBasedPixel(actor);
            newPixel.mixWith(partner, 1 - ourShare, configuration.rng);
            if (configuration.rng.nextDouble() <= configuration.mutationRate) {
                newPixel.mutate(configuration);
            }
        }
        else {
            newPixel = new RuleBasedPixel(actor, actor.getRules());
            newPixel.setPixelColor(new PixelColor(actor.getPixelColor()));
            newPixel.getPixelColor().mixWith(partner.getPixelColor(), 1 - ourShare, dimensions);
            if (configuration.rng.nextDouble() <= configuration.mutationRate) {
                newPixel.getPixelColor().mutate(configuration.rng);
            }
        }
        newPixel.setLocation(randomFreeSpot);
        newPixel.setEnergy(offspringEnergy);
        configuration.world.set(newPixel);

        partner.changeEnergy(partnerEnergyChange);

        return energyChange;
    }

    @Override
    public Map<String, String>addParametersString(Map<String, String> parametersMap) {
        parametersMap = super.addParametersString(parametersMap);
        if (partnerEnergyChange > 0) {
            parametersMap.put("partner's reward", Integer.toString(partnerEnergyChange));
        }
        else if (partnerEnergyChange < 0) {
            parametersMap.put("partner's cost", Integer.toString((-1) * partnerEnergyChange));
        }
        parametersMap.put("offspring's starting-energy", Integer.toString(offspringEnergy));
        parametersMap.put("dimensions", dimensions.toString());
        parametersMap.put("our share in %", Integer.toString(Math.round(ourShare * 100)));
        parametersMap.put("mode", "color " + (mixRules ? "and rules" : "only"));
        return parametersMap;
    }

    @Override
    public Map<String, String>addParametersHTML(Map<String, String> parametersMap) {
        parametersMap = super.addParametersHTML(parametersMap);
        if (partnerEnergyChange > 0) {
            parametersMap.put("partner's reward", Integer.toString(partnerEnergyChange));
        }
        else if (partnerEnergyChange < 0) {
            parametersMap.put("partner's cost", Integer.toString((-1) * partnerEnergyChange));
        }
        parametersMap.put("offspring's starting-energy", Integer.toString(offspringEnergy));
        parametersMap.put("dimensions", dimensions.toHTML());
        parametersMap.put("our share in %", Integer.toString(Math.round(ourShare * 100)));
        parametersMap.put("mode", "color " + (mixRules ? "and rules" : "only"));
        return parametersMap;
    }

    @Override
    public LinkedHashMap<String,JComponent> addParametersGUI(LinkedHashMap<String, JComponent> parametersMap) {
        parametersMap = super.addParametersGUI(parametersMap);

        SpinnerNumberModel partnerEnergyModel = new SpinnerNumberModel(partnerEnergyChange, Integer.MIN_VALUE, Integer.MAX_VALUE, 1);
        JSpinner partnerEnergyChangeSpinner = new AutoSelectOnFocusSpinner(partnerEnergyModel);
        partnerEnergyChangeSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                partnerEnergyChange = (Integer) ((JSpinner) e.getSource()).getValue();
            }
        });
        parametersMap.put("Partner's Energy Change", partnerEnergyChangeSpinner);

        SpinnerNumberModel offspringEnergyModel = new SpinnerNumberModel(offspringEnergy, 0, Integer.MAX_VALUE, 1);
        JSpinner offspringEnergySpinner = new AutoSelectOnFocusSpinner(offspringEnergyModel);
        offspringEnergySpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                offspringEnergy = (Integer) ((JSpinner) e.getSource()).getValue();
            }
        });
        parametersMap.put("Offspring's Starting-Energy", offspringEnergySpinner);

        JPanel dimensionsPanel = new JPanel();
        JToggleButton btnH = new JToggleButton("H");
        btnH.setToolTipText("Select to include the hue in color-mixing");
        JToggleButton btnS = new JToggleButton("S");
        btnS.setToolTipText("Select to include the saturation in color-mixing");
        JToggleButton btnB = new JToggleButton("B");
        btnB.setToolTipText("Select to include the brightness in color-mixing");
        DimensionsListener dimensionsListener = new DimensionsListener(dimensions, btnH, btnS, btnB);
        btnH.addActionListener(dimensionsListener);
        btnS.addActionListener(dimensionsListener);
        btnB.addActionListener(dimensionsListener);
        if (dimensions.hue) {
            btnH.setSelected(true);
        }
        if (dimensions.saturation) {
            btnS.setSelected(true);
        }
        if (dimensions.brightness) {
            btnB.setSelected(true);
        }
        dimensionsPanel.add(btnH);
        dimensionsPanel.add(btnS);
        dimensionsPanel.add(btnB);
        parametersMap.put("Dimensions", dimensionsPanel);

        SpinnerNumberModel ourSharePercentSpinnerModel =
                new SpinnerNumberModel(Math.round(ourShare * 100), 0, 100, 1);
        JSpinner ourSharePercentSpinner =
                new AutoSelectOnFocusSpinner(ourSharePercentSpinnerModel);
        ourSharePercentSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ourShare =
                        ((Integer)((JSpinner)e.getSource()).getValue()).floatValue() / 100;
            }
        });
        ourSharePercentSpinner.setToolTipText("<html>Defines how many percent of the acting pixel's genes shall be injected into the target.<br />(eg. setting this to 80 will make the target 80% like this pixel and leave 20% as they are)</html>");
        parametersMap.put("Our share in %", ourSharePercentSpinner);

        final JCheckBox mixRuleSetCheckBox = new JCheckBox();
        mixRuleSetCheckBox.setSelected(mixRules);
        mixRuleSetCheckBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                mixRules = mixRuleSetCheckBox.isSelected();
            }
        });
        mixRuleSetCheckBox.setToolTipText("If checked, the actor's and target's color and rule set will be mixed. If unchecked, only color will be mixed.");
        parametersMap.put("Mix rules", mixRuleSetCheckBox);

        return parametersMap;
    }
}
