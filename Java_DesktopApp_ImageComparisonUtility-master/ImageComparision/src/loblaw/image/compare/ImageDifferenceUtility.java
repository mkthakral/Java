package loblaw.image.compare;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;

import loblaw.image.compare.constant.ImageUtilityConstants;
import loblaw.image.compare.helper.ImageProcessor;
import loblaw.image.compare.helper.format.ImageProcessOuput;

/**
 * @author Mohit Kumar
 * 
 * 
 *         Description: This class is the entry point for Image Comparison
 *         Utility This class would create a Java Swing Form which would let
 *         users use this application
 */
public class ImageDifferenceUtility extends JFrame implements ActionListener {

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	// Creating reference for image processor
	private ImageProcessor imageProcessor;

	// Label on user form used in different methods
	static JLabel jLabel;

	// Variable to store file selected
	static String inputFileSelected;

	/**
	 * Creating object of processor in constructor of Main file of this tool
	 */
	public ImageDifferenceUtility() {
		imageProcessor = new ImageProcessor();
	}

	/**
	 * Entry method for this utility and class This will create create form for user
	 * to use this utility
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// Creating JFrame for user form
		JFrame jFrame = new JFrame(ImageUtilityConstants.FRAME_TITLE + ImageUtilityConstants.STRING_SPACE + ImageUtilityConstants.VERSION);
		// Set the size of the frame
		jFrame.setSize(ImageUtilityConstants.IMAGE_WIDTH, ImageUtilityConstants.IMAGE_HEIGHT);
		// Set default close option
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Creating label for user form
		jLabel = new JLabel(ImageUtilityConstants.LABEL_BLANK);
		// Creating browse button
		JButton browseButton = new JButton(ImageUtilityConstants.BUTTON_BROWSE);
		// Creating process input button
		JButton processInputButton = new JButton(ImageUtilityConstants.BUTTON_RUN);

		// Creating object of this class
		ImageDifferenceUtility imageDifferenceUtility = new ImageDifferenceUtility();
		// Add action listeners to identify button clicks
		browseButton.addActionListener(imageDifferenceUtility);
		processInputButton.addActionListener(imageDifferenceUtility);

		// Create JPanel to add the buttons and labels
		JPanel jPanel = new JPanel();
		
		
		// Add all elements to the panel
		jPanel.add(jLabel);
		jPanel.add(browseButton);
		jPanel.add(processInputButton);

		// Add panel to the frame
		jFrame.add(jPanel);
		// Opening the frame
		jFrame.setVisible(true);
	}

	/**
	 * This method would handle all button clicks on the Form
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		// Read which button is clicked
		String buttonClicked = e.getActionCommand();

		// Process Input button is clicked
		if (buttonClicked.equals(ImageUtilityConstants.BUTTON_RUN)) 
			processButtonClick();
		// Browse button is clicked
		else 
			browseButtonClick();
		
	}

	private void processButtonClick() {
		jLabel.setText(ImageUtilityConstants.IN_PROGRESS);
		ImageProcessOuput processOuput;
		try {
			if (inputFileSelected != null && inputFileSelected.endsWith(ImageUtilityConstants.EXTENSION_CSV)) {
				processOuput = imageProcessor.processInput(inputFileSelected);
				if (processOuput.getError() != null && !processOuput.getError().equals("")) 
					jLabel.setText(ImageUtilityConstants.ERROR + processOuput.getError() + " " + inputFileSelected);
				 else 
					// Successfully process input file
					jLabel.setText(ImageUtilityConstants.SUCCESSFULLY_PROCESDED + processOuput.getOutputFile());
				
			} else {
				// File name is not valid
				jLabel.setText(ImageUtilityConstants.ERROR_FILE_NAME_INVALID);
			}

		} catch (Exception e1) {
			jLabel.setText(ImageUtilityConstants.EXCEPTION_GENERAL);
			e1.printStackTrace();
		}
	}

	private void browseButtonClick() {
		// Create File Chooser
		JFileChooser jFileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

		// Add CSV Filter
		jFileChooser.addChoosableFileFilter(createCSVFileFilter());

		// Open file chooser
		int dialogBoxAction = jFileChooser.showOpenDialog(null);

		// if the user selects a file
		if (dialogBoxAction == JFileChooser.APPROVE_OPTION) {
			inputFileSelected = jFileChooser.getSelectedFile().getAbsolutePath();
			jLabel.setText(inputFileSelected);
		}
		// if user clicks cancel button on file chooser
		else {
			if (null == inputFileSelected || inputFileSelected.isEmpty()) {
				jLabel.setText(ImageUtilityConstants.ERROR_FILE_NOT_SELECTED);
			}
		}
	}

	/**
	 * Create CSV filter for FileChooser
	 * 
	 * @return FileFilter for CSV files
	 */
	private FileFilter createCSVFileFilter() {
		return new FileFilter() {
			@Override
			public String getDescription() {
				return ImageUtilityConstants.FILE_EXPLORER_FILE_TYPE;
			}

			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				} else {
					return f.getName().toLowerCase().endsWith(ImageUtilityConstants.EXTENSION_CSV);
				}
			}
		};
	}

}
