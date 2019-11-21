import java.awt.Color;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JDialog;

public class ImageProcessing {
	private String pathFile;  // Configuration file
	private String outputDir; // Name of output directory
	private String fileDelimiter; // File delimiter is \ for Windows and / for Unix and Mac


	/* Obtain the name of the configuration file */
	private String getPathFile() {
		JFileChooser jfc = new JFileChooser();
		JDialog dialog = new JDialog();
		jfc.setDialogTitle("Choose the configuration file: ");
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

		// Open a file browser to select the configuration file
		int returnValue = jfc.showOpenDialog(dialog);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			if (jfc.getSelectedFile().isFile()) {
				System.out.println("You selected the file: " + jfc.getSelectedFile());
				return jfc.getSelectedFile().toString();
			}
		}
		System.out.println("Rerun specifying pathFile.");
		return null;
	}

	/* Obtain the path to the output directory */
	private String getOutputDirectory() {
		JFileChooser jfc = new JFileChooser();
		JDialog dialog = new JDialog();
		jfc.setDialogTitle("Choose a directory to save your output images: ");
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		// Open a file browser to select the output directory
		int returnValue = jfc.showSaveDialog(dialog);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			if (jfc.getSelectedFile().isDirectory()) {
				System.out.println("You selected the directory: " + jfc.getSelectedFile());
				return jfc.getSelectedFile().toString();
			}
		}
		System.out.println("Rerun specifying pathFile.");
		return null;
	}

	/* Forms the name of the output file by concatenating the name of the image input file
	 * with "_output_" and the names of the operations applied to the image file separated
	 * by underscores. 
	 */
	private String createOutputImageName(String inputPath, String op) {
		// Partition the path to the input file into sub-strings separated by back slashes  
		String[] parts;
		if (fileDelimiter.equals("/")) {
			parts = inputPath.split(fileDelimiter);
		} else {
			parts = inputPath.split(fileDelimiter + fileDelimiter);
		}

		// Separate name of image file and file extension
		String[] inputName = parts[parts.length - 1].split("\\."); 
		
		// Concatenate file name, "_output_", applied operations, and file extensioni
		return inputName[0] + "_output_" + op + "." + inputName[1];
	}

	/* Check if user specified configuration file and output directory. If not, then
	 * invoke getPathFile( and getOutputDirectory() to get them.
	 */
	public boolean checkArguments(String[] args) {
		if (args.length != 2) { // Configuration file and output directory not specified
			System.out.println(
					"You can also use the program in the following format: java ImageProcessing pathFile outputFolder");
					
			try {
				EventQueue.invokeAndWait(new Runnable() {
					@Override
					public void run() {
					
						// Obtain configuration file 
						pathFile = getPathFile();
					}
				});
				if (pathFile == null) return false;
			
				EventQueue.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						// Obtain output directory
						outputDir = getOutputDirectory();
					}
				});
				if (outputDir == null) return false;
			}
			catch(Exception e) {
				return false;
			}
			
		} else {
			
			// verify that configuration file exists
			if (!new File(args[0]).isFile()) {
				System.out.println("First argument is not a file or does not exist.");
				return false;
			} else {
				pathFile = args[0];
			}

			// Verify that output directory exists
			if (!new File(args[1]).isDirectory()) {
				System.out.println("Second argument is not a directory.");
				return false;
			} else {
				outputDir = args[1];
			}
		}
		return true;
	}

	/* Process the input image file by applying to it all the operations specified in the
	 * configuration file.
	 */
	public void processImage() {
		BufferedReader br;
		String OS;        // Operating system of this machine
		OS = System.getProperty("os.name");
		if (OS.startsWith("Win")) fileDelimiter = "\\"; else fileDelimiter = "/";
		try {
			// Open the configuration file
			br = new BufferedReader(new FileReader(pathFile));
			String line = null;
			try {
				String outputFile = ""; // Name of output image file
				int filesProcessed = 0;
				// Read the configuration file
				while ((line = br.readLine()) != null) {
					
					// Split the line read from the configuration file into its components: color mode,
					// name of input image file, and image operations to apply to the image
					String[] parameters = line.split("\\s+");

					Color[][] array;

					// Read the image file
					ImageLoader loader = new ImageLoader();
					if (loader.loadImage(parameters[1])) {

						// Store pixels of image file into "array" according to color mode
						if ("Color".equals(parameters[0])) {
							array = loader.getColorArray();
						} else if ("Grayscale".equals(parameters[0])) {
							array = loader.getGrayscaleArray();
						} else {
							System.out.println("Error: Please provide Color or Grayscale in configuration file: " + parameters[1]);
							br.close();
							return;
						}

						// This string will contain all image operations performed. This will be used as part
						// of the name of the output file
						String nameSuffix = ""; 
						Color[][] result = null;  // After applying operation the image will be stored here
						int numOfOperations = parameters.length-2;
						
						// Apply image operations
						for (int i = 0; i < numOfOperations; i++) {
							ImageOperation operation = OperationFactory.create(parameters[2 + i]);
							result = operation.doOperation(array);
							array = result;  // Get ready to apply next operation
							
							// Append name of applied operation to nameSuffix
							if (nameSuffix.length() > 0) nameSuffix = nameSuffix + "_";
							nameSuffix = nameSuffix + parameters[2+i];
						}
						
						// Create name for output file
						outputFile = createOutputImageName(parameters[1], nameSuffix);
						
						// Save modified image into a file in the output directory
						ImageWriter writer = new ImageWriter();
						writer.writeImage(outputDir + fileDelimiter + outputFile, result);
						++ filesProcessed;
					}
				}
				br.close();
				if (filesProcessed == 1) 
					System.out.println("Image file \""+outputDir+fileDelimiter+outputFile+ "\" saved");
				else System.out.println(filesProcessed+" image files processed");
				System.exit(0);
			} catch (IOException ioException) {
				System.out.println("Error when reading the text file.");
				ioException.printStackTrace();
			}
		} catch (FileNotFoundException fileException) {
			System.out.println("Text file containing images' paths not found.");
			fileException.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ImageProcessing process = new ImageProcessing();
		if (process.checkArguments(args))
			process.processImage();
	}

}