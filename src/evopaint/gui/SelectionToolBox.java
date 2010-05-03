/*
 *  Copyright (C) 2010 Markus Echterhoff <tam@edu.uni-klu.ac.at>,
 *                      Daniel Hoelbling (http://www.tigraine.at)
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

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import evopaint.Selection;
import evopaint.commands.DeleteCurrentSelectionCommand;
import evopaint.gui.listeners.SelectionListenerFactory;

public class SelectionToolBox extends JPanel implements Observer {

	private final Showcase showcase;

	public SelectionToolBox(Showcase showcase) {
		this.showcase = showcase;
		showcase.getCurrentSelections().addObserver(this);
		initializeComponents();
	}
	
	private void initializeComponents(){
		this.setLayout(new GridLayout(0, 1));
                setBackground(new Color(0xF2F2F5));
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		SelectionList.SelectionListEventArgs updateEvent = (SelectionList.SelectionListEventArgs) arg1;
            if (updateEvent.getChangeType() == SelectionList.ChangeType.LIST_CLEARED) {
                this.removeAll();
            }
            if (updateEvent.getChangeType() == SelectionList.ChangeType.ITEM_ADDED) {
                this.add(new SelectionWrapper(updateEvent.getSelection(), showcase));
            }
            if (updateEvent.getChangeType() == SelectionList.ChangeType.ITEM_DELETED) {
                for(int i = 0; i < this.getComponentCount() ; i++) {
                    SelectionWrapper wrapper = (SelectionWrapper)this.getComponent(i);
                    if (wrapper.getSelection() == updateEvent.getSelection()) {
                        this.remove(i);
                        break;
                    }
                }
            }

            this.revalidate();		//This does not always cause the UI to update??
            this.repaint();             // it does, but deletion needs a repaint() call too
	}
	
	private class SelectionWrapper extends JPanel implements Observer, MouseListener {

		private final Color backColor;
		private final Showcase showcase2;
		private final SelectionListenerFactory selectionListenerFactory;
		
		private JLabel selectionName;

		private final Selection selection;

		public SelectionWrapper(Selection selection, Showcase showcase){
			this.selection = selection;
			selection.addObserver(this);
			showcase2 = showcase;
			
			
			selectionName = new JLabel(selection.getSelectionName());
			this.add(selectionName);
			this.addMouseListener(this);

                        setBackground(new Color(0xF2F2F5));
			backColor = this.getBackground();
			selectionListenerFactory = new SelectionListenerFactory(showcase);
		}
		
		@Override
		public void update(Observable o, Object arg) {
			Selection selection = (Selection) o;
            UpdateName(selection);			
		}

		private void UpdateName(Selection selection) {
			selectionName.setText(selection.getSelectionName());
		}

		public Selection getSelection() {
			return selection;
		}

		@Override
		public void mouseClicked(MouseEvent arg0) {
			
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			this.selection.setHighlighted(true);
			this.setBackground(Color.LIGHT_GRAY);
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			this.selection.setHighlighted(false);
			this.setBackground(backColor);
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			if (arg0.getClickCount() == 2 && !arg0.isConsumed()) {
				selectionListenerFactory.CreateSelectionSetNameListener().actionPerformed(null);
			}else if (arg0.isPopupTrigger()) {
				showContextMenu(arg0);
			} else {
				showcase2.setActiveSelection(selection);
			}
			
		}

		private void showContextMenu(MouseEvent arg) {
			JPopupMenu menu = new JPopupMenu("Selection");
			JMenuItem deleteMenuItem = new JMenuItem("Delete");
			deleteMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					showcase.setActiveSelection(selection);
					DeleteCurrentSelectionCommand csc = new DeleteCurrentSelectionCommand(showcase);
					csc.execute();
				}
			});
			menu.add(deleteMenuItem);
			
			JMenuItem setName = new JMenuItem("Set name");
			setName.addActionListener(selectionListenerFactory.CreateSelectionSetNameListener());
			menu.add(setName);
			
			menu.show(arg.getComponent(), arg.getX(), arg.getY());
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			if (arg0.isPopupTrigger())
				showContextMenu(arg0);
		}
		
	}

}
