package malictus.robusta.swing;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

/**
 * This abstract class creates a basic Swing window. Several constructor
 * options can be changed to control various aspects of the window.
 * Many application windows can be based on this window.
 */
public abstract class SmartWindow extends JFrame {

	protected JPanel contentPane = null;

	/**
	 * Constructor for SmartWindow. Parameters for the constructor control the
	 * way the window is created.
	 *
	 * @param width Initial width of the window
	 * @param height Initial length of the window
	 * @param showWindow If true, window will be shown immediately when it is created
	 * @param resizeable If true, window will be resizeable
	 * @param centerWindow If true, window will display in the center of the screen
	 * @param title The title of the window
	 * @param endOnClose If true, app will close when window is closed
	 */
	public SmartWindow(int width, int height, boolean resizeable, boolean centerWindow, String title, boolean endOnClose) {
		super();
		if (endOnClose) {
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
        this.setSize(new java.awt.Dimension(width, height));
        this.setResizable(resizeable);
        this.setTitle(title);
        contentPane = new JPanel();
        //no layout, by default
        contentPane.setLayout(null);
        this.setContentPane(contentPane);
        if (centerWindow) {
        	centerWindow();
        }
	}

	/**
     * Center this window on the screen
	 */
    public void centerWindow() {
    	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = this.getSize();
        if (frameSize.height > screenSize.height) {
        	frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
        	frameSize.width = screenSize.width;
        }
        this.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    }

	/**
	 * Overridden to enable a trigger when window is closing
	 *
	 * @param e the WindowEvent that is being processed
	 */
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            //give user opportunity to abort close
        	if (this.canClose()) {
        		super.processWindowEvent(e);
        	}
        } else {
            super.processWindowEvent(e);
        }
    }

    /**
     * Return true if it's OK to really close the window. This can be
     * overwritten to implement checks for dirty states before closing, etc.
     *
     * @return true to truly close window. Return false to keep window open.
     */
    protected boolean canClose() {
    	return true;
    }

}
