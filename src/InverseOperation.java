import java.awt.Color;

public class InverseOperation implements ImageOperation {

	public Color[][] doOperation(Color[][] imageArray) {
		int numOfRows = imageArray.length;
		int numOfColumns = imageArray[0].length;

		Color[][] result = new Color[numOfRows][numOfColumns];

		for (int i = 0; i < numOfRows; i++)
			for (int j = 0; j < numOfColumns; j++) {
				int red = 255 - imageArray[i][j].getRed();
				int green = 255 - imageArray[i][j].getGreen();
				int blue = 255 - imageArray[i][j].getBlue();
				result[i][j] = new Color(red, green, blue);
			}
		return result;
	}

}
