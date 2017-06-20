package main;

import image.*;
import ui.*;
import util.*;
import graph.*;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.awt.Point;

public class Main implements UIListener {
	public UI ui;
	public String fname;
	public ImageMatrix sourceMatrix;
	public ImageMatrix processedMatrix;
	public ImageMatrix croppedMatrix;
	public ImageMatrix segmentedMatrix;
	public ImageGraph sourceGraph;
	public GraphCut<Pixel> graphCut;
	public ArrayList<Pixel> segmentedTree;


	public static void main(String args[])
	{
		Main main = new Main();
		main.run();
	}

	public Main()
	{
		sourceGraph = new ImageGraph();
	}

	public void run()
	{
		UI ui = new UI(this);
		ui.run();
	}

	public void setImage(BufferedImage image, String name)
	{
		fname = name.substring(0, name.lastIndexOf('.'));
		sourceMatrix = ImageProcessor.imageToMatrix(image, ImageProcessor.RGB);
		processedMatrix = ImageProcessor.getGrayscaleCopy(sourceMatrix);
	}

	public void segment(Point source, Point[] sinks)
	{
		createImageGraph(source, sinks);
		runGraphCut(source, sinks);
		System.out.println("Maxflow: " + graphCut.getMaxFlow());
		segmentedMatrix = ImageProcessor.getSegmentedImage(processedMatrix, segmentedTree);
	}

	private void createImageGraph(Point source, Point[] sinks)
	{
		Point[] points = new Point[sinks.length + 1];
		points[0] = source;
		System.arraycopy(sinks, 0, points, 1, sinks.length);
		Pair<Point, Point> boundingBox = processedMatrix.getBoundingBox(points);
		croppedMatrix = ImageProcessor.getCrop(processedMatrix, boundingBox);
		sourceGraph.load(croppedMatrix, boundingBox.first.x, boundingBox.first.y);
	}

	private void runGraphCut(Point source, Point[] sinks)
	{
		if (graphCut != null)
		{
			graphCut.setGraph(sourceGraph);
		}
		else
		{
			graphCut = new GraphCut<>(sourceGraph, new ImageDirector());
		}
		Pixel sourceP = new Pixel(processedMatrix.matrix[source.x][source.y], source.x, source.y);
		graphCut.setSource(sourceP);
		Pixel sinkP;
		for (Point sink: sinks)
		{
			sinkP = new Pixel(processedMatrix.matrix[sink.x][sink.y], sink.x, sink.y);
			graphCut.addSink(sinkP);
		}
		graphCut.run();
		segmentedTree = graphCut.getSourceTree();
	}

	public void loadGraph()
	{
	}

	public void saveGraph()
	{
	}

	public void saveSegment()
	{
		ImageProcessor.saveImage("outputs/" + fname + "_segment.jpg", segmentedMatrix, ImageProcessor.GRAYSCALE);
	}

	public String displayGraph()
	{
		return sourceGraph.toString();
	}

}
