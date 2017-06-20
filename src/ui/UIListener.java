package ui;
import java.awt.Point;
import java.awt.image.BufferedImage;

public interface UIListener
{
	public void setImage(BufferedImage iamge, String name);
	public void segment(Point source, Point[] sinks);
	public void loadGraph();
	public void saveGraph();
	public void saveSegment();
	public String displayGraph();
}
