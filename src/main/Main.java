package main;

import image.*;
import ui.*;
import util.*;
import graph.*;
import java.io.*;
import java.util.ArrayList;

import javax.swing.JFileChooser;

import java.awt.image.BufferedImage;
import java.awt.Point;

public class Main implements UIListener {
	public UI ui;
	public String fname;
	public BufferedImage segmentedImage;
	public ImageMatrix sourceMatrix;
	public ImageMatrix grayscaleMatrix;
	public ImageMatrix processedMatrix;
	public ImageMatrix croppedMatrix;
	public ImageMatrix segmentedMatrix;
	public ImageGraph sourceGraph;
	public GraphCut<Pixel> graphCut;
	public ArrayList<Pixel> segmentedTree;
	boolean loaded = false;
	boolean imageChange = false;


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
		ui = new UI(this);
		ui.run();
	}

	public void setImage(BufferedImage image, String name)
	{
		if (loaded)
			imageChange = true;
		fname = name.substring(0, name.lastIndexOf('.'));
		sourceMatrix = ImageProcessor.imageToMatrix(image, ImageProcessor.RGB);
		grayscaleMatrix = ImageProcessor.getGrayscaleCopy(sourceMatrix);
		processedMatrix = grayscaleMatrix;
		loaded = false;
	}

	public void segment(Point source, Point[] sinks)
	{
		createImageGraph(source, sinks);
		runGraphCut(source, sinks);
		System.out.println("Maxflow: " + graphCut.getMaxFlow());
		segmentedMatrix = ImageProcessor.getSegmentedImage(grayscaleMatrix, segmentedTree);
		segmentedImage = ImageProcessor.matrixToImage(segmentedMatrix, ImageProcessor.GRAYSCALE);
		ui.displaySegmented(segmentedImage);
	}

	private void createImageGraph(Point source, Point[] sinks)
	{
		Point[] points = new Point[sinks.length + 1];
		points[0] = source;
		System.arraycopy(sinks, 0, points, 1, sinks.length);
		Pair<Point, Point> boundingBox = processedMatrix.getBoundingBox(points);
		croppedMatrix = ImageProcessor.getCrop(processedMatrix, boundingBox);
		sourceGraph.clear();
		sourceGraph.load(croppedMatrix, boundingBox.first);
		loaded = true;
	}

	private void createImageGraph()
	{
		sourceGraph.load(grayscaleMatrix);
		loaded = true;
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

	public void loadGraph(File f)
	{
		sourceGraph.clear();
		sourceMatrix = sourceGraph.load(f);
		// grayscaleMatrix = ImageProcessor.getGrayscaleCopy(sourceMatrix);
		grayscaleMatrix = sourceMatrix;
		processedMatrix = grayscaleMatrix;
		ui.setImage(ImageProcessor.matrixToImage(sourceMatrix, ImageProcessor.GRAYSCALE));
	}

	public void saveGraph(File file)
	{
		if (loaded == false)
			createImageGraph();
		PrintWriter pw;
		try {

			pw = new PrintWriter(file);
			sourceGraph.save(pw);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	public void saveSegment()
	{
		JFileChooser fc = new JFileChooser();
		File workingDirectory = new File(System.getProperty("user.dir"));
		fc.setCurrentDirectory(workingDirectory);
		int result = fc.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			ImageProcessor.saveImage(file, segmentedImage);
		}
	}

	public String displayGraph()
	{
		return sourceGraph.toString();
	}

	public String displayGraphBFT()
	{
		return sourceGraph.BFT();
	}

	public String displayGraphDFT()
	{
		return sourceGraph.DFT();
	}

}
