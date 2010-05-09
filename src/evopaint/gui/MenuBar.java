/*
 *  Copyright (C) 2010 Markus Echterhoff <tam@edu.uni-klu.ac.at>,
 *                      Daniel Hoelbling (http://www.tigraine.at),
 *                      Augustin Malle
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
import evopaint.Selection;
import evopaint.commands.*;
import evopaint.gui.listeners.SelectionListenerFactory;
import evopaint.interfaces.IChangeListener;
import evopaint.util.ExceptionHandler;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Observable;
import java.util.Observer;

/**
 * The MenuBar of EvoPaint
 * 
 * @author Markus Echterhoff <tam@edu.uni-klu.ac.at>
 * @author Daniel Hoelbling (http://www.tigraine.at)
 * @author Augustin Malle
 */
public class MenuBar extends JMenuBar implements Observer {

    private Configuration configuration;
    private Showcase showcase;
    private JMenu selectionMenu;
    private JMenu activeSelections;
    private MenuBar mb;

    public MenuBar(final Configuration configuration, SelectionListenerFactory listenerFactory, Showcase showcase) {
        this.configuration = configuration;
        this.showcase = showcase;
        this.mb = this;

        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        showcase.getCurrentSelections().addObserver(this);
        // World Menu
        JMenu worldMenu = new JMenu();
        worldMenu.setText("World");
        add(worldMenu, c);

        // File Menu Items        
        JMenuItem newItem = new JMenuItem();
        newItem.setText("New");
        newItem.addActionListener(new NewWorldCommand(configuration));
        worldMenu.add(newItem);

        JMenuItem open = new JMenuItem("Open...");
        open.addActionListener(new LoadCommand(configuration));
        worldMenu.add(open);
        JMenuItem save = new JMenuItem("Save");
        save.addActionListener(new SaveCommand(configuration));
        worldMenu.add(save);
        JMenuItem saveAs = new JMenuItem("Save as...");
        saveAs.addActionListener(new SaveAsCommand(configuration));
        worldMenu.add(saveAs);
        JMenuItem importMenu = new JMenuItem("Import...");
        importMenu.addActionListener(showcase.getImportCommand());
        worldMenu.add(importMenu);

        JMenuItem exportItem = new JMenuItem();
        exportItem.setText("Export");
        exportItem.addActionListener(new ExportDialog(configuration));

        worldMenu.add(exportItem);

        JMenuItem opt = new JMenuItem("Options...");
        opt.addActionListener(new ShowConfigurationDialogCommand(configuration));
        worldMenu.add(opt);

        JMenuItem endItem = new JMenuItem();
        endItem.setText("End");
        endItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (JOptionPane.showConfirmDialog(getRootPane(), "Do you really want to Exit?", "Exit EvoPaint", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
        worldMenu.add(endItem);

        // selection menu
        selectionMenu = new JMenu("Selection");
        c.gridx = 1;
        add(selectionMenu, c);


        JMenuItem selectAll = new JMenuItem("Select All");
        selectAll.addActionListener(new SelectAllCommand(showcase, configuration));
        selectionMenu.add(selectAll);

        JMenuItem selectNone = new JMenuItem("Select None");
        selectNone.addActionListener(new SelectNoneCommand(configuration));
        selectionMenu.add(selectNone);

        JMenuItem selectionSetName = new JMenuItem("Set Name...");
        selectionMenu.add(selectionSetName);
        selectionSetName.addActionListener(listenerFactory.CreateSelectionSetNameListener());
//        JMenuItem fillSelection = new JMenuItem("Fill");
//        fillSelection.addActionListener(new FillSelectionCommand(showcase));
//        selectionMenu.add(fillSelection);
//        JMenuItem fillHalfSelection = new JMenuItem("Fill 50%");
//        fillHalfSelection.addActionListener(new FillSelectionCommandScattered(showcase));
//        selectionMenu.add(fillHalfSelection);
        selectionMenu.add(new JMenuItem("Open as new"));
        JMenuItem copySelection = new JMenuItem("Copy");
        copySelection.addActionListener(showcase.getCopySelectionCommand());
        selectionMenu.add(copySelection);
        activeSelections = new JMenu("Selections");
        JMenuItem deleteCurrentSelection = new JMenuItem("Delete current");
        selectionMenu.add(deleteCurrentSelection);
        deleteCurrentSelection.addActionListener(new DeleteCurrentSelectionCommand(showcase));
        selectionMenu.add(activeSelections);
        JMenuItem clearSelections = new JMenuItem("Clear Selections");
        clearSelections.addActionListener(listenerFactory.CreateClearSelectionsListener());
        selectionMenu.add(clearSelections);

        // info menu
        JMenu infoMenu = new JMenu();
        infoMenu.setText("Info");
        c.gridx = 2;
        add(infoMenu, c);

        JMenuItem userGuide = new JMenuItem();
        userGuide.setText("User Guide");
        userGuide.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                //.getDesktop().browse(new URI("http://www.your.url"));
                try {
                    java.awt.Desktop.getDesktop().browse(new URI(Configuration.USER_GUIDE_URL));
                } catch (IOException e1) {
                    ExceptionHandler.handle(e1, false);
                } catch (URISyntaxException e1) {
                    ExceptionHandler.handle(e1, false);
                }
            }
        });
        infoMenu.add(userGuide);

        JMenuItem getCode = new JMenuItem();
        getCode.setText("Get the source code");
        getCode.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    java.awt.Desktop.getDesktop().browse(new URI(Configuration.CODE_DOWNLOAD_URL));
                } catch (URISyntaxException e1) {
                    ExceptionHandler.handle(e1, false);
                } catch (IOException e1) {
                    ExceptionHandler.handle(e1, false);
                }
            }
        });
        infoMenu.add(getCode);

        JMenuItem about = new JMenuItem();
        about.setText("About");
        about.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JAboutDialog aboutDialog = new JAboutDialog(configuration.mainFrame);
                aboutDialog.pack();
                aboutDialog.setVisible(true);
            }
        });
        infoMenu.add(about);



        final JMenu modeMenu = new JMenu("Mode: Agent Simulation");

        JRadioButtonMenuItem menuRadioAgentSimulation = new JRadioButtonMenuItem("Agent Simulation", true);
        menuRadioAgentSimulation.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                configuration.world.addChangeListener(new IChangeListener() {

                    public void changed() {
                        configuration.operationMode = Configuration.OPERATIONMODE_AGENT_SIMULATION;
                        modeMenu.setText("Mode: Agent Simulation");
                        configuration.world.reset();
                    }
                });
            }
        });
        menuRadioAgentSimulation.setToolTipText("<html>During each time frame each pixel will act once.<br />"
                + "Pixels act in a different, random order each time frame.<br />"
                + "The effects of an action will be seen by all pixels immediately.<br />"
                + "So during a single time frame the pixel who acts first can influence the<br />"
                + "descision of a neighbor acting after him or even remove the neighbor alltogether.</html>");
        modeMenu.add(menuRadioAgentSimulation);

        JRadioButtonMenuItem menuRadioCellularAutomaton = new JRadioButtonMenuItem("Cellular Automaton", false);
        menuRadioCellularAutomaton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                configuration.world.addChangeListener(new IChangeListener() {

                    public void changed() {
                        configuration.operationMode = Configuration.OPERATIONMODE_CELLULAR_AUTOMATON;
                        modeMenu.setText("Mode: Cellular Automaton");
                        configuration.world.reset();
                    }
                });
            }
        });
        menuRadioCellularAutomaton.setToolTipText("<html>Each time frame consists of a snapshot of the world.<br />"
                + "Each pixel can then change itself once according to its environment.<br />"
                + "The changed pixels are used to construct the subsequent snapshot.<br />"
                + "Please note that for increased performance there are no restrictions in place that would<br />"
                + "prevent you from modifying neighbors but doing so will not yield the desired results.<br />"
                + "(Unintentional SE-directed patterns would be created due to the lack of randomization)</html>");
        modeMenu.add(menuRadioCellularAutomaton);

        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(menuRadioAgentSimulation);
        modeGroup.add(menuRadioCellularAutomaton);

        c.gridx = 3;
        c.weightx = 1;
        c.anchor = GridBagConstraints.EAST;
        add(modeMenu, c);

    }
    /*
    public void addSelection(Selection selection) {
    activeSelections.add(new SelectionWrapper(selection, showcase));
    }*/

    public void update(Observable o, Object arg) {
        SelectionList.SelectionListEventArgs eventEvent = (SelectionList.SelectionListEventArgs) arg;
        if (eventEvent.getChangeType() == SelectionList.ChangeType.LIST_CLEARED) {
            activeSelections.removeAll();
        }
        if (eventEvent.getChangeType() == SelectionList.ChangeType.ITEM_ADDED) {
            activeSelections.add(new SelectionWrapper(eventEvent.getSelection(), showcase));
        }
        if (eventEvent.getChangeType() == SelectionList.ChangeType.ITEM_DELETED) {
            for (int i = 0; i < activeSelections.getItemCount(); i++) {
                SelectionWrapper wrapper = (SelectionWrapper) activeSelections.getItem(i);
                if (wrapper.selection == eventEvent.getSelection()) {
                    activeSelections.remove(i);
                    break;
                }
            }
        }
    }

    private class SelectionWrapper extends JMenuItem implements Observer {

        private Selection selection;
        private SelectionManager selectionManager;

        private SelectionWrapper(Selection selection, SelectionManager manager) {
            selectionManager = manager;
            selection.addObserver(this);
            this.selection = selection;
            UpdateName(selection);
            addMouseListener(new SelectionMouseListener());
        }

        public void update(Observable o, Object arg) {
            Selection sel = (Selection) o;
            UpdateName(sel);
        }

        private void UpdateName(Selection selection) {
            this.setText(selection.getSelectionName());
        }

        private class SelectionMouseListener implements MouseListener {

            public void mouseClicked(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
                selectionManager.setActiveSelection(selection);
            }

            public void mouseEntered(MouseEvent e) {
                selection.setHighlighted(true);
            }

            public void mouseExited(MouseEvent e) {
                selection.setHighlighted(false);
            }
        }
    }
}
