package image;
import java.awt.Point;

public class Pixel
{
	public int value;
	public Point coordinate;
	public Pixel(int value, Point coordinate) {}
	public Pixel(int value, int x, int y) {}
	public Pixel() {this(0, 0, 0);}
}
