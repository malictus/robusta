package malictus.robusta.swing;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.*;

/**
 * A SmartTree is a JTree structure imbedded in a scroll pane. 
 * Currently all SmartTrees should use only DefaultMutableTreeNodes.
 */
public class SmartTree extends JScrollPane {

	private JTree tree = null;

	/**
	 * Regular Smart Tree constructor.
	 */
	public SmartTree() {
		super();
    	//used to prevent default nodes from showing up
    	DefaultMutableTreeNode fakeNode = new DefaultMutableTreeNode();
		tree = new JTree(fakeNode);
		tree.setEditable(false);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setShowsRootHandles(true);
		tree.setExpandsSelectedPaths(true);
    	this.setViewportView(tree);
    	this.setVisible(true);
	}

	/**
	 * Smart Tree constructor for null layout windows.
	 *
	 * @param rect The location of the tree's scroll pane within the parent window.
	 */
	public SmartTree(Rectangle rect) {
		super();
    	this.setBounds(rect);
    	//used to prevent default nodes from showing up
    	DefaultMutableTreeNode fakeNode = new DefaultMutableTreeNode();
		tree = new JTree(fakeNode);
		tree.setEditable(false);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setShowsRootHandles(true);
		tree.setExpandsSelectedPaths(true);
    	this.setViewportView(tree);
    	this.setVisible(true);
	}

	/**
	 * A way to get the JTree object itself
	 *
	 * @return the JTree object
	 */
	public JTree getTree() {
		return tree;
	}

	/**
	 * Remove all nodes from the tree
	 */
	public void resetTree() {
		DefaultMutableTreeNode fakeNode = new DefaultMutableTreeNode();
		rebuildTree(fakeNode, true);
	}

	/**
	 * This should be called whenever the tree is first built, and whenever it
	 * needs to be rebuilt.
	 *
	 * @param theNode the new root node of the tree
	 * @param expandNodes whether to begin with all nodes expanded or not
	 * @throws ClassCastException if the given node contains any children that are not DefaultMutableTreeNode objects
	 */
	public void rebuildTree(DefaultMutableTreeNode theNode, boolean expandNodes) throws ClassCastException {
		DefaultTreeModel model = new DefaultTreeModel(theNode);
    	tree.setModel(model);
    	if (expandNodes) {
	    	//Expand all nodes
    		Enumeration<DefaultMutableTreeNode> nodeList = null;
    		try {
    			nodeList = theNode.preorderEnumeration();
    		} catch (Exception err) {
    			throw new ClassCastException("Tree contains invalid nodes");
    		}
	        while (nodeList.hasMoreElements()) {
		        DefaultMutableTreeNode currNode = nodeList.nextElement();
		        tree.expandPath(new TreePath(currNode.getPath()));
	        }
    	}
	}
	
	/**
	 * Convenience method
	 * 
	 * @param enabled whether tree is enabled or disabled
	 */
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		this.tree.setEnabled(enabled);
	}

}
