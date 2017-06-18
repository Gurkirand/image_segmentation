package tests;

import image.*;
import util.*;
import tests.base.*;
import java.awt.image.BufferedImage;
import java.util.function.Function;

public class ImageClassTests
{
	public static void main(String[] args)
	{
		Function<String, BufferedImage> loadF = (a) -> {return ImageProcessor.loadImage(a);};
		Function<BufferedImage, Integer> averageF = (a) -> {return ImageProcessor.getAverageGrayscaleColor(a);};
		Function<BufferedImage, ImageMatrix> matrixF = (a) -> {return ImageProcessor.imageToGrayscaleMatrix(a);};
		Function<Pair<String, ImageMatrix>, Boolean> saveF = (a) -> {return ImageProcessor.saveImage(a.first, a.second);};
		Function<Pair<ImageMatrix, Integer>, ImageMatrix> blurF = (a) -> {return ImageProcessor.applyGaussianBlur(a.first, a.second);};

		BufferedImage image = Timer.time("Load Image", loadF, "data/CaravaggioSaintJohn.jpg");
		Timer.time("Average Color", averageF, image);
		ImageMatrix imageMatrix = Timer.time("Grayscale Image Matrix", matrixF, image);
		Timer.time("Save Grayscale Image Matrix", saveF, new Pair<>("data/tests/CaravaggioSaintJohn_gray.jpg", imageMatrix), true);
		ImageMatrix blurMatrix = Timer.time("Gaussian Blur", blurF, new Pair<>(imageMatrix, 5));
		Timer.time("Save Blurred Image Matrix", saveF, new Pair<>("data/tests/CaravaggioSaintJohn_blur.jpg", blurMatrix), true);
	}

	// Without Timer / Functional programming
	// public static void main(String[] args)
	// {
	// 	BufferedImage image = ImageProcessor.loadImage("data/CaravaggioSaintJohn.jpg");
	// 	if (image == null)
	// 	{
	// 		System.out.println("Failed to open image.");
	// 		return;
	// 	}

	// 	ImageMatrix imageMatrix = ImageProcessor.imageToGrayscaleMatrix(image);
	// 	if (imageMatrix == null)
	// 	{
	// 		System.out.println("Failed to convert image to matrix.");
	// 		return;
	// 	}
	// 	ImageProcessor.saveImage("data/tests/CaravaggioSaintJohn_gray.jpg", imageMatrix);

	// 	int gray = ImageProcessor.getAverageGrayscaleColor(image);
	// 	System.out.println("AverageColor: " + gray);
		
	// 	ImageMatrix blurredImage = ImageProcessor.applyGaussianBlur(imageMatrix, 5);
	// 	if (imageMatrix == null)
	// 	{
	// 		System.out.println("Failed to apply gaussian blur.");
	// 		return;
	// 	}
	// 	ImageProcessor.saveImage("data/tests/CaravaggioSaintJohn_blur.jpg", blurredImage);
	// }
}
