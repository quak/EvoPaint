package evopaint.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;

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
		worldSize.add(new JLabel("World Size"));
		JTextField txtWidth = new JTextField();
		txtWidth.setText(Integer.toString(config.world.getWidth()));
		JTextField txtHeight = new JTextField();
		txtHeight.setText(Integer.toString(config.world.getHeight()));
		worldSize.add(txtWidth);
		worldSize.add(txtHeight);
	}
}
