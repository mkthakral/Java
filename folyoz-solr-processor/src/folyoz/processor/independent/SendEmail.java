package folyoz.processor.independent;

import java.io.FileReader;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import folyoz.processor.helper.EmailHelper;

public class SendEmail {

	private final static Logger logger = Logger.getLogger(SendEmail.class.getName());
	static FileHandler logFileHandler;

	// File size in bytes
	public static final int LOG_FILE_SIZE = 50000;
	// Count of log files to mainatain
	public static final int LOG_FILE_COUNT = 5;

	//public static final String PROPERTY_FILE = "folyoz-properties.properties";
	static Properties propertyFile;

	public static void main(String[] args) {

		logger.info("Start: SendEmail");

		if (args.length < 4) {
			System.out.println("Pass Arg1: Path of Property File Arg2: Environment(dev/live) Arg3: Email Subject, Arg4: Email Body");
		} else {

			try {
				
				String fileName = args[0];
				String environment = args[1];
				String emailSubject = args[2];
				String emailBody = args[3];

				
				FileReader reader = new FileReader(fileName);
				propertyFile = new Properties();
				propertyFile.load(reader);

				logFileHandler = new FileHandler(propertyFile.getProperty("log-file-send-email"), LOG_FILE_SIZE,
						LOG_FILE_COUNT, true);
				logFileHandler.setFormatter(new SimpleFormatter());
				logger.addHandler(logFileHandler);

				EmailHelper emailHelper = new EmailHelper();

				logger.info("Going to send email");

				logger.info("Email Subject: " + emailSubject);
				logger.info("Email Body: " + emailBody);

				emailHelper.sendEmail(logFileHandler, propertyFile, environment, emailSubject, emailBody);
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("Error in Send email: " + e.getMessage());
			}
		}

		logger.info("End: SendEmail");
	}

}
