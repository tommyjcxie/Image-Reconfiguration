import java.awt.Color;
import java.awt.image.BufferedImage;

public class ImageConverter {

	/* Save color pixels of image into a 2 dimensional array of Color objects */
	public Color[][] bufferedImageToColorArray(BufferedImage image) {
		int height = image.getHeight();
		int width = image.getWidth();

		Color[][] array = new Color[width][height];

		// Store pixels into the 2 dimensional array
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++) {
				Color pixel = new Color(image.getRGB(i, j));
				array[i][j] = pixel;

			}
		return array;
	}

	/* Save grey pixels of image into a 2 dimensional array of Color objects */
	public Color[][] bufferedImageToGrayscaleArray(BufferedImage image) {
		int height = image.getHeight();
		int width = image.getWidth();

		Color[][] array = new Color[width][height];

		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++) {
				Color pixel = new Color(image.getRGB(i, j));
				
				// Convert color pixel into a  shade of grey
				int color = (int) Math
						.round((0.21 * pixel.getRed()) + (0.71 * pixel.getGreen()) + (0.07 * pixel.getBlue()));
				array[i][j] = new Color(color, color, color);

			}
		return array;
	}

	/* Convert 2-dimensional array of Color objects into set of image pixels */
	public BufferedImage arrayToBufferedImage(Color[][] array) {
		int numOfRows = array.length;
		int numOfColumns = array[0].length;

		// Create image container
		BufferedImage image = new BufferedImage(numOfRows, numOfColumns, BufferedImage.TYPE_INT_RGB);

		for (int i = 0; i < numOfRows; i++)
			for (int j = 0; j < numOfColumns; j++) {
				int color = getIntFromColor(array[i][j]); // Convert Color to pixel
				image.setRGB(i, j, color);  // store in image container
			}

		return image;
	}

	/* Convert a Color object containing R,G,V values into an integer pixel for storage in an image */
	private int getIntFromColor(Color color) {
		int R = Math.round(color.getRed());
		int G = Math.round(color.getGreen());
		int B = Math.round(color.getBlue());

		// Store R G V values into a single integer of 32 bits
		R = (R << 16) & 0x00FF0000;
		G = (G << 8) & 0x0000FF00;
		B = B & 0x000000FF;

		return 0xFF000000 | R | G | B;
	}
}
