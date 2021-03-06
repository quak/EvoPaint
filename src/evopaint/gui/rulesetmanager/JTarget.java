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

package evopaint.gui.rulesetmanager;

import evopaint.pixel.rulebased.targeting.ITarget;
import evopaint.pixel.rulebased.targeting.MetaTarget;
import evopaint.pixel.rulebased.targeting.SingleTarget;
import evopaint.util.ImageRotator;
import evopaint.util.mapping.RelativeCoordinate;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

/**
 * A Panel which holds buttons for the acting pixel and its moore-neighborhood
 *
 * @author Markus Echterhoff <tam@edu.uni-klu.ac.at>
 */
public class JTarget extends JPanel {

    private List<RelativeCoordinate> directions;
    private IdentityHashMap<JToggleButton,RelativeCoordinate> buttonsDirections;
    private boolean neighborsToggled;

    public int numSelected() {
        int ret = 0;
        for (JToggleButton b : buttonsDirections.keySet()) {
            ret += b.isSelected() ? 1 : 0;
        }
        return ret;
    }

    public ITarget createTarget() {
        if (directions.size() > 1) {
            return new MetaTarget(directions);
        }
        if (directions.size() == 0) {
            return null;
        }
        return new SingleTarget(directions.get(0));
    }

    public JTarget(ITarget target, ActionListener buttonListener) {
        this(target);
        for (JToggleButton b : buttonsDirections.keySet()) {
            b.addActionListener(buttonListener);
        }
    }

    public JTarget(ITarget target) {
        if (target instanceof MetaTarget) {
            this.directions = ((MetaTarget)target).getDirections();
        } else {
            this.directions = new ArrayList<RelativeCoordinate>();
            RelativeCoordinate direction = ((SingleTarget)target).getDirection();

            if (direction != null) {
                this.directions.add(direction);
            }
        }
        setLayout(new GridBagLayout());

        JPanel directionsPanel = new JPanel();
        directionsPanel.setLayout(new GridLayout(3, 3, 3, 3));
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTH;
        add(directionsPanel, c);

        JButton neighborsButton = new JButton("neighbors");
        neighborsButton.addActionListener(new AllNeighborsButtonListener());
        c.gridy = 1;
        c.insets = new Insets(5, 0, 0, 0);
        add(neighborsButton, c);

        buttonsDirections = new IdentityHashMap<JToggleButton, RelativeCoordinate>();

        ImageIcon protoIconNorth = new ImageIcon(getClass().getResource("icons/target-north.png"));

        JToggleButton btn = new JToggleButton();
        btn.setIcon(ImageRotator.createRotatedImage(btn, protoIconNorth, 315));
        directionsPanel.add(btn);
        buttonsDirections.put(btn, RelativeCoordinate.NORTH_WEST);

        btn = new JToggleButton();
        btn.setIcon(protoIconNorth);
        directionsPanel.add(btn);
        buttonsDirections.put(btn, RelativeCoordinate.NORTH);

        btn = new JToggleButton();
        btn.setIcon(ImageRotator.createRotatedImage(btn, protoIconNorth, 45));
        directionsPanel.add(btn);
        buttonsDirections.put(btn, RelativeCoordinate.NORTH_EAST);

        btn = new JToggleButton();
        btn.setIcon(ImageRotator.createRotatedImage(btn, protoIconNorth, 270));
        directionsPanel.add(btn);
        buttonsDirections.put(btn, RelativeCoordinate.WEST);

        btn = new JToggleButton();
        btn.setIcon(new ImageIcon(getClass().getResource("icons/target-self.png")));
        directionsPanel.add(btn);
        buttonsDirections.put(btn, RelativeCoordinate.CENTER);

        btn = new JToggleButton();
        btn.setIcon(ImageRotator.createRotatedImage(btn, protoIconNorth, 90));
        directionsPanel.add(btn);
        buttonsDirections.put(btn, RelativeCoordinate.EAST);

        btn = new JToggleButton();
        btn.setIcon(ImageRotator.createRotatedImage(btn, protoIconNorth, 225));
        directionsPanel.add(btn);
        buttonsDirections.put(btn, RelativeCoordinate.SOUTH_WEST);

        btn = new JToggleButton();
        btn.setIcon(ImageRotator.createRotatedImage(btn, protoIconNorth, 180));
        directionsPanel.add(btn);
        buttonsDirections.put(btn, RelativeCoordinate.SOUTH);

        btn = new JToggleButton();
        btn.setIcon(ImageRotator.createRotatedImage(btn, protoIconNorth, 135));
        directionsPanel.add(btn);
        buttonsDirections.put(btn, RelativeCoordinate.SOUTH_EAST);

        for (JToggleButton b : buttonsDirections.keySet()) {
            b.setPreferredSize(new Dimension(30, 30));
            b.setMaximumSize(b.getPreferredSize());
            b.setMinimumSize(b.getPreferredSize());
            b.addActionListener(new TargetActionListener());
            for (RelativeCoordinate rc : directions) {
                if (rc == this.buttonsDirections.get(b)) {
                   b.setSelected(true);
                   break;
                }
            }
        }
    }

    private class TargetActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            JToggleButton actionButton = (JToggleButton)e.getSource();
            RelativeCoordinate actionCoordinate = buttonsDirections.get(actionButton);
            if (actionButton.isSelected()) {
                if (directions.contains(actionCoordinate) == false) {
                    directions.add(actionCoordinate);
                }
            } else {
                directions.remove(actionCoordinate);
            }
        }
    }

    private class AllNeighborsButtonListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            boolean containedCenter = false;
            if (directions.contains(RelativeCoordinate.CENTER)) {
                containedCenter = true;
            }

            List<RelativeCoordinate> allNeighbors = new ArrayList<RelativeCoordinate>() {{
                add(RelativeCoordinate.NORTH);
                add(RelativeCoordinate.NORTH_EAST);
                add(RelativeCoordinate.EAST);
                add(RelativeCoordinate.SOUTH_EAST);
                add(RelativeCoordinate.SOUTH);
                add(RelativeCoordinate.SOUTH_WEST);
                add(RelativeCoordinate.WEST);
                add(RelativeCoordinate.NORTH_WEST);
            }};

            if (directions.containsAll(allNeighbors)) {
                neighborsToggled = true;
            }

            directions.clear();

            if (containedCenter) {
                directions.add(RelativeCoordinate.CENTER);
            }

            if (neighborsToggled == false) {
                directions.addAll(allNeighbors);
            }

            for (JToggleButton b : buttonsDirections.keySet()) {
                if (buttonsDirections.get(b) == RelativeCoordinate.CENTER) {
                    continue;
                }
                if (neighborsToggled == false && b.isSelected() == false) {
                    b.setSelected(true);
                    ActionEvent ae = new ActionEvent((Object)b, ActionEvent.ACTION_PERFORMED, "");
                    for (ActionListener a : b.getActionListeners()) {
                        a.actionPerformed(ae);
                    }
                }
                else if (neighborsToggled && b.isSelected()) {
                    b.setSelected(false);
                    ActionEvent ae = new ActionEvent((Object)b, ActionEvent.ACTION_PERFORMED, "");
                    for (ActionListener a : b.getActionListeners()) {
                        a.actionPerformed(ae);
                    }
                }
            }
            neighborsToggled = !neighborsToggled;
        }
    }
}
