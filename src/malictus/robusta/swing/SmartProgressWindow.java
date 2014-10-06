package malictus.robusta.swing;

import javax.swing.*;
import java.util.*;

/**
 * SmartProgressWindow
 * 
 * An abstract modal dialog which can be used to monitor progress for long tasks. Contains a progress indicator, a status line, and a cancel button,
 * all of which are optional. To use, simply create an instance of this class that adds a 'startTask()' type method, which gives the dialog a task, starts
 * the task, and sets the dialog to visible. To monitor progress through the task, modify the status string or the progressCounter. When the task is finished,
 * set finished variable to true. If you use the 'cancel' button, you'll need to manually check for the canceled variable to be set to true while the task
 * is working in order to truly cancel by calling wasCanceled().
 *
 */
public abstract class SmartProgressWindow extends JDialog {

	private JPanel jContentPane = null;
	private JLabel lblProg = null;
	private JProgressBar prgProg = null;
	private JButton btnCancel = null;

	private boolean finished = false;
	private int progressCounter = 0;
	private boolean canceled = false;
	private String status = "";

	private java.util.Timer theTimer = new java.util.Timer();

	/**
	 * Initialize the SmartProgressWindow
	 * 
	 * @param parent Parent window to this dialog; may be null
	 * @param showProgress whether or not to show a JProgressBar in the progress window
	 * @param showText whether or not to show a changeable text message in the progress window
	 * @param showCancel whether or not to show a cancel button in the progress window
	 */
	public SmartProgressWindow(java.awt.Window parent, boolean showProgress, boolean showText, boolean showCancel) {
		super();
        this.setSize(new java.awt.Dimension(412,88));
        if (parent != null) {
        	//center over parent window
        	this.setLocation(parent.getX() + (parent.getWidth() / 2) - (this.getWidth() / 2),
        			parent.getY() + (parent.getHeight() / 2) - (this.getHeight() / 2));
        }
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setModal(true);
        this.setResizable(false);
        lblProg = new JLabel();
		lblProg.setBounds(new java.awt.Rectangle(7,5,300,16));
		lblProg.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12));
		lblProg.setText("");
		btnCancel = new JButton();
		btnCancel.setBounds(new java.awt.Rectangle(320,28,75,22));
		btnCancel.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12));
		btnCancel.setText("Cancel");
		btnCancel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				setCanceled(true);
			}
		});
		prgProg = new JProgressBar();
		prgProg.setMinimum(0);
		prgProg.setMaximum(100);
		prgProg.setValue(0);
		prgProg.setBounds(new java.awt.Rectangle(7,28,303,23));
		jContentPane = new JPanel();
		jContentPane.setLayout(null);
		if (showText) {
			jContentPane.add(lblProg, null);
		}
		if (showCancel) {
			jContentPane.add(btnCancel, null);
		}
		if (showProgress) {
			jContentPane.add(prgProg, null);
		}
		this.setContentPane(jContentPane);
		//start the timer to listen behind the scenes
		ProgressTask lTask = new ProgressTask(theTimer);
        theTimer.schedule(lTask, 0, 200);
	}

	/**
	 * Toggle whether or not the window's progress was canceled
	 * 
	 * @param canceledVal shows whether or not the window's progress was canceled
	 */
	public void setCanceled(boolean canceledVal) {
		canceled = canceledVal;
	}

	/**
	 * Public retrieval method for canceled variable
	 * 
	 * @return true if the progress was canceled, false otherwise
	 */
	public boolean wasCanceled() {
		return canceled;
	}

	/**
	 * Update the status text string for this window
	 * 
	 * @param newStatus the new status string
	 */
	public void setStatus(String newStatus) {
		status = newStatus;
	}

	/**
	 * Update the JProgressBar for this window
	 * 
	 * @param newValue the new value for the progress bar
	 */
	public void setProgressCounter(int newValue) {
		progressCounter = newValue;
	}

	/**
	 * Set the maximum value for the JProgressBar for this window
	 * 
	 * @param newValue the new max value
	 */
	public void setProgressCounterMax(int newValue) {
		prgProg.setMaximum(newValue);
	}

	/**
	 * Toggle the cancel button for this window
	 * 
	 * @param enabled if true, button will be enabled; false otherwise
	 */
	public void setCancelButtonEnabled(boolean enabled) {
		this.btnCancel.setEnabled(enabled);
	}

	/**
	 * Public toggle to signal that the task is finished. Calling this method will close the dialog and stop the timer
	 */
	public void taskFinished() {
		finished = true;
	}

	/**
	 * The SmartProgressWindow uses this task to periodically update the status text and progress counter, 
	 * and to close the window at the appropriate time.
	 */
	private class ProgressTask extends TimerTask {

        java.util.Timer myTimer = null;
        String currStatus = "";
        int currCounter = 0;

        public ProgressTask(java.util.Timer aTimer) {
            super();
            myTimer = aTimer;
        }

        public void run() {
            if (finished) {
            	theTimer.cancel();
            	setVisible(false);
            }
            if (!(currStatus.equals(status))) {
            	currStatus = status;
            	lblProg.setText(status);
            }
            if (currCounter != progressCounter) {
            	currCounter = progressCounter;
            	prgProg.setValue(progressCounter);
            }
        }
	}
}
