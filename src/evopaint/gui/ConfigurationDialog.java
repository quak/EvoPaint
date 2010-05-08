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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

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
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
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

        JPanel evolutionOptionsPanel = new JPanel();
        evolutionOptionsPanel.setBorder(new TitledBorder("Evolution"));
        evolutionOptionsPanel.setLayout(new BoxLayout(evolutionOptionsPanel, BoxLayout.Y_AXIS));
        mainPanel.add(evolutionOptionsPanel);

        // world size
        JPanel worldSizePanel = new JPanel(new GridBagLayout());
        evolutionOptionsPanel.add(worldSizePanel);
        JLabel worldSizeLabel = new JLabel("World Size");
        worldSizeLabel.setToolTipText("Size of the world in pixels. Width x Height.");
        worldSizePanel.add(worldSizeLabel, labelConstraints);
        JPanel worldSizeInputPanel = new JPanel();
        worldSizeInputPanel.setLayout(new GridBagLayout());
        final AutoSelectOnFocusSpinner worldSizeWidthSpinner = 
                new AutoSelectOnFocusSpinner(new SpinnerNumberModel(
                config.getDimension().width, 1, Integer.MAX_VALUE, 1));
        final AutoSelectOnFocusSpinner worldSizeHeightSpinner = 
                new AutoSelectOnFocusSpinner(new SpinnerNumberModel(
                config.getDimension().height, 1, Integer.MAX_VALUE, 1));
        worldSizeInputPanel.add(worldSizeWidthSpinner);
        JLabel separatorLabel = new JLabel("x");
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 5, 0, 5);
        worldSizeInputPanel.add(separatorLabel, c);
        worldSizeInputPanel.add(worldSizeHeightSpinner);
        worldSizePanel.add(worldSizeInputPanel, inputConstraints);

        // starting energy
        JPanel startingEnergyPanel = new JPanel(new GridBagLayout());
        evolutionOptionsPanel.add(startingEnergyPanel);
        JLabel startingEnergyLabel = new JLabel("Starting Energy");
        startingEnergyLabel.setToolTipText("<html>The amount of energy all newly painted pixels start out with.<br />Note that changing this will not affect already painted pixels.</html>");
        startingEnergyPanel.add(startingEnergyLabel, labelConstraints);
        final AutoSelectOnFocusSpinner startingEnergySpinner = 
                new AutoSelectOnFocusSpinner(new SpinnerNumberModel(
                config.startingEnergy, 1, Integer.MAX_VALUE, 1));
        startingEnergyPanel.add(startingEnergySpinner, inputConstraints);

        // mutation rate
        JPanel mutationRatePanel = new JPanel(new GridBagLayout());
        evolutionOptionsPanel.add(mutationRatePanel);
        JLabel mutationRateLabel = new JLabel("Mutation Rate");
        mutationRateLabel.setToolTipText("<html>Chance for a mutation to occur during copy or mixing operations.<br />A value of 0.01 means a 1% chance, a value of 0 disables mutation.</html>");
        mutationRatePanel.add(mutationRateLabel, labelConstraints);
        final JSpinner mutationRateSpinner =
                new JSpinner(new SpinnerNumberModel(
                config.mutationRate, 0.0, 1.0, 0.01));
        mutationRateSpinner.setEditor(new JSpinner.NumberEditor(mutationRateSpinner, "#.##############"));
        mutationRateSpinner.setPreferredSize(new Dimension(
                180,
                mutationRateSpinner.getPreferredSize().height));
        final JFormattedTextField mutationRateSpinnerText =
                ((JSpinner.NumberEditor)mutationRateSpinner.getEditor()).getTextField();
        mutationRateSpinnerText.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            mutationRateSpinnerText.selectAll();
                        }
                });
            }
            public void focusLost(FocusEvent e) {}
        });
        mutationRatePanel.add(mutationRateSpinner, inputConstraints);

        JPanel guiOptionsPanel = new JPanel();
        guiOptionsPanel.setBorder(new TitledBorder("GUI"));
        guiOptionsPanel.setLayout(new BoxLayout(guiOptionsPanel, BoxLayout.Y_AXIS));
        mainPanel.add(guiOptionsPanel);

        // background color
        JPanel backgroundColorPanel = new JPanel(new GridBagLayout());
        guiOptionsPanel.add(backgroundColorPanel, labelConstraints);
        JLabel backgroundColorLabel = new JLabel("Background Color");
        backgroundColorLabel.setToolTipText("<html>Pick the background color.<br />If you have a lot of dark pixels, you might want to set this to a light color.</html>");
        backgroundColorPanel.add(backgroundColorLabel, labelConstraints);
        backgroundColor = new PixelColor(config.backgroundColor);
        JPanel backgroundColorAlignmentPanel = new JPanel();
        backgroundColorAlignmentPanel.setLayout(new GridBagLayout());
        backgroundColorAlignmentPanel.setPreferredSize(
                mutationRateSpinner.getPreferredSize());
        final ColorChooserLabel backgroundColorChooserLabel =
                new ColorChooserLabel(backgroundColor);
        backgroundColorAlignmentPanel.add(backgroundColorChooserLabel);
        backgroundColorPanel.add(backgroundColorAlignmentPanel, inputConstraints);

        // FPS
        JPanel fpsPanel = new JPanel(new GridBagLayout());
        guiOptionsPanel.add(fpsPanel);
        JLabel fpsLabel = new JLabel("FPS");
        fpsLabel.setToolTipText("<html>The repaint interval in frames per second (FPS).<br />Set this to a lower value to save a few CPU cycles.</html>");
        fpsPanel.add(fpsLabel, labelConstraints);
        final AutoSelectOnFocusSpinner fpsSpinner = new
                AutoSelectOnFocusSpinner(new SpinnerNumberModel(
                config.fps, 1, Integer.MAX_VALUE, 1));
        fpsPanel.add(fpsSpinner, inputConstraints);

        // paint history size
        JPanel paintHistorySizePanel = new JPanel(new GridBagLayout());
        guiOptionsPanel.add(paintHistorySizePanel);
        JLabel paintHistorySizeLabel = new JLabel("Paint-History Size");
        paintHistorySizeLabel.setToolTipText("<html>Sets the number of entries in the paint history.<br />In case you have not found it yet: the paint history is the little menu that appears when you right-click on the image.</html>");
        paintHistorySizePanel.add(paintHistorySizeLabel, labelConstraints);
        final AutoSelectOnFocusSpinner paintHistorySizeSpinner = 
                new AutoSelectOnFocusSpinner(new SpinnerNumberModel(
                config.paintHistorySize, 1, Integer.MAX_VALUE, 1));
        paintHistorySizePanel.add(paintHistorySizeSpinner, inputConstraints);

        JPanel videoOptionsPanel = new JPanel();
        videoOptionsPanel.setBorder(new TitledBorder("Video Recording"));
        videoOptionsPanel.setLayout(new BoxLayout(videoOptionsPanel, BoxLayout.Y_AXIS));
        mainPanel.add(videoOptionsPanel);

        // FPS video
        JPanel fpsVideoPanel = new JPanel(new GridBagLayout());
        videoOptionsPanel.add(fpsVideoPanel);
        JLabel fpsVideoLabel = new JLabel("FPS of videos");
        fpsVideoLabel.setToolTipText("The number of frames per second (FPS) in recorded videos.");
        fpsVideoPanel.add(fpsVideoLabel, labelConstraints);
        final AutoSelectOnFocusSpinner fpsVideoSpinner = 
                new AutoSelectOnFocusSpinner(new SpinnerNumberModel(
                config.fpsVideo, 1, Integer.MAX_VALUE, 1));
        fpsVideoPanel.add(fpsVideoSpinner, inputConstraints);

        // video encoder command
        JPanel videoEncoderPanel = new JPanel(new GridBagLayout());
        videoOptionsPanel.add(videoEncoderPanel);
        JLabel videoEncoderLabel = new JLabel("Video Encoder");
        videoEncoderLabel.setToolTipText("<html>The command to invoke your video encoder.<br />Use the tokens INPUT_FILE and OUTPUT_FILE to denote input and output files of your encoder.</html>");
        videoEncoderPanel.add(videoEncoderLabel, labelConstraints);
        final JTextArea videoEncoderTextArea = new JTextArea(Configuration.ENCODER_COMMAND);
        videoEncoderTextArea.setLineWrap(true);
        videoEncoderTextArea.setWrapStyleWord(true);
        JScrollPane videoEncoderScrollPane = new JScrollPane(videoEncoderTextArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        videoEncoderScrollPane.setViewportBorder(null);
        videoEncoderScrollPane.setPreferredSize(new Dimension(
                worldSizeInputPanel.getPreferredSize().width, 100));
        videoEncoderPanel.add(videoEncoderScrollPane, inputConstraints);


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
                                int i, j;
                                double d;
                                String s;

                                // evolution
                                
                                i = (Integer)worldSizeWidthSpinner.getValue();
                                j = (Integer)worldSizeHeightSpinner.getValue();
                                if (config.getDimension().width != i ||
                                        config.getDimension().height != i) {
                                    config.setDimension(new Dimension(i, j));
                                }

                                i = (Integer) startingEnergySpinner.getValue();
                                if (config.startingEnergy != i) {
                                    config.startingEnergy = i;
                                }

                                d = (Double) mutationRateSpinner.getValue();
                                if (config.mutationRate != d) {
                                    config.mutationRate = d;
                                }

                                // gui

                                i = backgroundColor.getInteger();
                                if (config.backgroundColor != i) {
                                    config.backgroundColor = i;
                                }

                                i = (Integer) fpsSpinner.getValue();
                                if (config.fps != i) {
                                    config.fps = i;
                                }

                                i = (Integer) paintHistorySizeSpinner.getValue();
                                if (config.paintHistorySize != i) {
                                    config.paintHistorySize = i;
                                }

                                // video recording

                                i = (Integer) fpsVideoSpinner.getValue();
                                if (config.fpsVideo != i) {
                                    config.fpsVideo = i;
                                }

                                s = videoEncoderTextArea.getText();
                                if (false == Configuration.ENCODER_COMMAND.equals(s)) {
                                    Configuration.ENCODER_COMMAND = s;
                                }
                                
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
