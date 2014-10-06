package malictus.robusta.demo;

import javax.mail.MessagingException;
import malictus.robusta.mail.*;

/**
 * Demonstrate and test the SmartEmailer class.
 */
public class SmartEmailerDemo {
	
	public final static String HOST_SMTP = "SMTP SERVER GOES HERE";
	public final static String FROM_EMAIL = "FROM EMAIL GOES HERE";
	public final static String TO_EMAIL = "TO EMAIL GOES HERE";
	public final static String SUBJECT_LINE = "subject";
	public final static String MESSAGE = "message goes here";

	/**
	 * Run to demonstrate the SmartEmailer
	 *
	 * @param args not currently used
	 */
	public static void main(String[] args) {
		SmartEmailer emailer = new SmartEmailer(HOST_SMTP, FROM_EMAIL);
		try {
			System.out.println("Sending email");
			emailer.send(TO_EMAIL, SUBJECT_LINE, MESSAGE);
			System.out.println("Email sent successfully");
		} catch (MessagingException err) {
			err.printStackTrace();
			System.out.println("Error sending email\n" + err.getMessage());
		}
	}
	
}
