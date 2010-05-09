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

import evopaint.Configuration;
import evopaint.commands.FillSelectionCommand;
import evopaint.gui.util.AutoSelectOnFocusSpinner;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * Options for the fill tool
 *
 * @author Markus Echterhoff <tam@edu.uni-klu.ac.at>
 * @author Daniel Hoelbling (http://www.tigraine.at)
 */
public class FillOptionsPanel extends JPanel {
    private Configuration config;

    public FillOptionsPanel(Configuration config) {
        this.config = config;

        setLayout(new GridBagLayout());
        setBackground(new Color(0xF2F2F5));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        JLabel headingLabel = new JLabel("<html><b>Fill Options</b></html>");
        constraints.insets = new Insets(5, 5, 0, 0);
        add(headingLabel, constraints);
        constraints.insets = new Insets(0, 5, 5, 5);

        // and here comes the spinner
        constraints.gridy = 2;
        JPanel panelBrushSize = new JPanel();
        panelBrushSize.setBackground(new Color(0xF2F2F5));
        add(panelBrushSize, constraints);

        // labeled
        JLabel labelForSpinner = new JLabel("Density");
        panelBrushSize.add(labelForSpinner);

        FillSelectionCommand fillCommand = config.mainFrame.getShowcase().getFillCommand();
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(fillCommand.getDensity(), 0.001, 1.0, 0.1);
        JSpinner spinnerDensity = new AutoSelectOnFocusSpinner(spinnerModel);
        spinnerDensity.addChangeListener(new SpinnerBrushSizeListener(spinnerDensity));
        labelForSpinner.setLabelFor(spinnerDensity);
        spinnerDensity.setToolTipText("Density of the fill tool. (0.5 will paint every second pixel)");
        panelBrushSize.add(spinnerDensity);
    }

    private class SpinnerBrushSizeListener implements ChangeListener {
        JSpinner spinnerBrushSize;

        public SpinnerBrushSizeListener(JSpinner spinnerBrushSize) {
            this.spinnerBrushSize = spinnerBrushSize;
        }

        public void stateChanged(ChangeEvent e) {
            config.mainFrame.getShowcase().getFillCommand().setDensity((Double)spinnerBrushSize.getValue());
        }
    }
}
