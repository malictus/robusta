package malictus.robusta.swing;

import java.awt.*;
import javax.swing.*;

/**
 * A SmartLabel is a JLabel which will automatically resize itself to match
 * its text. Changing the text or font will cause the label to automatically resize to fit.
 * Currently this label is meant to be used in null layouts only.
 */
public class SmartLabel extends JLabel {

	public final static int WIDTH_PAD = 2;
	public final static int HEIGHT_PAD = 2;

	/**
	 * The SmartLabel constructor
	 *
	 * @param x X coordinate of the SmartLabel
	 * @param y Y coordinate of the SmartLabel
	 * @param text text of the SmartLabel
	 */
	public SmartLabel(int x, int y, String text) {
		super(text);
		//width and height will be set later
		this.setBounds(new java.awt.Rectangle(x, y, 1, 1));
		resizeLabel();
    	this.setVisible(true);
	}

	/**
	 * Overridden to resize when changing label text.
	 *
	 * @param text The new label text
	 */
	public void setText(String text) {
		super.setText(text);
		resizeLabel();
	}

	/**
	 * Overridden to resize when changing label font.
	 *
	 * @param font The new font
	 */
	public void setFont(Font font) {
		super.setFont(font);
		resizeLabel();
	}

	/**
	 * Actually does the resizing of the label.
	 */
	private void resizeLabel() {
		//check for this since this method will be called once before label initializes
		if (this.getFont() == null) {
			return;
		}
		FontMetrics fm = this.getFontMetrics(this.getFont());

    	int width = fm.stringWidth(this.getText());
    	int height = fm.getHeight();
    	this.setBounds(new java.awt.Rectangle(this.getX(), this.getY(), width + WIDTH_PAD, height + HEIGHT_PAD));
	}

}
