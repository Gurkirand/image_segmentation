package graph;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.StringTokenizer;

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
	
	/*Might be better to just read directly from the Pixel[][] matrix using add to vertex set method in Graph class*/
	private void populateVertexSet(ImageMatrix img)
	{
		int[][] matrix = img.matrix;
	 	int width = matrix.length,
	 	    height = matrix[0].length,
	 	    strideH = 1,
	 	    limitH = height-1,
	 	    limitW = width-1,
	 	    i = 0, j;
	 	Pixel current,
	 	      p;

	 	for(; i < limitW; i++)
	 	{
	 		for(j = 0; j < limitH; j += strideH)
	 		{
	 			current = new Pixel(matrix[i][j], i, j);
	 			if (i < width - 1)
	 			{
	 				p = new Pixel(matrix[i+1][j], i+1, j);
					addEdge(current, p, weightDifference(current, p));
	 			}
	 			if (j < height - 1)
	 			{
					p = new Pixel(matrix[i][j+1], i, j+1);
					addEdge(current, p, weightDifference(current, p));
	 			}
	 		}
	 	}
		
 }

	private double weightDifference(Pixel p1, Pixel p2)
	{
		return 100 * (p1.value > p2.value ? p2.value / (1.0 * p1.value) : p1.value / (1.0 * p2.value));
	}
	
	public void load(File file){
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

	}
	
}
