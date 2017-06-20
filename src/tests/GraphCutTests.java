package tests;

import tests.base.*;
import image.*;
import graph.*;
import tests.*;
import util.Pair;
import java.util.ArrayList;
import java.util.function.Function;


import java.awt.image.BufferedImage;
import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;


public class GraphCutTests
{
	public static void main(String[] args)
	{
		Function<Boolean, Boolean> graphCutF = (a) -> {return testGraphCut();};
		Timer.time("Testing Graph Cut", graphCutF, true, true);
	}

	public static boolean testGraphCut()
	{
		Graph<Pixel> g = new Graph<>();
		ImageGraph ig = new ImageGraph();
		int graph_dim = 11;
		int source = 5;
		Pixel[][] img = new Pixel[graph_dim][graph_dim];
		int i, j;
		int x = 250;
		int v;
		for (i = 0; i < graph_dim; i++)
		{
			for (j = 0; j < graph_dim; j++)
			{
				v = (int) (x - 50 * (Math.sqrt(Math.pow(i -source, 2) + Math.pow(j - source, 2))));
				if (v <= 0)
					v = 10;
				img[i][j] = new Pixel(v, i, j);
			}
		}

		Pixel p;
		Pixel q;
		double w;
		for (i = 0; i < graph_dim; i++)
		{
			for (j = 0; j < graph_dim; j++)
			{
				p = img[i][j];
				if (j > 0)
				{
					q = img[i][j-1];
					w = 100 * (q.value > p.value ? p.value / (1.0 * q.value): (1.0 * q.value) / p.value);
					g.addEdge(p, q, w);
				}
				if (i > 0)
				{
					q = img[i-1][j];
					w = 100 * (q.value > p.value ? p.value / (1.0 * q.value): (1.0 * q.value) / p.value);
					g.addEdge(p, q, w);
				}
				if (j < 9)
				{
					q = img[i][j+1];
					w = 100 * (q.value > p.value ? p.value / (1.0 * q.value): (1.0 * q.value) / p.value);
					g.addEdge(p, q, w);
				}
				if (i < 9)
				{
					q = img[i+1][j];
					w = 100 * (q.value > p.value ? p.value / (1.0 * q.value): (1.0 * q.value) / p.value);
					g.addEdge(p, q, w);
				}
			}
		}
	
		for (i = 0; i < graph_dim; i ++)
		{
			System.out.print("[");
			System.out.print(String.format("%5d", img[i][0].value));
			for (j = 1; j < graph_dim; j++)
			{
				System.out.print(", " + String.format("%5d", img[i][j].value));
			}
			System.out.print("]\n");
		}

		GraphCut<Pixel> gc = new GraphCut<>(g, new ImageDirector());
		//added test to see if saving and reading work
		PrintWriter pw;
		try {
			pw = new PrintWriter(new FileOutputStream(
				    new File("data/tests/graph_save.txt")));
			gc.save(pw);
		} catch (FileNotFoundException e) {
			System.out.println("file not found");
			e.printStackTrace();
		} 
		ig.load(new File("data/tests/graph_save.txt"));
		PrintWriter pw1;
		try {
			pw1 = new PrintWriter(new FileOutputStream(
					    new File("data/tests/graph_loaded_save.txt")));
			ig.save(pw1);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		gc = new GraphCut<Pixel>(ig, new ImageDirector());//reconstructing graph from saved file
		
		gc.setSource(img[source][source]);
		gc.addSink(img[graph_dim-1][graph_dim - 1]);
		gc.addSink(img[0][0]);
		gc.run();
		ArrayList<Pixel> s = gc.getSourceTree();
		ArrayList<Pixel> t = gc.getSinkTree();
		System.out.println("Source tree size: " + s.size());
		System.out.println("Sink tree size: " + t.size());
		System.out.println("Maxflow: " + gc.getMaxFlow());

		for (Pixel pix: s)
			img[pix.coordinate.x][pix.coordinate.y].value = 1000;

		for (Pixel pix: t)
			img[pix.coordinate.x][pix.coordinate.y].value = 0;
		for (i = 0; i < graph_dim; i ++)
		{
			System.out.print("[");
			System.out.print(String.format("%5d", img[i][0].value));
			for (j = 1; j < graph_dim; j++)
			{
				System.out.print(", " + String.format("%5d", img[i][j].value));
			}
			System.out.print("]\n");
		}
		return true;
	}

	public static boolean testGraphCutWithImage()
	{
		String name = "jumpman";

		// String name = "CaravaggioSaintJohn";

		BufferedImage image = ImageProcessor.load("data/" + name + ".jpg");
		ImageMatrix imgM = ImageProcessor.imageToMatrix(image, ImageProcessor.GRAYSCALE);

		//NEWWINDS
		Pixel source = new Pixel(imgM.matrix[41][45], 41, 45);
		Pixel[] sinks = new Pixel[]{
			new Pixel(imgM.matrix[2][92], 2, 92),
			new Pixel(imgM.matrix[42][5], 42, 5),
			new Pixel(imgM.matrix[90][80], 90, 80)
		};

		// Caravaggio
		// Pixel source = new Pixel(imgM.matrix[1162][906], 1162, 906);
		// Pixel[] sinks = new Pixel[]{
		// 	new Pixel(imgM.matrix[815][1080], 815, 1080),
		// 	new Pixel(imgM.matrix[1557][360], 1557, 360),
		// };

		Point[] points = new Point[sinks.length + 1];
		points[0] = source.coordinate;
		for (int i = 1; i < points.length; i++)
		{
			points[i] = sinks[i - 1].coordinate;
		}
		
		Pair<Point, Point> b = imgM.getBoundingBox(points);
		ImageMatrix imgM_crop = ImageProcessor.getCrop(imgM, b);
		ImageGraph imgG = new ImageGraph();
		imgG.load(imgM_crop, b.first.x, b.first.y);


		GraphCut<Pixel> gc = new GraphCut<>(imgG, new ImageDirector());
		gc.setSource(source);
		for (Pixel s: sinks)
		{
			gc.addSink(s);
		}
		gc.run();
		ArrayList<Pixel> s = gc.getSourceTree();
		ArrayList<Pixel> t = gc.getSinkTree();
		System.out.println("Source tree size: " + s.size());
		System.out.println("Sink tree size: " + t.size());
		System.out.println("Maxflow: " + gc.getMaxFlow());

		Pixel[] s_copy = new Pixel[s.size()];
		s.toArray(s_copy);
		ImageMatrix imgM_segmented = ImageProcessor.getSegmentedImage(imgM, s_copy);
	
		ImageProcessor.saveImage("data/tests/" + name + "_segment.jpg", imgM_segmented, ImageProcessor.GRAYSCALE);

		return true;
	}
}
