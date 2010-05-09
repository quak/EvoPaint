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

import evopaint.pixel.rulebased.RuleSet;
import evopaint.pixel.rulebased.RuleSetCollection;
import evopaint.util.CollectionNode;
import evopaint.util.ExceptionHandler;
import evopaint.util.RuleSetNode;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * A drag and drop implementation of a JTree, designed for EvoPaint, not fit
 * for general use, based on
 * http://www.coderanch.com/t/346509/Swing-AWT-SWT-JFace/java/JTree-drag-drop-inside-one
 * 
 * @author Markus Echterhoff <tam@edu.uni-klu.ac.at>
 */
public class RuleSetDragDropTree extends JTree {

    public RuleSetDragDropTree() {
        setDragEnabled(true);
        setDropMode(DropMode.ON);
        setTransferHandler(new TreeTransferHandler());
        getSelectionModel().setSelectionMode(
                TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
    }

}

class TreeTransferHandler extends TransferHandler {

    DataFlavor nodeFlavor;
    RuleSetNode original;

    public TreeTransferHandler() {
        try {
            String mimeType = DataFlavor.javaJVMLocalObjectMimeType
                    + ";class=\""
                    + RuleSetNode.class.getName()
                    + "\"";
            nodeFlavor = new DataFlavor(mimeType);
        } catch (ClassNotFoundException e) {
            ExceptionHandler.handle(e, true);
        }
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        if (!support.isDrop()) {
            return false;
        }

        support.setShowDropLocation(true);
        if (!support.isDataFlavorSupported(nodeFlavor)) {
            return false;
        }

        JTree.DropLocation dl =
                (JTree.DropLocation) support.getDropLocation();
        JTree tree = (JTree) support.getComponent();
       
        TreePath dest = dl.getPath();
        if (dest == null) {
            return false;
        }

        // forbid moving collection nodes because they will be reordered on restart anyways
        if (((DefaultMutableTreeNode)tree.getSelectionPath().getLastPathComponent()).getAllowsChildren()) {
            return false;
        }

        // and forbid rule set nodes to be moved onto anything but collection nodes
        DefaultMutableTreeNode target = (DefaultMutableTreeNode) dest.getLastPathComponent();
        if (false == target instanceof CollectionNode) {
            return false;
        }

        // and forbid rule set nodes to be moved into the collection they originate from
        if (target == ((DefaultMutableTreeNode)tree.getSelectionPath().getLastPathComponent()).getParent()) {
            return false;
        }
     
        return true;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        JTree tree = (JTree) c;
        TreePath path = tree.getSelectionPath();
        if (path == null) {
            return null;
        }

        if (false == path.getLastPathComponent() instanceof RuleSetNode) {
            return null;
        }

        original = (RuleSetNode)path.getLastPathComponent();
        RuleSetNode copy = copy(original);
        return new NodesTransferable(copy);
    }

    /** Defensive copy used in createTransferable. Deep copy because of name updates */
    private RuleSetNode copy(RuleSetNode node) {
        RuleSet ruleSet = (RuleSet) node.getUserObject();
        RuleSet copiedRuleSet = null;
        try {
            ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(outByteStream);
            out.writeObject(ruleSet);
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(outByteStream.toByteArray()));
            copiedRuleSet = (RuleSet) in.readObject();
        } catch (ClassNotFoundException ex) {
            ExceptionHandler.handle(ex, true);
        } catch (IOException ex) {
            ExceptionHandler.handle(ex, true);
        }
        return new RuleSetNode(copiedRuleSet);
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        if ((action & MOVE) == MOVE) {
            JTree tree = (JTree) source;
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            model.removeNodeFromParent(original);
        }
    }

    @Override
    public int getSourceActions(JComponent c) {
        return MOVE;
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }

        // Extract transfer data.
        RuleSetNode ruleSetNode = null;
        try {
            Transferable t = support.getTransferable();
            ruleSetNode = (RuleSetNode) t.getTransferData(nodeFlavor);
        } catch (UnsupportedFlavorException ex) {
            ExceptionHandler.handle(ex, true);
        } catch (java.io.IOException ex) {
            ExceptionHandler.handle(ex, true);
        }

        // Get drop location info.
        JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
        TreePath dest = dl.getPath();
        CollectionNode collectionNode =
                (CollectionNode) dest.getLastPathComponent();
        JTree tree = (JTree) support.getComponent();
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();

        // make sure the name of the copy is unique
        RuleSet ruleSet = (RuleSet)ruleSetNode.getUserObject();
        String originalName = ruleSet.getName().replaceAll(" *\\(\\d+\\)", "");

        ruleSet.setName(originalName);
        boolean found = false;
        Enumeration siblingRuleSetNodes = collectionNode.children();
        while (siblingRuleSetNodes.hasMoreElements()) {
            RuleSetNode node = (RuleSetNode)
                    siblingRuleSetNodes.nextElement();
            RuleSet rs = (RuleSet)node.getUserObject();
            if (rs.getName().equals(ruleSet.getName())) {
                found = true;
                break;
            }
        }
        if (found == true) {
            for (int i = 1; found == true; i++) {
                ruleSet.setName(originalName + " (" + i + ")");
                found = false;
                siblingRuleSetNodes = collectionNode.children();
                while (siblingRuleSetNodes.hasMoreElements()) {
                    RuleSetNode node = (RuleSetNode)
                            siblingRuleSetNodes.nextElement();
                    RuleSet rs = (RuleSet)node.getUserObject();
                    if (rs.getName().equals(ruleSet.getName())) {
                        found = true;
                        break;
                    }
                }
            }
        }

        // Add data to model.
        model.insertNodeInto(ruleSetNode, collectionNode, collectionNode.getChildCount());

        return true;
    }

    public class NodesTransferable implements Transferable {

        RuleSetNode ruleSetNode;

        public NodesTransferable(RuleSetNode ruleSetNode) {
            this.ruleSetNode = ruleSetNode;
        }

        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return ruleSetNode;
        }

        public DataFlavor[] getTransferDataFlavors() {
            DataFlavor[] flavors = { nodeFlavor };
            return flavors;
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return nodeFlavor.equals(flavor);
        }
    }
}
