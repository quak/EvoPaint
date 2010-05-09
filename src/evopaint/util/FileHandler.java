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

package evopaint.util;

import evopaint.Configuration;
import evopaint.pixel.rulebased.RuleSet;
import evopaint.pixel.rulebased.RuleSetCollection;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.util.Enumeration;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * Handles any files EvoPaint stores on the disk. The file handler is also
 * informed about tree changes in the rule set browser, so that any
 * modifications made by the user are saved automatically
 *
 * @author Markus Echterhoff <tam@edu.uni-klu.ac.at>
 */
public class FileHandler implements TreeModelListener {

    private File homeDir;
    private File collectionsDir;

    public File getHomeDir() {
        return homeDir;
    }

    public File getCollectionsDir() {
        return collectionsDir;
    }

    public synchronized DefaultTreeModel readCollections() {
        File [] collectionDirs = collectionsDir.listFiles();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        DefaultTreeModel model = new DefaultTreeModel(root, true);

        for (File collectionDir : collectionDirs) {
            if (false == collectionDir.isDirectory()) {
                collectionDir.delete();
            }
            
            File metadataFile = new File(collectionDir, "metadata.epc");
            RuleSetCollection ruleSetCollection = (RuleSetCollection)importFromFile(metadataFile);
            
            if (ruleSetCollection == null) {
                continue;
            }

            CollectionNode collectionNode = new CollectionNode(ruleSetCollection);
            for (File file : collectionDir.listFiles()) {
                if (file.getName().equals("metadata.epc")) {
                    continue;
                }
                
                RuleSet ruleSet = (RuleSet)importFromFile(file);

                if (ruleSet == null) {
                    continue;
                }

                RuleSetNode ruleSetNode = new RuleSetNode(ruleSet);
                collectionNode.insert(ruleSetNode, collectionNode.getChildCount());
            }
            model.insertNodeInto(collectionNode, root, root.getChildCount());
        }
        model.addTreeModelListener(this);
        return model;
    }

    private String makeDirectoryName(String title) {
        return title.replace(" ", "_").toLowerCase();
    }

    private String makeFileName(String title) {
        return title.replace(" ", "_").toLowerCase() + ".epr";
    }

    public void treeNodesChanged(TreeModelEvent e) {
        DefaultMutableTreeNode parentNode =
                (DefaultMutableTreeNode)e.getTreePath().getLastPathComponent();
        DefaultMutableTreeNode changedNode = (DefaultMutableTreeNode)
                parentNode.getChildAt(e.getChildIndices()[0]);

        if (changedNode instanceof PickedRuleSetNode) {
            return;
        }

        if (changedNode instanceof RuleSetNode) {
            RuleSetNode ruleSetNode = (RuleSetNode)changedNode;
            CollectionNode collectionNode = (CollectionNode)parentNode;
            assert(collectionNode != null);
            File ruleSetFile = new File(collectionsDir,
                    makeDirectoryName(collectionNode.getName()) + File.separator +
                    makeFileName(ruleSetNode.getName()));
            writeToFile((RuleSet)ruleSetNode.getUserObject(), ruleSetFile);

            // delete old rule set file if the name was changed
            File collectionDir = new File(collectionsDir,
                    makeDirectoryName(collectionNode.getName()));
            File [] ruleSetFiles = collectionDir.listFiles();
            for (int i = 0; i < ruleSetFiles.length; i++) {
                boolean found = false;
                if (ruleSetFiles[i].getName().equals("metadata.epc")) {
                    continue;
                }
                Enumeration enumeration = collectionNode.children();
                while (enumeration.hasMoreElements()) {
                    String name = makeFileName(((RuleSetNode)enumeration.nextElement()).getName());
                    if (ruleSetFiles[i].getName().equals(name)) {
                        found = true;
                        break;
                    }
                }
                if (found == false) {
                    if (false == ruleSetFiles[i].delete()) {
                        ExceptionHandler.handle(new Exception(), false, "I failed to delete the old rule set file at \"" + ruleSetFiles[i].getAbsolutePath() + "\"");
                    }
                    break;
                }
            }
            return;
        }

        if (changedNode instanceof CollectionNode) {
            //System.out.println("collection directory changed");
            CollectionNode collectionNode = (CollectionNode)changedNode;
            assert(collectionNode != null);
            File [] collectionDirs = collectionsDir.listFiles();

            for (int i = 0; i < collectionDirs.length; i++) {
                boolean found = false;
                Enumeration enumeration = parentNode.children();
                while (enumeration.hasMoreElements()) {
                    String name = makeDirectoryName(((CollectionNode)enumeration.nextElement()).getName());
                    if (collectionDirs[i].getName().equals(name)) {
                        found = true;
                        break;
                    }
                }
                if (found == false) {
                    if (false == collectionDirs[i].renameTo(new File(collectionsDir,
                            makeDirectoryName(collectionNode.getName())))) {
                        ExceptionHandler.handle(new Exception(), false, "Failed to rename collection directory: " + collectionDirs[i].getAbsolutePath());
                    } else {
                        File metaDataFile = new File(collectionsDir,
                                makeDirectoryName(collectionNode.getName()) +
                                "/metadata.epc");

                        writeToFile(collectionNode.getUserObject(), metaDataFile);
                    }
                    break;
                }
            }
            return;
        }
        assert (false);
    }

    public void treeNodesInserted(TreeModelEvent e) {
        DefaultMutableTreeNode parentNode =
                (DefaultMutableTreeNode)e.getTreePath().getLastPathComponent();
        DefaultMutableTreeNode addedNode =
                (DefaultMutableTreeNode)e.getChildren()[0];

        if (addedNode instanceof PickedRuleSetNode) {
            return;
        }

        //System.out.println("file handler: detected node insertion");
        if (addedNode instanceof CollectionNode) {
            File collectionDir = new File(collectionsDir,
                    makeDirectoryName(((CollectionNode)addedNode).getName()));
            if (false == collectionDir.exists()) {
                collectionDir.mkdir();
            }

            File metaDataFile = new File(collectionDir, "metadata.epc");
            writeToFile(addedNode.getUserObject(), metaDataFile);
            return;
        }

        if (addedNode instanceof RuleSetNode) {
            CollectionNode collectionNode = (CollectionNode)parentNode;
            assert(collectionNode != null);
            File ruleSetFile = new File(collectionsDir,
                makeDirectoryName(collectionNode.getName() +
                File.separator +
                makeFileName(((RuleSetNode)addedNode).getName())));
            writeToFile(addedNode.getUserObject(), ruleSetFile);
            return;
        }

        assert(false);
    }

    public void treeNodesRemoved(TreeModelEvent e) {
        DefaultMutableTreeNode parentNode =
                (DefaultMutableTreeNode)e.getTreePath().getLastPathComponent();
        DefaultMutableTreeNode removedNode =
                (DefaultMutableTreeNode)e.getChildren()[0];
        
        if (removedNode instanceof PickedRuleSetNode) {
            return;
        }

        //System.out.println("file handler: detected node deletion");
        if (removedNode instanceof CollectionNode) {
            String dirName = makeDirectoryName(((CollectionNode)removedNode).getName());
            File collectionDir = new File(collectionsDir, dirName);
            collectionDir.delete();
        }

        if (removedNode instanceof RuleSetNode) {
            CollectionNode collectionNode = (CollectionNode)parentNode;
            assert(collectionNode != null);
            File ruleSetFile = new File(collectionsDir,
                    makeDirectoryName(collectionNode.getName() +
                    File.separator +
                    makeFileName(((RuleSetNode)removedNode).getName())));
            if (false == ruleSetFile.delete()) {
                ExceptionHandler.handle(new Exception(), false, "I failed to delete rule set file: " + ruleSetFile.getAbsolutePath());
            }
            return;
        }

        assert(false);
    }

    public void treeStructureChanged(TreeModelEvent e) {
        ExceptionHandler.handle(new Exception(), true, "file handler: detected node structure change this was not expected, exiting.");
    }

    private Object importFromFile(File file) {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = null;
        String line = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
        } catch (UnsupportedEncodingException ex) {
            ExceptionHandler.handle(ex, false);
        } catch (FileNotFoundException ex) {
            ExceptionHandler.handle(ex, true);
        } catch(IOException ex) {
            ExceptionHandler.handle(ex, true);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ex) {
                ExceptionHandler.handle(ex, true);
            }
        }

        return Configuration.IMPORT_EXPORT_HANDLER.importFromString(stringBuilder.toString(), file);
    }

    public void writeToFile(Object object, File file) {
        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
            writer.write(Configuration.IMPORT_EXPORT_HANDLER.exportToString(object));
        } catch (UnsupportedEncodingException ex) {
            ExceptionHandler.handle(ex, false);
        } catch (FileNotFoundException ex) {
            ExceptionHandler.handle(ex, true);
        } catch (IOException ex) {
            ExceptionHandler.handle(ex, true);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException ex) {
                ExceptionHandler.handle(ex, true);
            }
        }
    }

    public FileHandler() {
        homeDir = new File(System.getProperty("user.dir"));
        collectionsDir = new File(homeDir, "/collections");
        if (false == collectionsDir.exists()) {
            ExceptionHandler.handle(new Exception(), true, "I cannot find the collections folder in the current working directory \"" + homeDir + "\"! If you created a short cut for me on your Desktop *blushes* make sure you set the working directory poperty correctly and click that sexy short cut again!");
        }
    }
}
