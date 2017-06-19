package graph;
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

	public void loadCrop(ImageMatrix i, int start_x, int start_y)
	{
		populateVertexSet(i, start_x, start_y);
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

	private void populateVertexSet(ImageMatrix img, int start_x, int start_y)
	{
		int[][] matrix = img.matrix;
	 	int width = matrix.length,
	 	    height = matrix[0].length,
	 	    strideH = 1,
	 	    limitH = height-1,
	 	    limitW = width-1,
	 	    i = 0, j, ti, tj;
			
	 	Pixel current,
	 	      p;

	 	for(; i < limitW; i++)
	 	{
			ti = i + start_x;
	 		for(j = 0; j < limitH; j += strideH)
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
	
}
