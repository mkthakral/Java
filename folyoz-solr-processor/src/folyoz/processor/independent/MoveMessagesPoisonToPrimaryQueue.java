package folyoz.processor.independent;

import java.io.FileReader;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.queue.CloudQueue;
import com.microsoft.azure.storage.queue.CloudQueueClient;
import com.microsoft.azure.storage.queue.CloudQueueMessage;

public class MoveMessagesPoisonToPrimaryQueue {

	private final static Logger logger = Logger.getLogger(SendEmail.class.getName());
	static FileHandler logFileHandler;

	// File size in bytes
	public static final int LOG_FILE_SIZE = 50000;
	// Count of log files to mainatain
	public static final int LOG_FILE_COUNT = 5;

	public static final String PROPERTY_FILE = "folyoz-properties.properties";
	static Properties propertyFile;

	public static void main(String[] args) {

		logger.info("Start: MoveMessagesPoisonToPrimaryQueue");

		if (args.length < 1) {
			System.out.println("Please pass dev or live as environment as argument to this class.");
		} else {

			try {
				FileReader reader = new FileReader(PROPERTY_FILE);
				propertyFile = new Properties();
				propertyFile.load(reader);

				logFileHandler = new FileHandler(propertyFile.getProperty("log-move-messages-poison-to-primary-queue"),
						LOG_FILE_SIZE, LOG_FILE_COUNT, true);
				logFileHandler.setFormatter(new SimpleFormatter());
				logger.addHandler(logFileHandler);

				String queueName = propertyFile.getProperty("azure-queue-name-" + args[0]);
				String queueNamePoison = propertyFile.getProperty("azure-queue-name-" + args[0]) + "-poison";

				logger.info("Primary Queue: " + queueName);
				logger.info("Poison Queue: " + queueNamePoison);

				// Retrieve storage account from connection-string.
				CloudStorageAccount storageAccount = CloudStorageAccount
						.parse(propertyFile.getProperty("azure-queue-storage-connection-string"));

				// Create the queue client.
				CloudQueueClient queueClient = storageAccount.createCloudQueueClient();

				// Retrieve a reference to a queue.
				CloudQueue queuePrimary = queueClient.getQueueReference(queueName);

				// Retrieve a reference to a queue.
				CloudQueue queuePoison = queueClient.getQueueReference(queueNamePoison);

				CloudQueueMessage message;

				while (true) {
					try {
						while (queuePoison.peekMessage() != null) {
							message = queuePoison.retrieveMessage();
							if (message != null) {
								logger.info("Processing Message: " + message.getMessageContentAsString());
								// Add message in primary queue
								logger.info("Adding message to primary queue");
								queuePrimary.addMessage(message);
								/*
								 * logger.info("Time out of 60 seconds"); TimeUnit.SECONDS.sleep(60);
								 */ // Delete message from Poison queue
								logger.info("Delete message from Poison queue: " + message.getMessageContentAsString());
								queuePoison.deleteMessage(message);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				logger.info("Error: " + e.getMessage());
			}
		}

		logger.info("End: MoveMessagesPoisonToPrimaryQueue");
	}

}
