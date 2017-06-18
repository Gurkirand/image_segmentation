package graph;

import java.awt.Point;
import image.Pixel;

public class ImageDirector extends Director<Pixel>
{
	private double distance(int x1, int y1, int x2, int y2)
	{
		return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
	}

	private double distance(Point p1, Point p2)
	{
		return distance(p1.x, p1.y, p2.x, p2.y);
	}

	public int direct(Pixel v1, Pixel v2)
	{
		double d1 = distance(source.coordinate, v1.coordinate);
		double d2 = distance(source.coordinate, v2.coordinate);

		return (d1 < d2 ? 1 : -1);
	}
}
