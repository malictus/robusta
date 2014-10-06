package malictus.robusta.swing;

import javax.swing.*;
import java.awt.*;

/**
 * A SmartList is a JList embedded in a JScrollPane.
 */
public class SmartList extends JScrollPane {

	private JList list = null;

	/**
	 * Default SmartList constructor.
	 */
	public SmartList() {
		super();
		list = new JList();
    	this.setViewportView(list);
    	this.setVisible(true);
	}
	
	/**
	 * SmartList constructor with preset list data.
	 *
	 * @param data List data (array of string objects)
	 */
	public SmartList(String[] data) {
		super();
		if (data == null) {
			list = new JList();
		} else {
			list = new JList(data);
		}
    	this.setViewportView(list);
    	this.setVisible(true);
	}
	
	/**
	 * Default SmartList constructor for null layout.
	 *
	 * @param rect Initial location and size of the scroll pane.
	 */
	public SmartList(Rectangle rect) {
		super();
		list = new JList();
    	this.setBounds(rect);
    	this.setViewportView(list);
    	this.setVisible(true);
	}

	/**
	 * SmartList constructor for null layout with preset list data.
	 *
	 * @param rect Initial location and size of the scroll pane.
	 * @param data List data (array of string objects)
	 */
	public SmartList(Rectangle rect, String[] data) {
		super();
		if (data == null) {
			list = new JList();
		} else {
			list = new JList(data);
		}
    	this.setBounds(rect);
    	this.setViewportView(list);
    	this.setVisible(true);
	}

	/**
	 * Allows access to the JList itself, embedded within the JScrollPane
	 *
	 * @return The JTextPane itself
	 */
	public JList getList() {
		return list;
	}

}
