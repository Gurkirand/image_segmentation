package image;
import java.awt.Point;

public class Pixel
{
	public int value;
	public Point coordinate;
	public Pixel(int v, Point c) 
	{
		value = v;
		coordinate = c;
	}

	public Pixel(int v, int x, int y)
	{
		this(v, new Point(x, y));
	}

	public Pixel() {this(0, 0, 0);}

	public String toString()
	{
		//return String.format("value: %d, coord: (%d, %d)", value, coordinate.x, coordinate.y);
		return String.format("%d %d %d", value, coordinate.x, coordinate.y);
	}

	public boolean equals(Object o)
	{
		if (!(o instanceof Pixel))
		{
			return false;
		}

		Pixel p = (Pixel) o;

		if (value != p.value)
		{
			return false;
		}

		return coordinate.equals(p.coordinate);
	}

	public int hashCode()
	{
		return (37 * coordinate.hashCode() + value);
	}

	public Pixel clone()
	{
		return new Pixel(value, coordinate.x, coordinate.y);
	}
}
