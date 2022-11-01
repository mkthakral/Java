package folyoz.processor.independent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

import folyoz.processor.helper.EmailHelper;

public class CheckAndRunJAR {
	
	private static final String JAR_NAME = "Processor.jar";
	public static final String PROPERTY_FILE = "folyoz-properties.properties";
	
	public static void main(String[] args) throws IOException {
	
		FileReader lFileReader = new FileReader(PROPERTY_FILE);
		Properties lPropertyFile = new Properties();
		lPropertyFile.load(lFileReader);
		
		
		String lLine;
		String lEnvironmentName = null;
		if(args.length < 1) {
			System.out.println("Please pass the first argument i.e. environment i.e. dev or live");
			System.exit(0);
		}else {
			lEnvironmentName = args[0];
			System.out.println("Environment: " + lEnvironmentName);
		}
		
		Process lProcess = Runtime.getRuntime().exec("/bin/ps ux");
		BufferedReader lBufferedReader = new BufferedReader(new InputStreamReader(lProcess.getInputStream()));
		int count = 0;
		while((lLine = lBufferedReader.readLine()) != null){
			if(lLine.contains(JAR_NAME + " " + lEnvironmentName)) {
				count++;
			}
		}
		
		System.out.println("Total Count: " + count);
		
		if(count >= 1) {
			System.out.println("Process is running. Taking no action.");
		}else {
			String lJARLocation = lPropertyFile.getProperty("jar-location");
			
			//Sending Email
			/*
			 * System.out.println("Going to issue Email command"); FileHandler
			 * logFileHandler;
			 * System.setProperty("java.util.logging.SimpleFormatter.format",
			 * "%1$tF %1$tT %4$s %2$s %5$s%6$s%n"); logFileHandler = new
			 * FileHandler("tmp.log", 100, 1, true); logFileHandler.setFormatter(new
			 * SimpleFormatter()); EmailHelper lEmailHelper = new EmailHelper();
			 * lEmailHelper.sendEmail(logFileHandler, lPropertyFile, lEnvironmentName,
			 * "[Monitor] Solr App for env "+ lEnvironmentName + " not running.",
			 * "As an obidient program, let me start this app for you.");
			 * System.out.println("Email command issued");
			 */
			
			//Start JAR
			String lShellCommandToRunJAR = "nohup java -jar " + lJARLocation + "/" + JAR_NAME + " " + lEnvironmentName + " &";
			System.out.println("Going to execute shell command: " + lShellCommandToRunJAR);
			Runtime.getRuntime().exec(lShellCommandToRunJAR);
		}
		
		
		lBufferedReader.close();
	}
}
