package folyoz.processor.helper;

import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailHelper {

	private final static Logger logger = Logger.getLogger(EmailHelper.class.getName());
	
	private final static String EMAIL_BODY_SENT_BY_BOT= "[NOTE] This email is sent by a automated bot. If you receive too many of these outside of development cycle, you should inform dev team as this can crush the server and your email client too.";
	
	public void sendEmail(FileHandler logFileHandler, Properties propertyFile, String environment, String emailSubject, String emailBody) {
		logger.addHandler(logFileHandler);
		logger.info("[FOLYOZ] [ERROR] Going to send an email as an error has occured.");
		String FROM = propertyFile.getProperty("email-sender-address");
        String FROMNAME = propertyFile.getProperty("email-sender-name");
        String TO = propertyFile.getProperty("email-recipent-address-" + environment);
        String SMTP_USERNAME = propertyFile.getProperty("email-aws-ses-username");
        String SMTP_PASSWORD = propertyFile.getProperty("email-aws-ses-password");
        String HOST = propertyFile.getProperty("email-aws-ses-hostname");
   
        //This port is used by STARTTLS to encrypt the email
        int PORT = Integer.parseInt(propertyFile.getProperty("email-aws-ses-port"));
        
        String SUBJECT = emailSubject + " [" + environment.toUpperCase() + "]";
        
        StringBuffer lEmailBodyStringBuffer = new StringBuffer();

        lEmailBodyStringBuffer.append(emailBody);
        lEmailBodyStringBuffer.append(EMAIL_BODY_SENT_BY_BOT);
        
        String BODY = lEmailBodyStringBuffer.toString();

        // Create a Properties object to contain connection configuration information.
        Properties props = System.getProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", PORT);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        
        try {
        	// Create a Session object to represent a mail session with the specified properties.
        	Session session = Session.getDefaultInstance(props);
        	
        	//Create a message with the specified information.
        	MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(FROM,FROMNAME));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(TO));
            msg.setSubject(SUBJECT);
            msg.setContent(BODY,"text/html");
            
            // Create a transport.
            Transport transport = session.getTransport();
            logger.info("Sending Email...");
            
            //Connect to Amazon SES using the SMTP username and password you specified above.
            transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);
            
            //Send the email.
            transport.sendMessage(msg, msg.getAllRecipients());

            logger.info("Sent Email");
            
            //Close and terminate the connection.
            transport.close();
            
        }catch(Exception e) {
			logger.severe("[FOLYOZ] [ERROR] Error in sending email in case of exception occured in processing queue message");
			logger.severe(e.getMessage());
        }
        
        
        
	}
}
