package malictus.robusta.demo;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import malictus.robusta.swing.*;

/**
 * Demonstrate and test some of the SmartWindow features, as well as some of the other components in the robusta.swing package.
 */
public class SmartWindowDemo extends SmartWindow {
	
	SmartButton button;
	SmartButton showFileChooser;
	SmartTextPane textPane;
	SmartTree tree;
	SmartTextField textfield;
	
	/**
	 * Run to demonstrate SmartWindow features
	 *
	 * @param args not currently used
	 */
	public static void main(String[] args) {
		new SmartWindowDemo();
	}
	
	/**
	 * Initialize the SmartWindow and add a few sample components
	 */
	public SmartWindowDemo() {
		super(400, 300, false, true, "SmartWindow Demo", true);
		button = new SmartButton(10, 10, "Press");
		button.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				//button should automatically resize
				if (button.getText().equals("Press")) {
					button.setText("Press Again");
				} else {
					button.setText("Press");
				}
			}
		});
		contentPane.add(button);
		showFileChooser = new SmartButton(120, 10, "Show File Chooser");
		showFileChooser.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				SmartFileChooser sfc = new SmartFileChooser("txt, rtf, doc", "Text files (txt, rtf, and doc)", true);
				sfc.showOpenDialog(null);
				
			}
		});
		contentPane.add(showFileChooser);
		textPane = new SmartTextPane(new Rectangle(10, 50, 100, 100), true);
		textPane.setText("Here is some sample text that should cause a scroll bar to automatically appear.");
		textPane.setMaximumStringSize(200);
		contentPane.add(textPane);
		tree = new SmartTree(new Rectangle(150, 50, 160, 150));
		//build a simple hierarchy tree structure for the tree
		DefaultMutableTreeNode treeroot = new DefaultMutableTreeNode("Tree root");
		treeroot.add(new DefaultMutableTreeNode("Child node 1"));
		treeroot.add(new DefaultMutableTreeNode("Child node 2"));
		DefaultMutableTreeNode node3 = new DefaultMutableTreeNode("Child node 3");
		node3.add(new DefaultMutableTreeNode("Grandchild node"));
		treeroot.add(node3);
		tree.rebuildTree(treeroot, true);
		contentPane.add(tree);
		textfield = new SmartTextField(10, 230, 240);
		textfield.setMaximumStringSize(40);
		textfield.setText("No strings longer than 40 characters.");
		contentPane.add(textfield);
		this.setVisible(true);
	}
	
	/**
	 * Override of method to check to see if window should really be closed. This method is called whenever the user attempts to close the window.
	 */
	protected boolean canClose() {
		JOptionPane.showMessageDialog(this, "Here is where you would place checks to ensure that the window should be closed.");
		//would return false if the window should not be closed
		return true;
	}

}
