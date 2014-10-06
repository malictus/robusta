package malictus.robusta.mail;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * A class that automates much of the process of sending simple emails from Java programs.
 */
public class SmartEmailer {
	
	private String hostSMTP;
	private String fromEmailAdress;
	private List<String> ccEmailAddresses;

	/**
	 * Initiate a SmartEmailer object.
	 * 
	 * @param hostSMTP The host smtp server for sending the email
	 * @param fromEmailAdress The 'from' email address
	 */
	public SmartEmailer(String hostSMTP, String fromEmailAdress) {
		this.hostSMTP = hostSMTP;
		this.fromEmailAdress = fromEmailAdress;
	}
	
	/**
	 * Adds an address who will be send a CC (carbon copy) of all
	 * subsequent e-mails.
	 * @param cc an email address to be intered in the CC field.
	 */
	public void addCC(String cc) {
	    if (this.ccEmailAddresses == null) {
	        this.ccEmailAddresses = new ArrayList<String>();
	    }
	    this.ccEmailAddresses.add(cc);
	}
	
	/**
	 * Send an email to a single recipient.
	 * 
	 * @param toEmailAdress The 'to' email address
	 * @param subject the subject of the email
	 * @param messageText the body of the email
	 * @throws MessagingException if error occurs in email transmission
	 */
	public void send (String toEmailAdress, String subject, String messageText) throws MessagingException {
		send(new String[] {toEmailAdress}, subject, messageText);
	}
	
	/**
	 * Send an email to a group of recipients.
	 * 
	 * @param toEmailAddresses A string array of 'to' email addresses
	 * @param subject subject of the email
	 * @param messageText the body of the email
	 * @throws MessagingException if error occurs in email transmission
	 */
	public void send(String[] toEmailAddresses, String subject, String messageText) throws MessagingException {
		Properties props = System.getProperties();
		props.put("mail.smtp.host", hostSMTP);
		Session session = Session.getDefaultInstance(props, null);
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(fromEmailAdress));
		int counter = 0;
		while (counter < toEmailAddresses.length) {
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmailAddresses[counter]));
			counter = counter + 1;
		}
		if (this.ccEmailAddresses != null) {
		    for (String cc : this.ccEmailAddresses) {
		        message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
		    }
		}
		message.setSubject(subject);
		message.setText(messageText);
		Transport.send(message);
	}
	
}
