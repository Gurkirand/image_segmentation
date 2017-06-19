package tests;

import tests.base.*;
import image.*;
import util.*;
import java.awt.image.BufferedImage;
import java.util.function.Function;
import java.util.ArrayList;

public class ImageClassTests
{
	public static void main(String[] args)
	{
		Function<String, BufferedImage> loadF = (a) -> {return ImageProcessor.load(a);};
		Function<BufferedImage, Integer> averageF = (a) -> {return ImageProcessor.getAverageGrayscaleColor(a);};
		Function<BufferedImage, ImageMatrix> matrixF = (a) -> {return ImageProcessor.imageToGrayscaleMatrix(a);};
		Function<Pair<String, ImageMatrix>, Boolean> saveF = (a) -> {return ImageProcessor.saveImage(a.first, a.second);};
		Function<Pair<ImageMatrix, Integer>, ImageMatrix> blurF = (a) -> {return ImageProcessor.applyGaussianBlur(a.first, a.second);};

		Function<Pair<ImageMatrix, Pixel[]>, ImageMatrix> segmentF = (a) -> {return ImageProcessor.getSegmentedImage(a.first, a.second);};

		BufferedImage image = Timer.time("Load Image", loadF, "data/CaravaggioSaintJohn.jpg");
		Timer.time("Average Color", averageF, image);
		ImageMatrix imageMatrix = Timer.time("Grayscale Image Matrix", matrixF, image);
		Timer.time("Save Grayscale Image Matrix", saveF, new Pair<>("data/tests/CaravaggioSaintJohn_gray.jpg", imageMatrix), true);
		ImageMatrix blurMatrix = Timer.time("Gaussian Blur", blurF, new Pair<>(imageMatrix, 5));
		Timer.time("Save Blurred Image Matrix", saveF, new Pair<>("data/tests/CaravaggioSaintJohn_blur.jpg", blurMatrix), true);

		ArrayList<Pixel> _section = new ArrayList<>();
		for (int i = 0; i < imageMatrix.matrix.length / 2; i++)
		{
			for (int j = 0; j < imageMatrix.matrix[0].length / 2; j++)
			{
				_section.add(new Pixel(imageMatrix.matrix[i][j], i, j));
			}
		}
		Pixel[] section = new Pixel[_section.size()];
		 _section.toArray(section);

		ImageMatrix segment = Timer.time("Segment Image", segmentF, new Pair<>(imageMatrix, section), null);

		Timer.time("Save Segmented Image Matrix", saveF, new Pair<>("data/tests/CaravaggioSaintJohn_segment.jpg", segment), true);
		
	}
}
