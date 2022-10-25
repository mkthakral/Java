package loblaw.image.compare.helper;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import loblaw.image.compare.constant.ImageUtilityConstants;
import loblaw.image.compare.helper.format.ImageProcessOuput;

/**
 * @author Mohit Kumar
 * 
 *         Description: This class handles processing of input file, comparing
 *         images and creating output file/report
 * 
 */

public class ImageProcessor {

	/**
	 * This is the primary method in the class which recieves the input file,
	 * process it and return error or output file
	 * 
	 * @param csvInput: CSV Input Absolute Path
	 * @return object of FileProcessOuput, custom output format to return error or
	 *         output file path
	 * @throws Exception: To handle all unexpected exceptions/errors
	 * 
	 */
	public ImageProcessOuput processInput(String csvInput) throws Exception {

		// Checking if file is valid or not
		if (!isFileValid(csvInput)) {
			return new ImageProcessOuput(ImageUtilityConstants.ERROR_EMPTY_FILE, "");
		}

		BufferedReader bufferedReader = null;
		// List to store output report
		List<String[]> outputList = new ArrayList<>();
		// Store Splitted row/line in input file in the form of array
		String[] inputRow;
		// Store output row
		String[] outputRow;
		// Difference between two images in percentage, stored as String
		String difference;
		// Store un-splitted row/line in input file
		String currentInputRow = "";
		// Create references to note start and end time of execution
		Instant executionStartTime, executionFinishTime;
		// row counter
		int rowCounter = 1;
		// Read and process input file
		try {

			// create buffered reader of input file
			bufferedReader = new BufferedReader(new FileReader(csvInput));
			// Loop input file, row wise
			while ((currentInputRow = bufferedReader.readLine()) != null) {
				if (rowCounter == 1) {
					outputList.add(ImageUtilityConstants.OUTPUT_FILE_TABLE_HEADING);
				} else {
					executionStartTime = Instant.now();
					outputRow = new String[4];
					// Breaking input row into array
					inputRow = currentInputRow.split(ImageUtilityConstants.COMMA);

					// Preparing first two columns for output file i.e. image paths
					outputRow[0] = ((inputRow[0] == null || inputRow[0] == "") ? "" : inputRow[0]);
					outputRow[1] = (inputRow.length < 2) ? "" : inputRow[1];

					// Preparing column three for output file i.e. difference in image
					String validateRowResponse = validateInputRow(inputRow);
					if (validateRowResponse.isEmpty()) {
						difference = compareImage(inputRow[0], inputRow[1]);
						outputRow[2] = (difference.equals(ImageUtilityConstants.ERROR_CODE_DIMENSION_MISMATCH))
								? ImageUtilityConstants.ERROR_DIMENSION_MISMATCH
								: difference;
					} else {
						outputRow[2] = validateRowResponse;
					}

					// Preparing column four for output file i.e. execution time
					executionFinishTime = Instant.now();
					outputRow[3] = (double) Duration.between(executionStartTime, executionFinishTime).toMillis() / 1000
							+ "";

					// Adding prepared row in the list
					outputList.add(outputRow);
				}
				rowCounter++;

			}
		} finally {
			// closing file connection
			bufferedReader.close();
		}

		// Get output file absolute path
		String outputFile = getOutputFileName(csvInput);

		// Create output CSV and write processed input into it
		writeCSV(outputFile, outputList);

		// returning process result
		return new ImageProcessOuput(ImageUtilityConstants.ERROR_NONE, outputFile);

	}

	/**
	 * Description: This method checks file is valid or not by checking it's size
	 * 
	 * @param csvInput: CSV Input file
	 * @return boolean: if file is valid or not
	 */
	private boolean isFileValid(String csvInput) {
		// check if file is blank
		if ((new File(csvInput)).length() == 0) {
			return false;
		}
		return true;
	}

	/**
	 * Description: This method generates output CSV file name by using input file
	 * name
	 * 
	 * @param csvInput: CSV Input file
	 * @return String: Output CSV Absolute Path
	 */
	private String getOutputFileName(String csvInput) {
		return csvInput.replace(ImageUtilityConstants.EXTENSION_CSV, "-"
				+ new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + ImageUtilityConstants.EXTENSION_CSV);
	}

	/**
	 * This method will create the output CSV file on file system and write
	 * processed output in it
	 * 
	 * @param outputFile
	 * @param output
	 * @throws IOException
	 */
	private void writeCSV(String outputFile, List<String[]> output) throws IOException {
		File csvOutputFile = new File(outputFile);
		csvOutputFile.createNewFile();
		try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
			output.stream().map(this::convertToCSV).forEach(pw::println);
		}

	}

	/**
	 * This method will create a row of output file by converting a String Array to
	 * Comma separated String
	 * 
	 * @param Row to be written to output file in the form of String Array
	 * @return Row for output file in form of String
	 */
	private String convertToCSV(String[] data) {
		return Stream.of(data).collect(Collectors.joining(ImageUtilityConstants.COMMA));
	}

	/**
	 * Description: Validate if images's name is given in file and the same image
	 * exist on file system
	 * 
	 * @param inputRow: Images' path of one row in Input file
	 * @return
	 */
	private String validateInputRow(String[] inputRow) {
		// Check if image 1 path is given in file
		if (inputRow[0] == null || inputRow[0].isEmpty()) {
			return ImageUtilityConstants.ERROR + ImageUtilityConstants.FILE_ONE
					+ ImageUtilityConstants.ERROR_IMAGE_NOT_MENTIONED;
		}
		// Check if image 2 path is given in file
		else if (inputRow.length < 2) {
			return ImageUtilityConstants.ERROR + ImageUtilityConstants.FILE_TWO
					+ ImageUtilityConstants.ERROR_IMAGE_NOT_MENTIONED;
		}
		// Check if image 1 exist on path mentioned in input file
		else if (!(new File(inputRow[0]).exists())) {
			return ImageUtilityConstants.ERROR + ImageUtilityConstants.FILE_ONE
					+ ImageUtilityConstants.ERROR_IMAGE_FILE_MISSING;
		}
		// Check if image 2 exist on path mentioned in input file
		else if (!(new File(inputRow[1]).exists())) {
			return ImageUtilityConstants.ERROR + ImageUtilityConstants.FILE_TWO
					+ ImageUtilityConstants.ERROR_IMAGE_FILE_MISSING;
		}

		// return no error if no error was found
		return ImageUtilityConstants.ERROR_NONE;
	}

	/**
	 * Description: This method executes the algorithm on two input images and
	 * calculate difference between them
	 * 
	 * @param imageOnePath
	 * @param imageTwoPath
	 * @return
	 * @throws IOException
	 */
	private String compareImage(String imageOnePath, String imageTwoPath) throws IOException {
		// Buffered Image Reference for two input images
		BufferedImage image1 = null;
		BufferedImage image2 = null;
		// Color References for images
		Color colorImage1;
		Color colorImage2;

		// To format output percentage
		DecimalFormat differenceFormat = new DecimalFormat("#.##");

		// Creating File objects of images
		File imageFile1 = new File(imageOnePath);
		File ImageFile2 = new File(imageTwoPath);

		// Reading input images
		image1 = ImageIO.read(imageFile1);
		image2 = ImageIO.read(ImageFile2);

		// Getting dimensions/pixel count of image 1
		int imageOneHeight = image1.getHeight();
		int imageOneWidth = image1.getWidth();

		// Getting dimensions/pixel count of image 2
		int imageTwoHeight = image2.getHeight();
		int imageTwoWidth = image2.getWidth();

		if ((imageOneWidth != imageTwoWidth) || (imageOneHeight != imageTwoHeight))
			return ImageUtilityConstants.ERROR_CODE_DIMENSION_MISMATCH;
		else {
			long pixelDifference = 0;
			for (int y = 0; y < imageOneHeight; y++) {
				for (int x = 0; x < imageOneWidth; x++) {
					// Create color object for both images
					colorImage1 = new Color(image1.getRGB(x, y));
					colorImage2 = new Color(image2.getRGB(x, y));

					// Adding all differences in Red, Green, Blue
					pixelDifference += Math.abs(colorImage1.getRed() - colorImage2.getRed());
					pixelDifference += Math.abs(colorImage1.getGreen() - colorImage2.getGreen());
					pixelDifference += Math.abs(colorImage1.getBlue() - colorImage2.getBlue());
				}
			}

			// Total pixel count = width * height * 3 (R+G+B)
			double totalPixels = imageOneWidth * imageOneHeight * 3;

			// Calculating average difference of pixels
			double averageDifferenceOfPixels = pixelDifference / totalPixels;

			// There are 255 values of pixels in total
			double differencePercentage = (averageDifferenceOfPixels / 255) * 100;

			// return difference percentage between two input images
			return differenceFormat.format(differencePercentage);
		}
	}
}