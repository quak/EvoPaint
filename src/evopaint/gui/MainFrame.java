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
import evopaint.commands.*;
import evopaint.gui.listeners.SelectionListenerFactory;
import evopaint.gui.rulesetmanager.JRuleSetManager;
import evopaint.pixel.rulebased.RuleSet;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.LineBorder;

/**
 *
 * @author Markus Echterhoff <tam@edu.uni-klu.ac.at>
 * @author Daniel Hoelbling (http://www.tigraine.at)
 * @author Augustin Malle
 */
public class MainFrame extends JFrame {
    private Container contentPane;
    private JPanel mainPanel;
    private MenuBar menuBar;
    private Showcase showcase;
    private JScrollPane showCaseScrollPane;
    private JOptionsPanel jOptionsPanel;
    private ToolBox toolBox;
    private SelectionToolBox selectionToolBox;
    private PaintOptionsPanel paintPanel;
    private JPanel leftPanel;
    private JRuleSetManager jRuleSetManager;
    private Configuration configuration;

    private Class activeTool = null;
    private ResumeCommand resumeCommand;
    private PauseCommand pauseCommand;
    private ZoomCommand zoomOutCommand;
    private ZoomCommand zoomInCommand;

    private int runLevelBeforeRuleSetManager;

    public Configuration getConfiguration() {
        return configuration;
    }

    public MainFrame(final Configuration configuration) {
        this.configuration = configuration;
        this.contentPane = getContentPane();

        setTitle("EvoPaint");

        resumeCommand = new ResumeCommand(configuration);
        pauseCommand = new PauseCommand(configuration);
        zoomInCommand = new ZoomInCommand(showcase);
        zoomOutCommand = new ZoomOutCommand(showcase);

        CommandFactory commandFactory = new CommandFactory(configuration);

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ee) {
                // mu!
            }
        }

        List<Image> iconList = new ArrayList<Image>();
        iconList.add(getToolkit().getImage(getClass().getResource("icons/application-16.png")));
        iconList.add(getToolkit().getImage(getClass().getResource("icons/application-22.png")));
        iconList.add(getToolkit().getImage(getClass().getResource("icons/application-32.png")));
        iconList.add(getToolkit().getImage(getClass().getResource("icons/application-48.png")));
        iconList.add(getToolkit().getImage(getClass().getResource("icons/application-64.png")));
        setIconImages(iconList);

        ToolTipManager.sharedInstance().setInitialDelay(600);
        ToolTipManager.sharedInstance().setReshowDelay(300);
        ToolTipManager.sharedInstance().setDismissDelay(5*60*1000);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setLayout(new CardLayout());

        this.mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        add(mainPanel, "main");
        this.jRuleSetManager = new JRuleSetManager(configuration,
                new RuleSetManagerOKListener(), new RuleSetManagerCancelListener());
        jRuleSetManager.setVisible(false);
        add(jRuleSetManager, "rule manager");

        this.jOptionsPanel = new JOptionsPanel(configuration);
        this.showcase = new Showcase(configuration, commandFactory);
        this.menuBar = new MenuBar(configuration, new SelectionListenerFactory(showcase), showcase);
        setJMenuBar(menuBar);

        addKeyListener(new MainFrameKeyListener());

        GridBagConstraints cMainPanel = new GridBagConstraints();
        cMainPanel.anchor = GridBagConstraints.NORTH;

        // some hand crafted GUI stuff for the panel on the left
        leftPanel = new JPanel();
        leftPanel.setLayout(new GridBagLayout());
        mainPanel.add(leftPanel, cMainPanel);

        GridBagConstraints constraintsLeftPanel = new GridBagConstraints();
        constraintsLeftPanel.anchor = GridBagConstraints.CENTER;
        constraintsLeftPanel.fill = GridBagConstraints.HORIZONTAL;
        constraintsLeftPanel.insets = new Insets(8, 10, 8, 10);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3, 3, 3, 3);
        c.anchor = GridBagConstraints.NORTHWEST;

        JPanel playerWrapperPanel = new JPanel();
        playerWrapperPanel.setLayout(new GridBagLayout());
        //playerWrapperPanel.setBackground(new Color(0xF2F2F5));
        //playerWrapperPanel.setBorder(new LineBorder(Color.GRAY));

        JEvolutionPlayerPanel evolutionPlayer = new JEvolutionPlayerPanel(configuration);
        playerWrapperPanel.add(evolutionPlayer, c);
        leftPanel.add(playerWrapperPanel, constraintsLeftPanel);

        JPanel toolWrapperPanel = new JPanel();
        toolWrapperPanel.setLayout(new GridBagLayout());
        toolWrapperPanel.setBackground(new Color(0xF2F2F5));
        toolWrapperPanel.setBorder(new LineBorder(Color.GRAY));


        constraintsLeftPanel.gridy = 1;
        leftPanel.add(toolWrapperPanel, constraintsLeftPanel);

        toolBox = new ToolBox(this);
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 0, 0);

        toolWrapperPanel.add(toolBox, c);
        
        JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
        c.gridy++;
        c.fill = GridBagConstraints.HORIZONTAL;
        toolWrapperPanel.add(separator, c);

        c.gridy++;
        c.fill = GridBagConstraints.NONE;
        toolWrapperPanel.add(jOptionsPanel, c);

        JPanel paintWrapperPanel = new JPanel();
        paintWrapperPanel.setLayout(new GridBagLayout());
        paintWrapperPanel.setBackground(new Color(0xF2F2F5));
        paintWrapperPanel.setBorder(new LineBorder(Color.GRAY));

        paintPanel = new PaintOptionsPanel(configuration, new OpenRuleSetManagerListener());
        c.gridy = 0;
        c.fill = GridBagConstraints.NONE;
        paintWrapperPanel.add(paintPanel, c);

        constraintsLeftPanel.gridy = 2;
        leftPanel.add(paintWrapperPanel, constraintsLeftPanel);

        JPanel selectionWrapperPanel = new JPanel();
        selectionWrapperPanel.setLayout(new GridBagLayout());
        selectionWrapperPanel.setBackground(new Color(0xF2F2F5));
        selectionWrapperPanel.setBorder(new LineBorder(Color.GRAY));

        GridBagConstraints cSelectionLabel = new GridBagConstraints(); // not at all clean, but fastest to implement solution atm
        cSelectionLabel.anchor = GridBagConstraints.NORTHWEST;
        cSelectionLabel.weightx = 1;
        cSelectionLabel.fill = GridBagConstraints.HORIZONTAL;
        cSelectionLabel.gridx = 0;
        cSelectionLabel.gridy = 0;
        cSelectionLabel.insets = new Insets(5, 8, 0, 5);
        selectionWrapperPanel.add(new JLabel("<html><b>Selections</b></html>"), cSelectionLabel);

        selectionToolBox = new SelectionToolBox(showcase);
        //selectionToolBox.setPreferredSize(new Dimension(200, 200));
        JScrollPane scroller = new JScrollPane(selectionToolBox,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.setViewportBorder(null);
        scroller.setBorder(null);
        scroller.setMinimumSize(new Dimension(100, 100));
        scroller.setPreferredSize(new Dimension(leftPanel.getPreferredSize().width - 20, 120));

        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        selectionWrapperPanel.add(scroller, c);

        constraintsLeftPanel.gridy = 3;
        leftPanel.add(selectionWrapperPanel, constraintsLeftPanel);

        JPanel wrapperPanelRight = new JPanel();
        wrapperPanelRight.setBackground(Color.WHITE);
        wrapperPanelRight.setLayout(new GridBagLayout());
        wrapperPanelRight.add(showcase);
        // and the right side
        showCaseScrollPane = new JScrollPane(wrapperPanelRight,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        // THIS IS NOT OBJECT ORIENTED! IT TOOK HOURS TO FIND THIS SHIT!
        showCaseScrollPane.setViewportBorder(null); 
        // showCaseScrollPane.getViewport().setBackground(new JPanel().getBackground());
        // ^ does not seem to work for viewports workaround v

        // not needed, but don't want to forget about: showCaseScrollPane.getViewport().setOpaque(false);

        // and if we do not set a null border we get a nice 1px solix black border "for free"
        // the JScrollPane is by far the most unpolished Swing component I have seen so far
        // edit: well that changed. SynthUI and JTree is my new favorite combo
        showCaseScrollPane.setBorder(new LineBorder(Color.GRAY));

        showCaseScrollPane.getViewport().addMouseWheelListener(showcase);

        cMainPanel.gridx = 1;
        cMainPanel.weightx = 1;
        cMainPanel.weighty = 1;
        cMainPanel.fill = GridBagConstraints.BOTH;
        mainPanel.add(showCaseScrollPane, cMainPanel);

        setPreferredSize(new Dimension(900, 800));

        this.pack();
        this.setVisible(true);

        toolBox.setInitialFocus();
    }

    public ToolBox getToolBox() {
        return toolBox;
    }

    public Class getActiveTool() {
        return activeTool;
    }

    public void setActiveTool(Class activeTool) {
        this.activeTool = activeTool;
        jOptionsPanel.displayOptions(activeTool);
    }

    public Showcase getShowcase() {
        return showcase;
    }

    public void setShowcase(Showcase showcase) {
        this.showcase = showcase;
    }

    public void resize() {
        this.pack();
    }

    public void setConfiguration(Configuration conf) {
        this.configuration = conf;
    }

    private class MainFrameKeyListener implements KeyListener {

        public void keyTyped(KeyEvent e) {
            // System.out.println(""+e.getK)
            //System.out.println("adsf");
            //throw new UnsupportedOperationException("Not supported yet.");
        }

        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_PLUS) {
                zoomInCommand.execute();
            } else if (e.getKeyCode() == KeyEvent.VK_MINUS) {
                zoomOutCommand.execute();
            } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                if (configuration.runLevel < Configuration.RUNLEVEL_RUNNING) {
                    pauseCommand.execute();
                } else {
                    resumeCommand.execute();
                }
            }
        }

        public void keyReleased(KeyEvent e) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private class RuleSetManagerOKListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            RuleSet ruleSet = jRuleSetManager.getSelectedRuleSet();
            assert (ruleSet != null);
            configuration.paint.changeCurrentRuleSet(ruleSet);
            ((CardLayout)contentPane.getLayout()).show(contentPane, "main");
            menuBar.setVisible(true);
            showCaseScrollPane.requestFocusInWindow();
            configuration.runLevel = runLevelBeforeRuleSetManager;
        }

    }

    private class RuleSetManagerCancelListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            ((CardLayout)contentPane.getLayout()).show(contentPane, "main");
            menuBar.setVisible(true);
            showCaseScrollPane.requestFocusInWindow();
            configuration.runLevel = runLevelBeforeRuleSetManager;
        }

    }

    private class OpenRuleSetManagerListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            ((CardLayout)contentPane.getLayout()).show(contentPane, "rule manager");
            menuBar.setVisible(false);
            runLevelBeforeRuleSetManager = configuration.runLevel;
            configuration.runLevel = Configuration.RUNLEVEL_STOP;
        }
    }
}
