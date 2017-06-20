package image;

import util.Pair;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;

public class ImageProcessor
{   
	private enum Encoding
	{
		RGB, GRAYSCALE
	}
	public static final Encoding GRAYSCALE = Encoding.GRAYSCALE;
	public static final Encoding RGB = Encoding.RGB;
	private static final double RED_WEIGHT = 0.21;
	private static final double GREEN_WEIGHT = 0.72;
	private static final double BLUE_WEIGHT = 0.07;

	public static BufferedImage load(String filename)
	{
		BufferedImage image = null;
		try
		{
			File imageFile = new File(filename);
			image = ImageIO.read(imageFile);
		} 
		catch (IOException e)
		{
			System.out.println("Failed to open image file");
			return null;
		}
		
		return image;
	}

	public static boolean saveImage(String filename, ImageMatrix data, Encoding encoding)
	{
		boolean saved = false;

		int w = data.matrix.length,
		    h = data.matrix[0].length,
		    value;
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

		for (int x = 0; x < w; x++)
		{
			for (int y = 0; y < h; y++)
			{
				value = data.matrix[x][y];
				if (encoding == GRAYSCALE)
				{
					value = (value << 16) + (value << 8) + value;
				}
				image.setRGB(x, y, value);
			}
		}
		 
		try
		{
			File ouptut = new File(filename);
			ImageIO.write(image, "jpg", ouptut);
			saved = true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return saved;
	}

	public static ImageMatrix imageToMatrix(BufferedImage image, Encoding encoding)
	{
		int w = image.getWidth(),
		    h = image.getHeight();
		int[][] matrix = new int[w][h];

		byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

		boolean hasAlphaChannel = image.getAlphaRaster() != null;

		int pixelLength = hasAlphaChannel ? 4: 3,
			initPos = hasAlphaChannel ? 1: 0,
		    pos, r, g, b, gray, value;
		
		for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength)
		{
			pos = initPos;
			b = ((int) pixels[pixel + pos++] & 0xFF);
			g = ((int) pixels[pixel + pos++] & 0xFF);
			r = ((int) pixels[pixel + pos] & 0xFF);

            if (encoding == GRAYSCALE)
			{
				value = (int) (RED_WEIGHT*r + GREEN_WEIGHT*g + BLUE_WEIGHT*b);
			}
            else
			{
				value = (r << 16) + (g << 8) + b;
			}

            matrix[col][row] = value;

            col++;
            if (col == w)
			{
				col = 0;
				row++;
			}
		}
		
		return new ImageMatrix(matrix);
	}

	public static ImageMatrix getGrayscaleCopy(ImageMatrix image)
	{
		int w = image.getWidth(),
		    h = image.getHeight(),
		    value;
		int[][] grayCopy = new int[w][h];

		for (int i = 0; i < w; i++)
		{
			for (int j = 0; j < h; j++)
			{
				value = image.matrix[i][j];
				grayCopy[i][j] = (int) (RED_WEIGHT * ((value >> 16) & 0xFF) + GREEN_WEIGHT * ((value >> 8) & 0xFF) + BLUE_WEIGHT * (value & 0xFF));
			}
		}

		return new ImageMatrix(grayCopy);
	}

	public static int[] getAverageColor(BufferedImage image)
	{
		BufferedImage scaledBitmap = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		scaledBitmap.getGraphics().drawImage(
				image.getScaledInstance(1, 1, BufferedImage.SCALE_AREA_AVERAGING),
				0, 0, null);
		int rgb = scaledBitmap.getRGB(0, 0);
		return new int[]{(rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF};
	}

	public static int getAverageGrayscaleColor(BufferedImage image)
	{
		int[] rgb = getAverageColor(image);
		return (int) (RED_WEIGHT*rgb[0] + GREEN_WEIGHT*rgb[1] + BLUE_WEIGHT*rgb[2]);
	}

	public static ImageMatrix getCrop(ImageMatrix img, Pair<Point, Point> boundingBox)
	{
		Point min = boundingBox.first,
		      max = boundingBox.second;

		int[][] crop = new int[max.x - min.x + 1][max.y - min.y + 1];

		for (int i = min.x, ci = 0; i <= max.x; i++, ci++)
		{
			for (int j = min.y, cj = 0; j <= max.y; j++, cj++)
			{
				crop[ci][cj] = img.matrix[i][j];
			}
		}

		return new ImageMatrix(crop);
	}

	public static ImageMatrix getSegmentedImage(ImageMatrix image, ArrayList<Pixel> section)
	{
		Pixel[] section_copy = new Pixel[section.size()];
		section.toArray(section_copy);
		return getSegmentedImage(image, section_copy);
	}

	public static ImageMatrix getSegmentedImage(ImageMatrix image, Pixel[] section)
	{
		int average = 0,
		    background,
		    width = image.matrix.length,
		    height = image.matrix[0].length,
		    i, j; 
		Pixel p;
		ImageMatrix segment = new ImageMatrix(new int[width][height]);
		for (i = 0; i < section.length; i++)
		{
			average += section[i].value;
		}

		average = average / section.length;
		background = average > 255 * 0.6 ? 0: 255;

		for (i = 0; i < width; i++)
		{
			for (j = 0; j < height; j++)
			{
				segment.matrix[i][j] = background;
			}
		}

		for (i = 0; i < section.length; i++)
		{
			p = section[i];
			segment.matrix[p.coordinate.x][p.coordinate.y] = p.value;
		}
		return segment;
	}

	/*
	   Using an approximation of gaussian using 3 passes of box blur.
	   Altered to work with the 2d array inside ImageMatrix objects.
	   Uses two ImageMatrices, since the algorithm requires "memory" betwene passes.
	   source - http://blog.ivank.net/fastest-gaussian-blur.html
	*/
	public static ImageMatrix applyGaussianBlur(ImageMatrix image, int r)
	{
		ImageMatrix target = new ImageMatrix(image.matrix);
		int w = image.matrix.length,
		    h = image.matrix[0].length;
		boxBlurApproximation(target, w, h, r);
		return target;
	}

	private static void boxBlurApproximation(ImageMatrix target, int w, int h, int r)
	{
		ImageMatrix memory = new ImageMatrix(target.matrix);
		int[] filter = boxFilter(r, 3);

		boxFilterPass(memory, target, w, h, (filter[0] - 1) / 2);

		copyMatrix(target, memory, w, h);
		boxFilterPass(memory, target, w, h, (filter[1] - 1) / 2);

		copyMatrix(memory, target, w, h);
		boxFilterPass(memory, target, w, h, (filter[2] - 1) / 2);
	}

	private static int[] boxFilter(int sigma, int n)
	{
		int wIdeal = (int) Math.floor(Math.sqrt((12*sigma*sigma / 2) + 1));
		int wl = (wIdeal % 2 == 0) ? wIdeal - 1: wIdeal;
		int wu = wl + 2;

		int m = Math.round((12*sigma*sigma - n*wl*wl - 4*n*wl - 3*n) / (-4*wl - 4));

		int[] filter = new int[n];
		for (int i = 0; i < n; i++)
		{
			filter[i] = (i < m) ? wl : wu;
		}

		return filter;
	}

	private static void boxFilterPass(ImageMatrix source, ImageMatrix target, int w, int  h, int r)
	{
		boxFilterPass_X(target, source, w, h, r);
		boxFilterPass_Y(source, target, w, h, r);
	}

	private static void boxFilterPass_X(ImageMatrix source, ImageMatrix target, int w, int  h, int r)
	{
		double div = r + r + 1,
			   first, last, val;
		int i= 0, j= 0, ti= 0, ri= 0, li=0;
		for (i = 0; i < h; i++)
		{
			ti = 0;
			li = ti;
			ri = ti + r;
			first = source.matrix[0][i];
			last = source.matrix[w - 1][i];
			val = (r + 1) * first;
			for (j = 0; j < r; j++)
			{
				val += source.matrix[j][i];
			}
			for (j = 0; j <=r; j++)
			{
				val += source.matrix[ri++][i] - first;
				target.matrix[ti++][i] = (int) Math.round(val / div);
			}
			for (j = r+1; j < w-r; j++)
			{
				val += source.matrix[ri++][i] - source.matrix[li++][i];
				target.matrix[ti++][i] = (int) Math.round(val / div);
			}
			for (j = w-r; j < w; j++)
			{
				val += last - source.matrix[li++][i];
				target.matrix[ti++][i] = (int) Math.round(val / div);
			}
		}
	}

	private static void boxFilterPass_Y(ImageMatrix source, ImageMatrix target, int w, int  h, int r)
	{
		double div = r + r + 1,
		       first, last, val;
		int i, j, ti, ri, li;
		for (i = 0; i < w; i++)
		{
			ti = 0;
			li = ti;
			ri = ti + r;
			first = source.matrix[i][0];
			last = source.matrix[i][h - 1];
			val = (r + 1) * first;
			for (j = 0; j < r; j++)
			{
				val += source.matrix[i][j];
			}
			for (j = 0; j <=r; j++)
			{
				val += source.matrix[i][ri++] - first;
				target.matrix[i][ti++] = (int) Math.round(val / div);
			}
			for (j = r+1; j < h-r; j++)
			{
				val += source.matrix[i][ri++] - source.matrix[i][li++];
				target.matrix[i][ti++] = (int) Math.round(val / div);
			}
			for (j = h-r; j < h; j++)
			{
				val += last - source.matrix[i][li++];
				target.matrix[i][ti++] = (int) Math.round(val / div);
			}
		}

	}

	private static void copyMatrix(ImageMatrix source, ImageMatrix target, int w, int h)
	{
		for (int i = 0; i < w; i++)
			for (int j = 0; j < h; j++)
				target.matrix[i][j] = source.matrix[i][j];
	}
}
