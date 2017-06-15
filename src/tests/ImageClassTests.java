import image.*;
import java.awt.image.BufferedImage;
//import java.util.Function;

public class ImageClassTests
{
	public static void main(String[] args)
	{
		BufferedImage image = ImageProcessor.loadImage("data/CaravaggioSaintJohn.jpg");
		if (image == null)
		{
			System.out.println("Failed to open image.");
			return;
		}

		ImageMatrix imageMatrix = ImageProcessor.imageToGrayscaleMatrix(image);
		if (imageMatrix == null)
		{
			System.out.println("Failed to convert image to matrix.");
			return;
		}
		ImageProcessor.saveImage("data/tests/CaravaggioSaintJohn_gray.jpg", imageMatrix);

		int gray = ImageProcessor.getAverageGrayscaleColor(image);
		System.out.println("AverageColor: " + gray);
		
		ImageMatrix blurredImage = ImageProcessor.applyGaussianBlur(imageMatrix, 5);
		if (imageMatrix == null)
		{
			System.out.println("Failed to apply gaussian blur.");
			return;
		}
		ImageProcessor.saveImage("data/tests/CaravaggioSaintJohn_blur.jpg", blurredImage);
	}
}
