package image;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageProcessor
{
	public static ImageMatrix loadImage(String filename)
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
		
		return new ImageMatrix(imageToGrayscaleMatrix(image));
	}

	public static boolean saveImage(String filename, ImageMatrix data)
	{
		boolean saved = false;

		int w = data.matrix.length,
		    h = data.matrix[0].length,
		    grey;
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

		for (int x = 0; x < w; x++)
		{
			for (int y = 0; y < h; y++)
			{
				grey = data.matrix[x][y];
				image.setRGB(x, y, (grey << 16) + (grey << 8) + grey);
			}
		}
		 
		try
		{
			File ouptut = new File(filename);
			ImageIO.write(image, "jpg", ouptut);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			saved = false;
		}

		return saved;
	}

	//https://stackoverflow.com/questions/6524196/java-get-pixel-array-from-image
	public static int[][] imageToGrayscaleMatrix(BufferedImage image)
	{
		int w = image.getWidth(),
		    h = image.getHeight();
		int[][] matrix = new int[w][h];

		byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

		boolean hasAlphaChannel = image.getAlphaRaster() != null;

		int pixelLength = hasAlphaChannel ? 4: 3,
			initPos = hasAlphaChannel ? 1: 0,
		    pos, r, g, b, grey, value;
		double rW = 0.3, gW = 0.59, bW = .11;
		
		for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength)
		{
			pos = initPos;
			b = ((int) pixels[pixel + pos++] & 0xFF);
			g = ((int) pixels[pixel + pos++] & 0xFF);
			r = ((int) pixels[pixel + pos] & 0xFF);

            grey = (int) (rW*r + gW*g + bW*b);
            value = (grey << 16) + (grey << 8) + grey;

            matrix[col][row] = grey;

            col++;
            if (col == w)
			{
				col = 0;
				row++;
			}
		}
		
		return matrix;
	}

	//http://blog.ivank.net/fastest-gaussian-blur.html
	public static ImageMatrix applyGaussianBlur(ImageMatrix image, int r)
	{
		ImageMatrix target = new ImageMatrix(image.matrix);
		int w = image.matrix.length,
		    h = image.matrix[0].length;
		int[] boxFilter = boxFilter(r, 3);
		return target;
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

	private static void filterPass(ImageMatrix source, int w, int  h, int r)
	{

	}

	private static void filterPassH(ImageMatrix source, int w, int  h, int r)
	{

	}

	private static void filterPassT(ImageMatrix source, int w, int  h, int r)
	{

	}
}
