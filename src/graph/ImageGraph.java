package graph;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.ArrayList;

import image.ImageMatrix;
import image.Pixel;

public class ImageGraph extends Graph<Pixel>
{
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

	public void load(ImageMatrix i, int start_x, int start_y)
	{
		populateVertexSet(i, start_x, start_y);
	}
	
	/*Might be better to just read directly from the Pixel[][] matrix using add to vertex set method in Graph class*/
	private void populateVertexSet(ImageMatrix img)
	{
		populateVertexSet(img, 0, 0);
	 }


	private void populateVertexSet(ImageMatrix img, int start_x, int start_y)
	{
		int[][] matrix = img.matrix;
	 	int width = img.getWidth(),
	 	    height = img.getHeight(),
	 	    i, j, ti, tj;
			
	 	Pixel current,
	 	      p;

	 	for(i = 0; i < width; i++)
	 	{
			ti = i + start_x;
	 		for(j = 0; j < height; j++)
	 		{
				tj = j + start_y;
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
		ArrayList<ArrayList<Integer>> matrix = new ArrayList<>();
		ArrayList<Integer> column;
		
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
				addToVertexSet(pixel);

				if (matrix.size() <= x)
				{
					while (matrix.size() <= x)
						matrix.add(new ArrayList<>());
				}
				column = matrix.get(x);
				if (column.size() <= y)
				{
					while (column.size() <= y)
						column.add(-1);
				}
				column.set(y, val);
				
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
		}
		
		int[][] simpleMatrix = new int[matrix.size()][matrix.get(0).size()];
		for (int i = 0; i < matrix.size(); i++)
		{
			for (int j = 0; j < matrix.get(0).size(); j++)
				simpleMatrix[i][j] = matrix.get(i).get(j);
		}

		return new ImageMatrix(simpleMatrix);
	}
	
}
