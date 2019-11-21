import java.awt.Color;

/* Interface used so all image operations are invoked in an uniform manner */
public interface ImageOperation {
	public Color[][] doOperation(Color[][] imageArray);
}
