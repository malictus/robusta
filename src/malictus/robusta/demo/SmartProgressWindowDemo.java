package malictus.robusta.demo;

import malictus.robusta.file.*;
import malictus.robusta.swing.*;
import java.io.*;

/**
 * This class demonstrates the way a SmartProgressWindow should be used to display
 * progress for time-consuming tasks.
 * 
 */
public class SmartProgressWindowDemo extends SmartProgressWindow {

	/**
	 * Run to demonstrate the SmartProgressWindow features
	 *
	 * @param args not currently used
	 */
	public static void main(String[] args) {
		new SmartProgressWindowDemo();
	}
	
	/**
	 * Initialize the SmartProgressWindow, run some lengthy tasks, and then close
	 */
	public SmartProgressWindowDemo() {
		super(null, true, true, true);
		this.setTitle("SmartProgressWindow Demo...");
		this.setStatus("Initializing...");
		this.setProgressCounter(0);
		Runnable q = new Runnable() {
            public void run() {
				SmartFile x = null;
				SmartRandomAccessFile xRaf = null;
				try {
					//to begin with, we'll write the same junk data to a temp file 40000 times
					setProgressCounterMax(40000);
	            	setStatus("Generating temp file");
					x = new SmartFile(File.createTempFile("tmp", "tmp"));
					x.deleteOnExit();
					setStatus("Adding junk data to file");
					int counter = 0;
					xRaf = new SmartRandomAccessFile(x, "rw");
					while (counter < 40000) {
						//check to see if the user canceled by pressing cancel button
						if (wasCanceled()) {
							setCanceled(true);
							taskFinished();
							xRaf.close();
							x.delete();
							return;
						}
						//update the progress bar to show that something is happening
						setProgressCounter(counter);
						//just write some junk data that will take some time
						xRaf.writeLong(100);
						xRaf.writeDouble(Math.cos(Math.log(Math.random())));
						xRaf.writeBytes("a string");
						counter = counter + 1;
					}
					xRaf.close();					
					x.delete();
					if (x.exists()) {
						System.out.println("Error: file cannot be deleted");
						throw new IOException("Error: file cannot be deleted");
					}
					//this would tell the parent window that the process completed successfully
					setCanceled(false);
					//this closes the window
					taskFinished();
					System.out.println("SmartProgressWindow demo completed successfully");
					System.exit(0);
				} catch (Exception err) {
					err.printStackTrace();
					if (xRaf != null) {
						try {
							xRaf.close();
						} catch (Exception e) {}
					}
					if (x != null) {
						x.delete();
					}
					System.out.println("Error in file writing");
					System.exit(1);
				}
            }
		};
		//start the thread and show the window
        Thread t = new Thread(q);
        t.start();
        this.setVisible(true);
	}
	
}
