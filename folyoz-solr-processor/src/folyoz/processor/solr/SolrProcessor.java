package folyoz.processor.solr;

import java.io.IOException;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.json.JSONObject;

import com.microsoft.azure.storage.queue.CloudQueueMessage;

import folyoz.processor.helper.EmailHelper;
import folyoz.processor.pojo.CustomProduct;

public class SolrProcessor {

	SolrClient solrClient;

	private final static Logger logger = Logger.getLogger(SolrProcessor.class.getName());

	String timeStamFileName = "timeStampFile.txt";
	
	LocalDateTime lastEmailSentAt;
	
	public void updateSolr(Properties propertyFile, List<CustomProduct> productList)
			throws SolrServerException, IOException {

		String urlString = propertyFile.getProperty("solr-endpoint");

		SolrClient solrClient = new HttpSolrClient.Builder(urlString).build();

		/*
		 * //Preparing the Solr document SolrInputDocument doc = new
		 * SolrInputDocument();
		 * 
		 * UpdateRequest updateRequest = new UpdateRequest(); updateRequest.setAction(
		 * UpdateRequest.ACTION.COMMIT, false, false); SolrInputDocument
		 * myDocumentInstantlycommited = new SolrInputDocument();
		 * 
		 * myDocumentInstantlycommited.addField("id", "002");
		 * myDocumentInstantlycommited.addField("name", "Rahman");
		 * myDocumentInstantlycommited.addField("age","27");
		 * myDocumentInstantlycommited.addField("addr","hyderabad"); updateRequest.add(
		 * myDocumentInstantlycommited);
		 */
		solrClient.deleteByQuery("*");

		/*
		 * for (CustomProduct product : productList) {
		 * 
		 * solrClient.addBean(product); }}
		 */

		for (CustomProduct product : productList) {

			SolrInputDocument doc = new SolrInputDocument();

			// Adding fields to the document
			doc.addField("sku", product.getSku());
			doc.addField("name", product.getName());
			doc.addField("image", product.getImage());

			doc.addField("thumbnail", product.getThumbnail());
			doc.addField("category", product.getCategory());
			doc.addField("attributeStyle", product.getAttributeStyle().split(","));
			doc.addField("attributeCategory", product.getAttributeCategory().split(","));
			doc.addField("artistID", product.getArtistID());
			doc.addField("artistName", product.getArtistName());
			doc.addField("artistCity", product.getArtistCity());
			doc.addField("artistState", product.getArtistState());
			doc.addField("artistCountry", product.getArtistCountry());
			doc.addField("artistGender", product.getArtistGender());
			doc.addField("artistEthnicity", product.getArtistEthnicity());

			solrClient.add(doc);
		}

		solrClient.commit();
		// UpdateResponse rsp = updateRequest.process(solrClient);
		System.out.println("Documents Updated");
	}

	public boolean updateRequest(Properties propertyFile, CloudQueueMessage queueMessage, FileHandler logFileHandler,
			String environment) throws IOException, ParseException {
		boolean returnStatus = true;
		try {
			logger.addHandler(logFileHandler);
			logger.info("Solr: PROCESSING: " + queueMessage.getMessageId() + " : "
					+ queueMessage.getMessageContentAsString());
			String solrURL = propertyFile.getProperty("solr-endpoint-" + environment);
			this.solrClient = new HttpSolrClient.Builder(solrURL).build();

			String message = queueMessage.getMessageContentAsString();
			JSONObject jsonObject = new JSONObject(message);
			String action = (String) jsonObject.get("action");
			JSONObject jsonProduct = (JSONObject) jsonObject.get("product");

			if (action.equals("ADD")) {
				actiondAdd(jsonProduct);
			} else if (action.equals("DELETE")) {
				actionDelete(jsonProduct);
			} else if (action.equals("DELETE_ALL")) {
				actionDeleteAll();
			} else if (action.equals("UPDATE")) {
				actionDeleteAndAdd(jsonProduct);
			}

			logger.info("Solr: PROCESSED: " + queueMessage.getMessageId() + " : "
					+ queueMessage.getMessageContentAsString());
		} catch (Exception e) {
			returnStatus = false;
			logger.warning("EXCEPTION in application: " + e.getMessage());

			if (shouldWeSendErrorEmail()) {
				(new EmailHelper()).sendEmail(logFileHandler, propertyFile, environment,
						"Error in Processing Solr Queue", e.getMessage());
				;
			}

		}
		return returnStatus;
	}

	private boolean shouldWeSendErrorEmail() {
		
		if(lastEmailSentAt == null) {
			logger.info("[Error Email] Send Email");
			lastEmailSentAt = LocalDateTime.now();
			return true;
		}else {
			LocalDateTime timeNow = LocalDateTime.now();
			Duration duration = Duration.between(timeNow, lastEmailSentAt);
			long diff = Math.abs(duration.toMinutes());
			
			//Send email every 2 minutes
			if(diff > 1) {
				logger.info("[Error Email] Time Diff Greater than 1. Send Email...");
				//Reset time
				lastEmailSentAt = timeNow;
				return true;
			}else {
				logger.info("[Error Email] Time Diff less than 1. DO NOT Send Email...");
				return false;
			}
		}
		
	}

	private void actionDelete(JSONObject product) throws SolrServerException, IOException {
		logger.info("Solr: DELETE TRY: Product Id: " + product.get("productId"));
		if (solrClient.getById((String) product.get("productId")) != null) {
			logger.info("Solr: document found, will delete now: Product Id: " + product.get("productId"));
			solrClient.deleteById((String) product.get("productId"));
			solrClient.commit();
			logger.info("Solr: DELETED: Product Id: " + product.get("productId"));
		} else {
			logger.info("Solr: DELETE: could not find product: Product Id: " + product.get("productId"));
		}

	}
	
	private void actionDeleteAll() throws SolrServerException, IOException {
		logger.info("Solr: DELETING ALL PRODUCTS");
		solrClient.deleteByQuery("*");
		solrClient.commit();
		logger.info("Solr: DELETED ALL PRODUCTS");
	}

	private void actiondAdd(JSONObject product) throws SolrServerException, IOException {
		logger.info("Solr: Adding: Product Id: " + product.get("productId"));
		SolrInputDocument doc = new SolrInputDocument();

		String artistCity = (product.get("artistCity") == JSONObject.NULL
				|| ((String) product.get("artistCity")).isEmpty()) ? "Not Available"
						: (String) product.get("artistCity");
		String artistState = (product.get("artistState") == JSONObject.NULL
				|| ((String) product.get("artistState")).isEmpty()) ? "Not Available"
						: (String) product.get("artistState");
		String artistCountry = (product.get("artistCountry") == JSONObject.NULL
				|| ((String) product.get("artistCountry")).isEmpty()) ? "Not Available"
						: (String) product.get("artistCountry");
		String artistGender = (product.get("artistGender") == JSONObject.NULL
				|| ((String) product.get("artistGender")).isEmpty()) ? "Not Available"
						: (String) product.get("artistGender");
		String artistEthnicity = (product.get("artistEthnicity") == JSONObject.NULL
				|| ((String) product.get("artistEthnicity")).isEmpty()) ? "Not Available"
						: (String) product.get("artistEthnicity");

		// System.out.println("Product Id: " + product.getString("productId") + "
		// artistEthnicity: " + artistEthnicity + " product.get(\"artistEthnicity\"): "
		// + product.get("artistEthnicity"));

		doc.addField("productId", product.get("productId"));
		doc.addField("name", product.get("name"));
		doc.addField("image", product.get("image"));
		doc.addField("imageModal", product.get("imageModal"));
		doc.addField("imageModalHeight", product.get("imageModalHeight"));
		doc.addField("imageModalWidth", product.get("imageModalWidth"));
		doc.addField("categoryId", product.get("categoryId"));
		doc.addField("categoryName", product.get("categoryName"));
		doc.addField("attributeStyle", product.get("style"));
		doc.addField("attributeCategory", product.get("categories"));
		doc.addField("artistID", product.get("artistId"));
		doc.addField("artistName", product.get("artistName"));
		doc.addField("artistCity", artistCity);
		doc.addField("artistState", artistState);
		doc.addField("artistCountry", artistCountry);
		doc.addField("artistGender", artistGender);
		doc.addField("artistEthnicity", artistEthnicity);
		doc.addField("productAvailabilty", product.get("availability"));
		doc.addField("productClient", product.get("client"));
		doc.addField("artistImage", product.get("artistImage"));
		doc.addField("artistEducation", product.get("artistEducation"));
		doc.addField("artistPortfolioLink", product.get("artistPortfolioLink"));
		doc.addField("keywords", product.get("productKeywords"));

		solrClient.add(doc);
		solrClient.commit();
		logger.info("Solr: Added: Product Id: " + product.get("productId"));

	}

	private void actionDeleteAndAdd(JSONObject product) throws SolrServerException, IOException {
		logger.info("Solr: Updating: Product Id: " + product.get("productId"));
		actionDelete(product);
		actiondAdd(product);
		logger.info("Solr: Updated: Product Id: " + product.get("productId"));
	}

}
