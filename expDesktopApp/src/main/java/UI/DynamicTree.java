// /*
//  * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
//  *
//  * Redistribution and use in source and binary forms, with or without
//  * modification, are permitted provided that the following conditions
//  * are met:
//  *
//  *   - Redistributions of source code must retain the above copyright
//  *     notice, this list of conditions and the following disclaimer.
//  *
//  *   - Redistributions in binary form must reproduce the above copyright
//  *     notice, this list of conditions and the following disclaimer in the
//  *     documentation and/or other materials provided with the distribution.
//  *
//  *   - Neither the name of Oracle or the names of its
//  *     contributors may be used to endorse or promote products derived
//  *     from this software without specific prior written permission.
//  *
//  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
//  * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
//  * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
//  * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
//  * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
//  * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
//  * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
//  * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
//  * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
//  * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
//  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//  */

// package UI;

// /*
//  * This code is based on an example provided by Richard Stanford, 
//  * a tutorial reader.
//  */

// // import UI.editor.Editor;
// // import UI.editor.ExperimentEditor;
// // import UI.editor.MouseEditor;
// import classes.*;

// import java.awt.*;
// import java.awt.event.MouseAdapter;
// import java.awt.event.MouseEvent;
// import java.io.Serializable;
// import java.util.HashMap;
// import java.util.Map;
// import javax.swing.*;
// import javax.swing.tree.*;
// import javax.swing.event.TreeModelEvent;
// import javax.swing.event.TreeModelListener;

// public class DynamicTree extends JPanel implements Serializable {
//     private static final long serialVersionUID = 1L;
//     protected DefaultMutableTreeNode rootNode;
//     protected DefaultTreeModel treeModel;
//     protected JTree tree;
//     private Toolkit toolkit = Toolkit.getDefaultToolkit();
//     private DefaultMutableTreeNode experiments, mice;
//     private DataManager man;

//     public DynamicTree(DataManager man) {
//         super(new GridLayout(1,0));
//         init(man);
//     }

//     public void init(DataManager man) {
//         this.man = man;
//         rootNode = new DefaultMutableTreeNode("Root Node");
//         treeModel = new DefaultTreeModel(rootNode);
//         treeModel.addTreeModelListener(new MyTreeModelListener());
//         tree = new JTree(treeModel);
//         tree.setEditable(true);
//         tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
//         tree.setShowsRootHandles(true);
//         JScrollPane scrollPane = new JScrollPane(tree);
//         add(scrollPane);
//         paint();
//     }

//     private void paint() {
//         experiments = addObject(rootNode, "Experiments");
//         mice = addObject(rootNode, "Mice");
//         addObject(experiments, "add an experiment");
//         addObject(mice, "add mouse");
//         tree.setCellRenderer(new IconTreeCellRenderer());
//         tree.addMouseListener(new PopClickListener());
//     }

//     /** Remove all nodes except the root node. */
//     public void clear() {
//         rootNode.removeAllChildren();
//         treeModel.reload();
//         paint();
//     }

//     public void setMan(DataManager man) {
//         this.man = man;
//     }

//     /** Remove the currently selected node. */
//     public void removeCurrentNode() {
//         TreePath currentSelection = tree.getSelectionPath();
//         if (currentSelection != null) {
//             DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)
//                     (currentSelection.getLastPathComponent());
//             MutableTreeNode parent = (MutableTreeNode)(currentNode.getParent());
//             if (parent != null) {
//                 treeModel.removeNodeFromParent(currentNode);
//                 return;
//             }
//         }

//         // Either there was no selection, or the root was selected.
//         toolkit.beep();
//     }

//     /** Add child to the currently selected node. */
//     public DefaultMutableTreeNode addObject(Object child) {
//         DefaultMutableTreeNode parentNode = null;
//         TreePath parentPath = tree.getSelectionPath();

//         if (parentPath == null) {
//             parentNode = rootNode;
//         } else {
//             parentNode = (DefaultMutableTreeNode)
//                     (parentPath.getLastPathComponent());
//         }

//         return addObject(parentNode, child, true);
//     }

//     public DefaultMutableTreeNode addExperiment(Experiment exp)
//     {
//         return addObject(experiments, exp);
//     }

//     public DefaultMutableTreeNode addMouse(Mouse maus)
//     {
//         DefaultMutableTreeNode mausRoot = addObject(mice, maus);
//         /*HashMap<Integer, Experiment> map = maus.getExperiments();
//         for(Map.Entry<Integer, Experiment> exp: map.entrySet()) {
//             addObject(mausRoot, man.getExperiment(exp.getKey()));
//         } // TODO check 
//         */
//     }

//     public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent, Object child) {
//         return addObject(parent, child, false);
//     }

//     public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent, Object child, boolean shouldBeVisible) {
//         DefaultMutableTreeNode childNode =
//                 new DefaultMutableTreeNode(child);

//         if (parent == null) {
//             parent = rootNode;
//         }

//         //It is key to invoke this on the TreeModel, and NOT DefaultMutableTreeNode
//         treeModel.insertNodeInto(childNode, parent,
//                 parent.getChildCount());

//         //Make sure the user can see the lovely new node.
//         if (shouldBeVisible) {
//             tree.scrollPathToVisible(new TreePath(childNode.getPath()));
//         }
//         return childNode;
//     }

//     class MyTreeModelListener implements TreeModelListener {
//         public void treeNodesChanged(TreeModelEvent e) {
//             DefaultMutableTreeNode node;
//             node = (DefaultMutableTreeNode)(e.getTreePath().getLastPathComponent());

//             /*
//              * If the event lists children, then the changed
//              * node is the child of the node we've already
//              * gotten.  Otherwise, the changed node and the
//              * specified node are the same.
//              */

//             int index = e.getChildIndices()[0];
//             node = (DefaultMutableTreeNode)(node.getChildAt(index));

//             System.out.println("The user has finished editing the node.");
//             System.out.println("New value: " + node.getUserObject());
//         }
//         public void treeNodesInserted(TreeModelEvent e) {
//         }
//         public void treeNodesRemoved(TreeModelEvent e) {
//         }
//         public void treeStructureChanged(TreeModelEvent e) {
//         }


//     }

//     class IconTreeCellRenderer implements TreeCellRenderer {
//         private JLabel icon;
// //        private String addImg = "C:\\Users\\owner\\Documents\\LAB_STRUCTURE\\src\\UI\\Add.png";
// //        private String exps = "C:\\Users\\owner\\Documents\\LAB_STRUCTURE\\src\\UI\\experiments.png";
//         private String addImg = GlobalConfig.makePathGlobalString("LAB_STRUCTURE/src/UI/Add.png");
//         private String exps = GlobalConfig.makePathGlobalString("LAB_STRUCTURE/src/UI/experiments.png");

//         public IconTreeCellRenderer() {
//             this.icon = new JLabel();
//         }

//         @Override
//         public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
//             Object o = ((DefaultMutableTreeNode) value).getUserObject();

//             if (o instanceof Mouse)
//             {
//                 Mouse maus = (Mouse) o;
//                 String imageUrl = maus.getIconPath();
//                 if (imageUrl != null) {
//                     ImageIcon imageIcon = new ImageIcon(imageUrl);
//                     Image image = imageIcon.getImage(); // transform it
//                     Image newimg = image.getScaledInstance(14, 14,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
//                     imageIcon = new ImageIcon(newimg);  // transform it back
//                     icon.setIcon(imageIcon);
//                 }
//                 icon.setText(maus.toString());
//                 return icon;
//             }

//             if (o instanceof Experiment)
//             {

//                 Experiment exp = (Experiment) o;
//                 String imageUrl = ((Experiment) o).getIconPath();

//                 if (imageUrl != null) {
//                     ImageIcon imageIcon = new ImageIcon(imageUrl);
//                     Image image = imageIcon.getImage(); // transform it
//                     Image newimg = image.getScaledInstance(12, 12,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
//                     imageIcon = new ImageIcon(newimg);  // transform it back
//                     icon.setIcon(imageIcon);
//                 }
//                 icon.setText(exp.toString());
//                 return icon;
//             }

//             if (o instanceof String)
//             {
//                 String title = (String) o;
//                 if (title.contains("add"))
//                 {
//                     ImageIcon imageIcon = new ImageIcon(addImg);
//                     Image image = imageIcon.getImage(); // transform it
//                     Image newimg = image.getScaledInstance(12, 12,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
//                     imageIcon = new ImageIcon(newimg);  // transform it back
//                     icon.setIcon(imageIcon);
//                     icon.setText("New");
//                     return icon;
//                 }

//                 if (title.compareTo("Experiments") == 0)
//                 {
//                     ImageIcon imageIcon = new ImageIcon(exps);
//                     Image image = imageIcon.getImage(); // transform it
//                     Image newimg = image.getScaledInstance(12, 12,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
//                     imageIcon = new ImageIcon(newimg);  // transform it back
//                     icon.setIcon(imageIcon);
//                     icon.setText("Experiments");
//                 }

//             }
// //            if (o instanceof JButton)
// //            {
// //                icon.setIcon(null);
// //                JButton butt = new JButton("Butt");
// //                icon.add(butt);
// //                return icon;
// //            }
// //            else {
// //                icon.setIcon(null);
// //                icon.setText("" + value);
// //            }
//             icon.setIcon(null);
//             icon.setText("" + value);
//             return icon;

//         }
//     }

//     class PopClickListener extends MouseAdapter {
//         public void mousePressed(MouseEvent e){
//             TreePath currentSelection = tree.getSelectionPath();
//             if (currentSelection != null) {
//                 DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)
//                         (currentSelection.getLastPathComponent());
//                 Object o = currentNode.getUserObject();
//                 if (o instanceof Mouse)
//                 {
//                     if (e.isPopupTrigger() && ((Mouse) o).aliveAndKicking())
//                         mouseMenuPop(e, (Mouse)o, currentNode);
//                 }
//                 if (o instanceof Experiment)
//                 {
//                     if (e.isPopupTrigger())
//                         experimentMenuPop(e, (Experiment)o, currentNode);
//                 }

//                 if (o instanceof String)
//                 {
//                     if (((String) o).contains("Mice"))
//                     {
//                         if (e.isPopupTrigger())
//                             miceMenu(e, currentNode);
//                     }
//                     if (((String) o).contains("Experiment"))
//                     {
//                         if (e.isPopupTrigger()){
//                             TreeMenuPop menu = new TreeMenuPop(TreeMenuPop.SELECT.EXPERIMENT_ADD);
//                             menu.addExperiment.addMouseListener(new MouseAdapter() {
//                                 @Override
//                                 public void mousePressed(MouseEvent e) {
//                                     super.mousePressed(e);
//                                     Experiment newExp = new Experiment();
//                                     man.addExperiment(newExp);
//                                     addObject(currentNode, newExp);
//                                 }
//                             });
//                             menu.show(e.getComponent(), e.getX(), e.getY());
//                         }

//                     }

//                 }
//             }
//         }

//         /**
//          * open objects on double click
//          * @param e mouse event
//          */
//         public void mouseClicked(MouseEvent e)
//         {
//             int row = tree.getRowForLocation(e.getX(),e.getY());
//             if (row == -1) //When user clicks on the "empty surface"
//                 tree.clearSelection();
//             if (e.getClickCount() == 2 && row != -1)
//             {
//                 DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
//                 Object o = selectedNode.getUserObject();
//                 if (o instanceof Experiment)
//                 {
//                     ExperimentEditor edit = new ExperimentEditor((Experiment)o, true);
//                     man.setEditingWin(edit);
//                 }

//                 if (o instanceof Mouse)
//                 {
//                     MouseEditor editor = new MouseEditor((Mouse) o, true);
//                     man.setEditingWin(editor);
//                 }
//             }
//         }

//         public void mouseReleased(MouseEvent e){
//             mousePressed(e);
//         }

//         private void mouseMenuPop(MouseEvent e, Mouse mau, DefaultMutableTreeNode parent){
//             TreeMenuPop menu = new TreeMenuPop(TreeMenuPop.SELECT.MOUSE);
//             menu.addExperiment.addMouseListener(new MouseAdapter() {
//                 @Override
//                 public void mousePressed(MouseEvent e) {
//                     super.mousePressed(e);
//                     Experiment newExp = new Experiment();
//                     man.addExperiment(newExp);
//                     addExperiment(newExp);
//                     mau.addExperiment(newExp);
//                     addObject(parent, newExp);
//                     MouseEditor editor = new MouseEditor(mau, true);
//                     man.setEditingWin(editor);
//                     editor.refresh();
//                 }
//             });
//             menu.show(e.getComponent(), e.getX(), e.getY());
//         }

//         private void miceMenu(MouseEvent e, DefaultMutableTreeNode parent){
//             TreeMenuPop menu = new TreeMenuPop(TreeMenuPop.SELECT.ADD_MOUSE);
//             menu.addMouse.addMouseListener(new MouseAdapter() {
//                 @Override
//                 public void mousePressed(MouseEvent e) {
//                     super.mousePressed(e);
//                     Mouse maus = new Mouse();
//                     man.addMouse(maus);
//                     addObject(parent, maus);
//                 }
//             });
//             menu.show(e.getComponent(), e.getX(), e.getY());
//         }

//         private void experimentMenuPop(MouseEvent e, Experiment exp, DefaultMutableTreeNode parent){
//             System.out.println(parent.getParent().toString());
//             TreeMenuPop menu;
//             if (parent.getParent().toString().contains("Mouse"))
//                 menu = new TreeMenuPop(TreeMenuPop.SELECT.EXPM);
//             else
//                 menu = new TreeMenuPop(TreeMenuPop.SELECT.EXPERIMENT); // todo: add edit only in case leaf of mice

//             menu.addExperiment.addMouseListener(new MouseAdapter() {
//                 @Override
//                 public void mousePressed(MouseEvent e) {
//                     super.mousePressed(e);
//                     Experiment newExp = new Experiment();
//                     man.addExperiment(newExp);
//                     addExperiment(newExp);
//                 }
//             });

//             menu.editExperiment.addMouseListener(new MouseAdapter() {
//                 @Override
//                 public void mousePressed(MouseEvent e) {
//                     super.mousePressed(e);
//                     Experiment newExp = new Experiment(exp);
//                     man.addExperiment(newExp);
//                     addExperiment(newExp);
//                 }
//             });
//             menu.show(e.getComponent(), e.getX(), e.getY());
//         }
//     }
// }