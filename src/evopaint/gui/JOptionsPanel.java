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

import evopaint.Configuration;
import evopaint.commands.EraseCommand;
import evopaint.commands.FillSelectionCommand;
import evopaint.commands.MoveCommand;
import evopaint.commands.PaintCommand;
import evopaint.commands.PickCommand;
import evopaint.commands.SelectCommand;
import evopaint.commands.ZoomCommand;
import java.awt.CardLayout;
import java.awt.Color;
import javax.swing.JPanel;

/**
 *
 * @author Markus Echterhoff <tam@edu.uni-klu.ac.at>
 */
public class JOptionsPanel extends JPanel {
    private Configuration configuration;
    private JPanel currentPanel;

    public void displayOptions(Class toolClass) {

        if (currentPanel != null) {
            remove(currentPanel);
        }
        currentPanel = null;

        if (toolClass == SelectCommand.class) {

        }
        else if (toolClass == MoveCommand.class) {

        }
        else if (toolClass == ZoomCommand.class) {

        }
        else if (toolClass == PaintCommand.class) {
            currentPanel = new BrushOptionsPanel(configuration);
            add(currentPanel);
            return;
        }
        else if (toolClass == FillSelectionCommand.class) {

        }
        else if (toolClass == EraseCommand.class) {

        }
        else if (toolClass == PickCommand.class) {

        }

        revalidate();
    }

    public JOptionsPanel(Configuration configuration) {
        this.configuration = configuration;
        setBackground(new Color(0xF2F2F5));
        //setLayout(new CardLayout());

        //JPanel emptyPanel = new JPanel();
        //emptyPanel.setBackground(new Color(0xF2F2F5));
        //add(emptyPanel, "empty");

        //BrushOptionsPanel paintOptionsPanel = new BrushOptionsPanel(configuration);
        //add(paintOptionsPanel, "paint");
    }
}
