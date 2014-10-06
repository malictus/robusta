package malictus.robusta.swing;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

/**
 * A SmartTextPane is a JTextPane embedded in a JScrollPane.
 */
public class SmartTextPane extends JScrollPane {

	private JTextPane txtpText = null;

	/**
	 * Regular SmartTextPane constructor.
	 *
	 * @param editable Whether the textpane is initially editable or not.
	 */
	public SmartTextPane(boolean editable) {
		super();
    	txtpText = new JTextPane();
    	txtpText.setEditable(editable);
    	this.setViewportView(txtpText);
    	this.setVisible(true);
	}

	/**
	 * SmartTextPane constructor for null layout windows.
	 *
	 * @param rect Initial location and size of the scroll pane.
	 * @param editable Whether the textpane is initially editable or not.
	 */
	public SmartTextPane(Rectangle rect, boolean editable) {
		super();
    	this.setBounds(rect);
    	txtpText = new JTextPane();
    	txtpText.setEditable(editable);
    	this.setViewportView(txtpText);
    	this.setVisible(true);
	}
	
	/**
	 * Set a maximum character limit for this text pane.
	 * @param size the maximum character limit for this text pane
	 */
	public void setMaximumStringSize(int size) {
		if (size >= 0) {
			this.txtpText.setDocument(new JTextPaneLimit(size));
		}
	}

	/**
	 * Convenience method.
	 *
	 * @param b whether the textpane should be editable or not
	 */
	public void setEditable(boolean b) {
		txtpText.setEditable(b);
	}

	/**
	 * Convenience method.
	 *
	 * @param text New text for textpane
	 */
	public void setText(String text) {
		txtpText.setText(text);
	}

	/**
	 * Convenience method.
	 *
	 * @return The current textpane text
	 */
	public String getText() {
		return txtpText.getText();
	}

	/**
	 * Convenience method
	 * 
	 * @param enabled whether textpane is enabled or disabled
	 */
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		this.txtpText.setEnabled(enabled);
	}
	
	/**
	 * Allows access to the JTextPane itself, embedded within the JScrollPane
	 *
	 * @return The JTextPane itself
	 */
	public JTextPane getTextPane() {
		return txtpText;
	}
	
	private class JTextPaneLimit extends DefaultStyledDocument {
		
		  private int limit;
		  
		  JTextPaneLimit(int limit) {
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
