public class OperationFactory {
	/* Create an object able to perform specified image operation */
	public static ImageOperation create(String op) {
		switch (op) {
		case "Contour":
		//	return new ContourOperation();
		case "Inverse":
			return new InverseOperation();
		case "Thresholding":
		//	return new ThresholdingOperation();
		case "Adjustment":
			//return new AdjustmentOperation();
		case "Magnify":
		//	return new MagnifyOperation();
		default:
			return new IdentityOperation();
		}
	}
}
