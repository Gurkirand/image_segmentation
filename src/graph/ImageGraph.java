package graph;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.ArrayList;

import java.awt.Point;

import image.ImageMatrix;
import image.Pixel;

public class ImageGraph extends Graph<Pixel>
{
	ImageMatrix imageMatrix;
	Point start;

	public ImageGraph() {
		super();
	}

	public ImageGraph(ImageMatrix i) {
		super();
		populateVertexSet(i);
	}

	public void load(ImageMatrix i)
	{
		populateVertexSet(i);
	}

	public void load(ImageMatrix i, Point start)
	{
		populateVertexSet(i, start);
	}

	private void populateVertexSet(ImageMatrix img)
	{
		populateVertexSet(img, new Point(0, 0));
	}

	// private void loadNewCrop(ImageMatrix i, Point newStart)
	// {
		
	// }

	private void populateVertexSet(ImageMatrix img, Point s)
	{
		start = s;
		imageMatrix = img;
		int[][] matrix = img.matrix;
		int width = img.getWidth(),
		    height = img.getHeight(),
		    i, j, ti, tj;

		Pixel current,
		      p;

		for(i = 0; i < width; i++)
		{
			ti = i + start.x;
			for(j = 0; j < height; j++)
			{
				tj = j + start.y;
				current = new Pixel(matrix[i][j], ti, tj);
				if (i < width - 1)
				{
					p = new Pixel(matrix[i+1][j], ti+1, tj);
					addEdge(current, p, weightDifference(current, p));
				}
				if (j < height - 1)
				{
					p = new Pixel(matrix[i][j+1], ti, tj+1);
					addEdge(current, p, weightDifference(current, p));
				}
			}
		}

	}

	private double weightDifference(Pixel p1, Pixel p2)
	{
		return 100 * (p1.value > p2.value ? p2.value / (1.0 * p1.value) : p1.value / (1.0 * p2.value));
	}

	public ImageMatrix load(File file){
		ArrayList<Pixel> pixels = new ArrayList<>();
		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE,
		    maxX = 0, maxY = 0;

		try {
			Scanner scanner = new Scanner(file);
			int val, x, y;
			Double edge;
			String line;
			StringTokenizer st;
			Vertex<Pixel> curr;
			this.clear();

			while(scanner.hasNextLine()){
				line = scanner.nextLine();
				st = new StringTokenizer(line, " ");
				val = Integer.parseInt(st.nextToken());
				x = Integer.parseInt(st.nextToken());
				y = Integer.parseInt(st.nextToken());

				Pixel pixel = new Pixel(val, x, y);
				pixels.add(pixel);
				addToVertexSet(pixel);

				if (x > maxX)
					maxX = x;
				if (y > maxY)
					maxY = y;
				if (x < minX)
					minX = x;
				if (y < minY)
					minY = y;

				while(st.hasMoreTokens()){
					val = Integer.parseInt(st.nextToken());
					x = Integer.parseInt(st.nextToken());
					y = Integer.parseInt(st.nextToken());
					edge = Double.parseDouble(st.nextToken());

					addEdge(new Pixel(val, x, y), pixel, edge);

				}

			}

		}catch(FileNotFoundException e){
			e.printStackTrace();
			return null;
		}

		int[][] simpleMatrix = new int[maxX - minX +1][maxY - minY +1];
		for (Pixel p: pixels)
		{
			simpleMatrix[p.coordinate.x - minX][p.coordinate.y - minY] = p.value;
		}

		return new ImageMatrix(simpleMatrix);
	}

	public String BFT()
	{
		Pixel startP = new Pixel(imageMatrix.matrix[0][0], start.x, start.y);
		StringVisitor visitor = new StringVisitor();
		breadthFirstTraversal(startP, visitor);
		return visitor.string.toString();
	}

	public String DFT()
	{
		Pixel startP = new Pixel(imageMatrix.matrix[0][0], start.x, start.y);
		StringVisitor visitor = new StringVisitor();
		depthFirstTraversal(startP, visitor);
		return visitor.string.toString();
	}


	class StringVisitor implements Visitor<Pixel>
	{
		StringBuilder string = new StringBuilder();
		public void visit(Pixel obj)
		{
			string.append("\n" + obj);
		}
	}
}
