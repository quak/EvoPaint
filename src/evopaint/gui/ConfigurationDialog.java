package evopaint.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;

import evopaint.Configuration;

public class ConfigurationDialog extends JDialog {
	private static final long serialVersionUID = 8398753169918348171L;
	private final Configuration config;
	
	public ConfigurationDialog(Configuration config) {
		this.config = config;
		initializeComponents();
	}

	private void initializeComponents() {
		this.setTitle("evoPaint Configuration");
		Dimension d = new Dimension(400, 400);
		this.setPreferredSize(d);
		this.setSize(d);

		Container cp = this.getContentPane();
		cp.setLayout(new FlowLayout());

		JPanel worldSize = new JPanel();
		cp.add(worldSize);
        //Worldsize
		worldSize.add(new JLabel("World Size"));
		JTextField txtWidth = new JTextField();
		txtWidth.setText(Integer.toString(config.world.getWidth()));
		JTextField txtHeight = new JTextField();
		txtHeight.setText(Integer.toString(config.world.getHeight()));
		worldSize.add(txtWidth);
		worldSize.add(txtHeight);

        JPanel mutationRate = new JPanel();
        cp.add(mutationRate);

        //Mutationrate
        mutationRate.add(new JLabel("Mutation Rate"));
        JTextField txtMutationRate = new JTextField();
        txtMutationRate.setText(Double.toString(config.mutationRate));
        mutationRate.add(txtMutationRate);

        JPanel fps = new JPanel();
        cp.add(fps);

        //FPS
        fps.add(new JLabel("FPS"));
        JTextField txtFps = new JTextField();
        txtFps.setText(Integer.toString(config.fps));
        fps.add(txtFps);

        JPanel backgroundColor = new JPanel();
        cp.add(backgroundColor);

        //BackgroundColor
        backgroundColor.add(new JLabel("Background Color"));
        JTextField txtBackgroundColor = new JTextField();
        txtBackgroundColor.addMouseListener(new BackgroundMouseListener(txtBackgroundColor));
        setBackgroundValue(txtBackgroundColor, config.backgroundColor);
        backgroundColor.add(txtBackgroundColor);
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
