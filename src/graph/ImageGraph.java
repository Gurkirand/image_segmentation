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
	
	/*Already in Graph*/
	public void reset() {
		vertexSet.clear();
	}
	
	/*Might be better to just read directly from the Pixel[][] matrix using add to vertex set method in Graph class*/
	private Vertex<Pixel>[][] toVertexMatrix(int[][] matrix){ //from matrix of int to a matrix of vertices
		
		int height = matrix.length;
		int width = matrix[0].length;
		Vertex<Pixel>[][] vertexMatrix = (Vertex<Pixel>[][]) new Object[height][width];
		
		for(int i=0; i<height; ++i){
			
			for(int j=0; j<width; ++j){
				
				Pixel pixel = new Pixel(matrix[i][j], i, j);
				Vertex<Pixel> vertex = new Vertex<>(pixel);
				vertexMatrix[i][j] = vertex;
				
			}
		}
		return vertexMatrix;
	}
	
	private Vertex<Pixel> alreadyInSet(Vertex<Pixel> vertex){
		
		if(!vertexSet.containsValue(vertex)){
			vertexSet.put(vertex.getData(), vertex);
		}else{
			vertex = vertexSet.get(vertex.getData());
		}
		return vertex;
	}
	
	private void populateVertexSet(ImageMatrix imageMatrix){
		
		Vertex<Pixel>[][] vertexMatrix = toVertexMatrix(imageMatrix.matrix);
		int height = vertexMatrix.length;
		int width = vertexMatrix[0].length;
		
		for(int i=0; i<height; ++i){
			
			for(int j=0; j<width; ++j){
				
				
				Vertex<Pixel> currVertex = vertexMatrix[i][j];
				currVertex = alreadyInSet(currVertex);
				
				if(j < width-1){
					Vertex<Pixel> rightVertex = vertexMatrix[i][j+1];
					rightVertex = alreadyInSet(rightVertex);
					currVertex.addToAdjList(rightVertex, currVertex.getData().weightDiff(rightVertex.getData()));
				}
				
				if(j > 0){
					Vertex<Pixel> leftVertex = vertexMatrix[i][j-1];
					leftVertex = alreadyInSet(leftVertex);
					currVertex.addToAdjList(leftVertex, currVertex.getData().weightDiff(leftVertex.getData()));
				}
				
				if(i < height-1){
					Vertex<Pixel> bottomVertex = vertexMatrix[i+1][j];
					bottomVertex = alreadyInSet(bottomVertex);
					currVertex.addToAdjList(bottomVertex, currVertex.getData().weightDiff(bottomVertex.getData()));
				}
				
				if(i > 0){
					Vertex<Pixel> topVertex = vertexMatrix[i-1][j];
					topVertex = alreadyInSet(topVertex);
					currVertex.addToAdjList(topVertex, currVertex.getData().weightDiff(topVertex.getData()));
				}
				
			}
		}
		
	}

	//This might be faster and easier:
	// private void populateVertexSet(ImageMatrix img)
	// {
	// 	int[][] matrix = img.matrix;
	// 	int width = matrix.length,
	// 	    height = matrix[0].length,
	// 	    strideH = 1,
	// 	    limitH = height-1,
	// 	    limitW = width-1,
	// 	    i = 0, j;
	// 	Pixel current,
	// 	      p;

	// 	for(; i < limitW; i++)
	// 	{
	// 		for(j = 0; j < limitH; j += strideH)
	// 		{
	// 			current = new Pixel(matrix[i][j], i, j);
	// 			if (i < width - 1)
	// 			{
	// 				p = new Pixel(matrix[i+1][j], i+1, j);
	// 				addEdge(current, p, weightDifference(current, p));
	// 			}
	// 			if (j < height - 1)
	// 			{
	// 				p = new Pixel(matrix[i][j+1], i, j+1);
	// 				addEdge(current, p, weightDifference(current, p));
	// 			}
	// 		}
	// 	}
		
	// }

	// private double weightDifference(Pixel p1, Pixel p2)
	// {
	// 	return 100 * (p1.value > p2.value ? p2.value / (1.0 * p1.value) : p1.value / (1.0 * p2.value));
	// }
	
}
