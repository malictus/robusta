package malictus.robusta.swing;

import java.awt.*;
import javax.swing.*;

/**
 * A SmartButton is a JButton that automatically adjusts the size of the button to match
 * the given text. Changing the text or font will cause the button to automatically resize to fit.
 * Currently this button is meant to be used in null layouts only.
 */
public class SmartButton  extends JButton {

	public final static int WIDTH_PAD = 18;
	public final static int HEIGHT_PAD = 10;

	/**
	 * Smart Button construtor class
	 *
	 * @param x X position of the button
	 * @param y Y position of the button
	 * @param text button text. The size of the button will be adjusted to fix the
	 * text length.
	 */
	public SmartButton(int x, int y, String text) {
		super(text);
		//width and height will be set later
		this.setBounds(new java.awt.Rectangle(x, y, 1, 1));
		this.setMargin(new Insets(2,2,2,2));
		resizeButton();
    	this.setVisible(true);
	}

	/**
	 * Overridden to resize when changing button text.
	 *
	 * @param text The new button text
	 */
	public void setText(String text) {
		super.setText(text);
		resizeButton();
	}

	/**
	 * Overridden to resize when changing button font.
	 *
	 * @param font The new font
	 */
	public void setFont(Font font) {
		super.setFont(font);
		resizeButton();
	}

	/**
	 * Actually does the resizing of the button.
	 */
	private void resizeButton() {
		//check for this since this method will be called once before button initializes
		if (this.getFont() == null) {
			return;
		}
		FontMetrics fm = this.getFontMetrics(this.getFont());
    	int width = fm.stringWidth(this.getText());
    	int height = fm.getHeight();
    	this.setBounds(new java.awt.Rectangle(this.getX(), this.getY(), width + WIDTH_PAD, height + HEIGHT_PAD));
	}

}
