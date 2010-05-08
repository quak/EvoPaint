package evopaint.gui;

import evopaint.Configuration;
import evopaint.commands.FillSelectionCommand;
import evopaint.gui.util.AutoSelectOnFocusSpinner;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 08.05.2010
 * Time: 20:30:04
 * To change this template use File | Settings | File Templates.
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
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(fillCommand.getDensity(), 1, 10, 1);
        JSpinner spinnerBrushSize = new AutoSelectOnFocusSpinner(spinnerModel);
        spinnerBrushSize.addChangeListener(new SpinnerBrushSizeListener(spinnerBrushSize));
        labelForSpinner.setLabelFor(spinnerBrushSize);
        spinnerBrushSize.setToolTipText("Size of your brush in square pixels");
        panelBrushSize.add(spinnerBrushSize);
    }

    private class SpinnerBrushSizeListener implements ChangeListener {
        JSpinner spinnerBrushSize;

        public SpinnerBrushSizeListener(JSpinner spinnerBrushSize) {
            this.spinnerBrushSize = spinnerBrushSize;
        }

        public void stateChanged(ChangeEvent e) {
            config.mainFrame.getShowcase().getFillCommand().setDensity((Integer)spinnerBrushSize.getValue());
        }
    }
}
