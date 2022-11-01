package folyoz.processor;

import java.io.FileReader;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.queue.CloudQueue;
import com.microsoft.azure.storage.queue.CloudQueueClient;
import com.microsoft.azure.storage.queue.CloudQueueMessage;

import folyoz.processor.helper.EmailHelper;
import folyoz.processor.solr.SolrProcessor;

public class MainApplication {

	public static final String storageConnectionString = "";
	private final static Logger logger = Logger.getLogger(MainApplication.class.getName());
	// 1 GB logs
	public static final int LOG_FILE_SIZE = 10485760;
	// Count of log files to mainatain
	public static final int LOG_FILE_COUNT = 500;

	public static final String PROPERTY_FILE = "folyoz-properties.properties";
	static Properties propertyFile;
	static FileHandler logFileHandler;
	static String environment;

	public static void main(String[] args) {
		try {

			SolrProcessor solrProcessor = new SolrProcessor();
			FileReader reader = new FileReader(PROPERTY_FILE);
			String queueName = null;
			propertyFile = new Properties();
			propertyFile.load(reader);

			System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");
			logFileHandler = new FileHandler(propertyFile.getProperty("log-file"), LOG_FILE_SIZE, LOG_FILE_COUNT, true);
			logFileHandler.setFormatter(new SimpleFormatter());
			logger.addHandler(logFileHandler);

			logger.info("Starting Application");

			if (args.length < 1) {
				logger.severe("Pass environment dev or live to this JAR while running it.");
			} else {
				environment = args[0];
				queueName = propertyFile.getProperty("azure-queue-name-" + environment);
				logger.info(" Enviroment Name: " + args[0] + " Queue Name: " + queueName);

				// Retrieve storage account from connection-string.
				CloudStorageAccount storageAccount = CloudStorageAccount
						.parse(propertyFile.getProperty("azure-queue-storage-connection-string"));

				// Create the queue client.
				CloudQueueClient queueClient = storageAccount.createCloudQueueClient();

				// Retrieve a reference to a queue.
				CloudQueue queue = queueClient.getQueueReference(queueName);

				CloudQueueMessage message;

				while (queue.peekMessage() != null) {
					message = queue.retrieveMessage();
					if (message != null) {
						logger.info("QUEUE: RECIEVED: " + " : " + message.getMessageId() + " : "
								+ message.getMessageContentAsString());
						boolean solrRequestStatus = solrProcessor.updateRequest(propertyFile, message, logFileHandler,
								environment);
						if (solrRequestStatus) {
							logger.info("QUEUE: DELETING: " + message.getMessageId() + " : "
									+ message.getMessageContentAsString());
							queue.deleteMessage(message);
							logger.info("QUEUE: DELETED: " + message.getMessageId() + " : "
									+ message.getMessageContentAsString());
						}
					}

				}

			}
		} catch (Exception e) {
			logger.severe("EXCEPTION in application: " + e.getMessage());
			(new EmailHelper()).sendEmail(logFileHandler, propertyFile, environment, "Error in Processing Solr Queue",
					e.getMessage());
			;
		}

	}

}
