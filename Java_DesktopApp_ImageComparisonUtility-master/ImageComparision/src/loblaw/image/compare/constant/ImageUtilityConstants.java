package loblaw.image.compare.constant;

/**
 * @author Mohit Kumar
 * 
 *         Description: This class holds labels, error messages and bit of
 *         configuration in form of static variables
 * 
 */

public class ImageUtilityConstants {
	// Configuration
	public static String VERSION = "(Version-1)";
	public static int IMAGE_WIDTH = 700;
	public static int IMAGE_HEIGHT = 150;
	public static String EXTENSION_CSV = ".csv";
	public static String[] OUTPUT_FILE_TABLE_HEADING = { "image1", "image2", "similar", "elapsed" };

	// Labels & others
	public static String FRAME_TITLE = "Image Comparison Tool";
	public static String BUTTON_BROWSE = "Browse Input CSV File";
	public static String BUTTON_RUN = "Process Selected CSV";
	public static String LABEL_BLANK = "No File Selected";
	public static String FILE_EXPLORER_FILE_TYPE = "CSV Files (*.csv)";

	// Errors
	public static String ERROR_NONE = "";
	public static String ERROR_EMPTY_FILE = "File is blank";
	public static String ERROR = "Error: ";
	public static String ERROR_FILE_NOT_SELECTED = "File not selected";
	public static String ERROR_DIMENSION_MISMATCH = "Image Dimension Mismatch";
	public static String ERROR_IMAGE_NOT_MENTIONED = "Image path missing in input file";
	public static String ERROR_IMAGE_FILE_MISSING = "Image not found at specified location";
	public static String ERROR_FILE_NAME_INVALID = "Please select a CSV file";
	public static String ERROR_CODE_DIMENSION_MISMATCH = "ERROR_DIMENSION_MISMATCH";
	public static String EXCEPTION_GENERAL = "There has been an exception while executing this request";

	// Other User Messages
	public static String SUCCESSFULLY_PROCESDED = "Successfully processed. Results are in file: ";
	public static String IN_PROGRESS = "Processing...";

	// Others
	public static String FILE_ONE = "File 1: ";
	public static String FILE_TWO = "File 2: ";
	public static String COMMA = ",";
	public static String STRING_BLANK = "";
	public static String STRING_SPACE = " ";

}
