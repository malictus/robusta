package malictus.robusta.swing;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;

/**
 * A SmartTextField is a JTextField that is placed on the screen automatically. The width
 * of the field is not adjusted automatically, but the height of the field will be adjusted 
 * when the font is changed. Currently this is meant to be used in null layouts only.
 */
public class SmartTextField extends JTextField {
	
	public static final int HEIGHT_PAD = 4;

	/**
	 * SmartTextField constructor
	 * 
	 * @param x X position of the textfield
	 * @param y Y position of the textfield
	 * @param width initial width of the textfield
	 */
	public SmartTextField(int x, int y, int width) {
		super();
		//real height will be set later
    	this.setBounds(new java.awt.Rectangle(x, y, width, 1));
    	resizeTextField();
    	this.setVisible(true);
	}
	
	/**
	 * Set a maximum character limit for this text field.
	 * @param size the maximum character limit for this text field
	 */
	public void setMaximumStringSize(int size) {
		if (size >= 0) {
			this.setDocument(new JTextFieldLimit(size));
		}
	}
	
	/**
	 * Overridden to resize when changing text field font.
	 * 
	 * @param font The new font
	 */
	public void setFont(Font font) {
		super.setFont(font);
		resizeTextField();
	}
	
	/**
	 * Actually does the resizing of the text field.
	 */
	private void resizeTextField() {
		FontMetrics fm = this.getFontMetrics(this.getFont());
    	int height = fm.getHeight();
    	this.setBounds(new java.awt.Rectangle(this.getX(), this.getY(), this.getWidth(), height + HEIGHT_PAD));
	}
	
	private class JTextFieldLimit extends PlainDocument {
		
		  private int limit;
		  
		  JTextFieldLimit(int limit) {
		    super();
		    this.limit = limit;
		  }

		  public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
			  if (str == null) {
				  return;
			  }
			  if ((getLength() + str.length()) <= limit) {
				  super.insertString(offset, str, attr);
			  } else if (getLength() < limit) {
				  super.insertString(offset, str.substring(0, limit - getLength()), attr);
			  }
		 }
	}

}
