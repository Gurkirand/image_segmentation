import image.*;

public class ImageClassTests
{
	public static void main(String[] args)
	{
		ImageMatrix image = ImageProcessor.loadImage("data/CaravaggioSaintJohn.jpg");
		if (image != null)
			ImageProcessor.saveImage("data/CaravaggioSaintJohn_gray.jpg", image);
	}
}
