package ui;
//package ui;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.*;

public interface UIListener
{
	public void setImage(BufferedImage iamge, String name);
	public void segment(Point source, Point[] sinks);
	public void loadGraph(File f);
	public void saveGraph(File f);
	public void saveSegment();
	public String displayGraph();
	public String displayGraphBFT();
	public String displayGraphDFT();
}
