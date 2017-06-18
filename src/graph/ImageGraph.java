package image;
import graph.Graph;
import image.ImageMatrix;
import image.Pixel;

public class ImageGraph extends Graph<Pixel>
{
	public ImageGraph() {
		super();
	}
	
	public ImageGraph(ImageMatrix imageMatrix) {
		
		super();
		populateVertexSet(imageMatrix);
		
	}
	
	public void load(ImageMatrix i) {}
	
	public void reset() {
		vertexSet.clear();
	}
	
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
	
}
