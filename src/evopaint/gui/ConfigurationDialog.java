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

package evopaint.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import evopaint.Configuration;
import evopaint.gui.rulesetmanager.util.ColorChooserLabel;
import evopaint.gui.util.AutoSelectOnFocusSpinner;
import evopaint.interfaces.IChangeListener;
import evopaint.pixel.PixelColor;
import javax.swing.border.LineBorder;

/**
 * World -> Options dialog
 *
 * @author Markus Echterhoff <tam@edu.uni-klu.ac.at>
 */
public class ConfigurationDialog extends JDialog {

    private final Configuration config;
    private PixelColor backgroundColor;

    public ConfigurationDialog(Configuration config) {
        this.config = config;
        initializeComponents();
    }

    private void initializeComponents() {
        this.setTitle("Configuration");
        this.setResizable(false);

        setLayout(new BorderLayout(20, 20));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.setBorder(new LineBorder(mainPanel.getBackground(), 10));
        add(mainPanel, BorderLayout.CENTER);

        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.insets = new Insets(5, 5, 5, 5);

        GridBagConstraints inputConstraints = new GridBagConstraints();
        inputConstraints.gridx = 1;
        inputConstraints.anchor = GridBagConstraints.EAST;
        inputConstraints.weightx = 1;
        inputConstraints.insets = new Insets(5, 5, 5, 5);

        // world size
        JPanel worldSizePanel = new JPanel(new GridBagLayout());
        mainPanel.add(worldSizePanel);
        worldSizePanel.add(new JLabel("World Size"), labelConstraints);
        JPanel worldSizeInputPanel = new JPanel();
        worldSizeInputPanel.setLayout(new GridBagLayout());
        final AutoSelectOnFocusSpinner widthSpinner = new AutoSelectOnFocusSpinner(new SpinnerNumberModel(config.getDimension().width, 1, Integer.MAX_VALUE, 1));
        final AutoSelectOnFocusSpinner heightSpinner = new AutoSelectOnFocusSpinner(new SpinnerNumberModel(config.getDimension().height, 1, Integer.MAX_VALUE, 1));
        worldSizeInputPanel.add(widthSpinner);
        JLabel separatorLabel = new JLabel("x");
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 5, 0, 5);
        worldSizeInputPanel.add(separatorLabel, c);
        worldSizeInputPanel.add(heightSpinner);
        worldSizePanel.add(worldSizeInputPanel, inputConstraints);

        // mutation rate
        JPanel mutationRatePanel = new JPanel(new GridBagLayout());
        mainPanel.add(mutationRatePanel);
        mutationRatePanel.add(new JLabel("Mutation Rate"), labelConstraints);
        final AutoSelectOnFocusSpinner mutationRateSpinner = new AutoSelectOnFocusSpinner(new SpinnerNumberModel(config.mutationRate, 0, Double.MAX_VALUE, 0.001));
        mutationRatePanel.add(mutationRateSpinner, inputConstraints);

        // FPS
        JPanel fpsPanel = new JPanel(new GridBagLayout());
        mainPanel.add(fpsPanel);
        fpsPanel.add(new JLabel("FPS"), labelConstraints);
        final AutoSelectOnFocusSpinner fpsSpinner = new AutoSelectOnFocusSpinner(new SpinnerNumberModel(config.fps, 1, Integer.MAX_VALUE, 1));
        fpsPanel.add(fpsSpinner, inputConstraints);

        // background color
        JPanel backgroundColorPanel = new JPanel(new GridBagLayout());
        mainPanel.add(backgroundColorPanel, labelConstraints);
        backgroundColorPanel.add(new JLabel("Background Color"), labelConstraints);
        backgroundColor = new PixelColor(config.backgroundColor);
        JPanel backgroundColorAlignmentPanel = new JPanel();
        backgroundColorAlignmentPanel.setLayout(new GridBagLayout());
        backgroundColorAlignmentPanel.setPreferredSize(fpsSpinner.getPreferredSize());
        final ColorChooserLabel backgroundColorLabel = new ColorChooserLabel(backgroundColor);
        backgroundColorAlignmentPanel.add(backgroundColorLabel);
        backgroundColorPanel.add(backgroundColorAlignmentPanel, inputConstraints);

        // starting energy
        JPanel startingEnergyPanel = new JPanel(new GridBagLayout());
        mainPanel.add(startingEnergyPanel);
        startingEnergyPanel.add(new JLabel("Starting Energy"), labelConstraints);
        final AutoSelectOnFocusSpinner startingEnergySpinner = new AutoSelectOnFocusSpinner(new SpinnerNumberModel(config.startingEnergy, 1, Integer.MAX_VALUE, 1));
        startingEnergyPanel.add(startingEnergySpinner, inputConstraints);


        JPanel controlPanel = new JPanel();
        add(controlPanel, BorderLayout.SOUTH);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        config.world.addChangeListener(new IChangeListener() {

                            public void changed() {
                                config.setDimension(new Dimension((Integer)widthSpinner.getValue(), (Integer)heightSpinner.getValue()));
                                config.mutationRate = (Double) mutationRateSpinner.getValue();
                                config.fps = (Integer) fpsSpinner.getValue();
                                config.backgroundColor = backgroundColor.getInteger();
                                config.startingEnergy = (Integer) startingEnergySpinner.getValue();
                                dispose();
                            }
                        });
                    }
                });
            }
        });
        controlPanel.add(okButton);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        controlPanel.add(cancelButton);

        pack();
    }

}
