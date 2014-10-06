package malictus.robusta.swing;

import java.awt.*;
import javax.swing.*;

/**
 * A SmartComboBox is a JComboBox with additional functionality.
 */
public class SmartComboBox extends JComboBox {

	/**
	 * SmartComboBox constructor for non-null layouts
	 * 
	 * @param editable whether the combo box is editable
	 */
	public SmartComboBox(boolean editable) {
		super();
    	this.setEditable(editable);
    	this.setVisible(true);
	}
	
	/**
	 * SmartComboBox constructor for null layouts
	 * 
	 * @param editable whether the combo box is editable
	 * @param rect bounds of the combo box
	 */
	public SmartComboBox(boolean editable, Rectangle rect) {
		super();
    	this.setBounds(rect);
    	this.setEditable(editable);
    	this.setVisible(true);
	}

}
