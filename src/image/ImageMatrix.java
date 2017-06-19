package image;
import util.Pair;
import java.awt.Point;

public class ImageMatrix
{
	public int[][] matrix;

	public ImageMatrix() {}

	public ImageMatrix(int[][] m)
	{
		matrix = m; 
	}

	public Pair<Point, Point> getBoundingBox(Point[] points)
	{
		Point max = new Point(0, 0),
		      min = new Point(matrix.length, matrix[0].length),
		      p;

		for (int i = 0; i < points.length; i++)
		{
			p = points[i];
			if (p.x > max.x)
			{
				max.x = p.x;
			}
			if (p.y > max.y)
			{
				max.y = p.y;
			}
			if (p.x < min.x)
			{
				min.x = p.x;
			}
			if (p.y < min.y)
			{
				min.y = p.y;
			}
		}

		return new Pair<>(min, max);
	}
}
