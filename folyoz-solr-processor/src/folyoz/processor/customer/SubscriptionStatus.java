package folyoz.processor.customer;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class SubscriptionStatus {
	public List<Integer> getArtistSubscription(Properties propertyFile) throws FileNotFoundException {
		//Map<Integer, Boolean> subscriptionList = new HashMap<Integer, Boolean>();
		List<Integer> activeSubscriptionArtistIds = new ArrayList<Integer>();
		String subscriptionTableCSV = propertyFile.getProperty("subscription-table");
		//int count = 0;
		Scanner scanner = new Scanner(new File(subscriptionTableCSV));
		while (scanner.hasNext()) {
			
			String currentRow = scanner.nextLine();
			currentRow = currentRow.replaceAll("\"", "");
			String[] currentRowSplit = currentRow.split(",");
			
			String artistID = currentRowSplit[0];
			String subscriptionStatus = currentRowSplit[1];
			//System.out.println(++count);
			if(subscriptionStatus.equalsIgnoreCase("active") && !artistID.equalsIgnoreCase("NULL") && !activeSubscriptionArtistIds.contains(Integer.valueOf(artistID))) {
				//System.out.println("Adding: " + artistID);
				activeSubscriptionArtistIds.add(Integer.valueOf(artistID));
			}
			
		}
		scanner.close();
		return activeSubscriptionArtistIds;
	}
}
