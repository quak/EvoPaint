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

package evopaint.gui.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

/**
 *
 * @author Markus Echterhoff <tam@edu.uni-klu.ac.at>
 */
public class JProgressDialog extends JDialog {
    private String action;
    private JTextArea messagePane;
    private JProgressBar progressbar;
    private JButton okButton;

    public JProgressDialog(String action, String additionalInformation) {
        this.action = action;

        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 10, 10, 10);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        messagePane = new JTextArea();
        messagePane.setEditable(false);
        messagePane.setBorder(new LineBorder(new JPanel().getBackground(), 10));
        messagePane.setText("I am working hard to " + action + ".\n\n" + additionalInformation);
        messagePane.setColumns(30);
        messagePane.setLineWrap(true);
        messagePane.setWrapStyleWord(true);
        messagePane.setOpaque(false);
        messagePane.setBackground(new Color(0,0,0,0)); // workaround because nimbus gives a shit about opaque backgrounds see http://stackoverflow.com/questions/613603/java-nimbus-laf-with-transparent-text-fields
        messagePane.setPreferredSize(new Dimension(300, 150));
        messagePane.setAlignmentX(LEFT_ALIGNMENT);
        add(messagePane, c);

        progressbar = new JProgressBar();
        progressbar.setIndeterminate(true);
        c.gridy = 1;
        add(progressbar, c);

        JPanel controlPanel = new JPanel();
        okButton = new JButton("Wait for it... wait for it...");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        okButton.setEnabled(false);
        controlPanel.add(okButton);
        c.gridy = 2;
        add(controlPanel, c);
    }

    public void done() {
        messagePane.setText("I managed to successfully " + action + ". Yay, me!");
        progressbar.setIndeterminate(false);
        progressbar.setValue(100);
        okButton.setText("Thanks");
        okButton.setEnabled(true);
    }

}
