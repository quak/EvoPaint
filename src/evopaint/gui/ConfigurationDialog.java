/*
 *  Copyright (C) 2010 Daniel Hoelbling (http://www.tigraine.at),
 *                      Markus Echterhoff <tam@edu.uni-klu.ac.at>
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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.*;

import evopaint.Configuration;
import evopaint.interfaces.IChangeListener;

public class ConfigurationDialog extends JDialog {
	private static final long serialVersionUID = 8398753169918348171L;
	private final Configuration config;
	
	public ConfigurationDialog(Configuration config) {
		this.config = config;
		initializeComponents();
	}

	private void initializeComponents() {
		this.setTitle("evoPaint Configuration");
		Dimension d = new Dimension(200, 270);
		this.setPreferredSize(d);
		this.setSize(d);
        this.setResizable(false);

		Container cp = this.getContentPane();
		cp.setLayout(new BoxLayout(cp, BoxLayout.PAGE_AXIS));

		JPanel worldSize = new JPanel();
		cp.add(worldSize);
        //Worldsize
		worldSize.add(new JLabel("World Size"));
		final JFormattedTextField txtWidth = new JFormattedTextField(NumberFormat.getIntegerInstance());
		txtWidth.setText(Integer.toString(config.world.getWidth()));
		final JTextField txtHeight = new JFormattedTextField(NumberFormat.getIntegerInstance());
		txtHeight.setText(Integer.toString(config.world.getHeight()));
		worldSize.add(txtWidth);
        worldSize.add(new JLabel("x"));
		worldSize.add(txtHeight);

        JPanel mutationRate = new JPanel();
        cp.add(mutationRate);

        //Mutationrate
        mutationRate.add(new JLabel("Mutation Rate"));
        final JTextField txtMutationRate = new JFormattedTextField(NumberFormat.getNumberInstance(Locale.ENGLISH));
        txtMutationRate.setText(Double.toString(config.mutationRate));
        mutationRate.add(txtMutationRate);

        JPanel fps = new JPanel();
        cp.add(fps);

        //FPS
        fps.add(new JLabel("FPS"));
        final JTextField txtFps = new JFormattedTextField();
        txtFps.setText(Integer.toString(config.fps));
        fps.add(txtFps);

        JPanel backgroundColor = new JPanel();
        cp.add(backgroundColor);

        //BackgroundColor
        backgroundColor.add(new JLabel("Background Color"));
        final JTextField txtBackgroundColor = new JTextField();
        txtBackgroundColor.addMouseListener(new BackgroundMouseListener(txtBackgroundColor));
        setBackgroundValue(txtBackgroundColor, config.backgroundColor);
        backgroundColor.add(txtBackgroundColor);

        //StartingEnergy
        JPanel startingEnergy = new JPanel();
        cp.add(startingEnergy);

        startingEnergy.add(new JLabel("Starting Energy"));
        final JTextField txtStartingEnergy = new JTextField();
        txtStartingEnergy.setText(Integer.toString(config.startingEnergy));
        startingEnergy.add(txtStartingEnergy);


        JPanel submit = new JPanel();
        submit.setBackground(new Color(0xF2F2F5));
        cp.add(submit);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!validateInput()) return;
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        config.world.addChangeListener(new IChangeListener() {

                            public void changed() {
                                config.setDimension(new Dimension(Integer.parseInt(txtWidth.getText()), Integer.parseInt(txtHeight.getText())));
                                config.mutationRate = Double.parseDouble(txtMutationRate.getText());
                                config.fps = Integer.parseInt(txtFps.getText());
                                config.backgroundColor = txtBackgroundColor.getBackground().getRGB();
                                config.startingEnergy = Integer.parseInt(txtStartingEnergy.getText());
                                closeDialog();
                            }
                        });
                    }
                });
            }
        });
        submit.add(saveButton);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                closeDialog();
            }
        });
        submit.add(cancelButton);
	}

    private boolean validateInput() {
        return true;  //To change body of created methods use File | Settings | File Templates.
    }

    private void closeDialog() {
        this.setVisible(false);
    }
    private void setBackgroundValue(JTextField textField, int color){
        textField.setText("#" + Integer.toHexString(color));
        setForegroundColor(color, textField);
        textField.setBackground(new Color(color));
    }

    private void setForegroundColor(int color, JTextField field){
        float [] hsb = null;
        hsb = Color.RGBtoHSB((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, hsb);
        if (hsb[2] < 0.5)
            field.setForeground(Color.white);
        else
            field.setForeground(Color.black);
    }

    private class BackgroundMouseListener implements MouseListener {
        private final JTextField txtBackgroundColor;
        private JColorChooser colorChooser = new JColorChooser();

        public BackgroundMouseListener(JTextField txtBackgroundColor) {
            this.txtBackgroundColor = txtBackgroundColor;
        }

        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            colorChooser.setVisible(true);
            JDialog dialog = JColorChooser.createDialog(txtBackgroundColor, "Choose Background Color", true, colorChooser, new ColorChooserOkListener(), new ColorChooserCancelListener());
            dialog.pack();
            dialog.setVisible(true);
            //JColorChooser.createDialog(editColorBtn, "Choose Color", true,
            //        colorChooser, new ColorChooserOKListener(), new ColorChooserCancelListener());
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        private class ColorChooserOkListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Color color = colorChooser.getColor();
                setBackgroundValue(txtBackgroundColor, color.getRGB());
                pack();
            }
        }

        private class ColorChooserCancelListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //NOOP
            }
        }
    }
}
