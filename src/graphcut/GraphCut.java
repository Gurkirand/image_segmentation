package graphcut;
import java.awt.Point;

public class GraphCut<E> extends Graph<E>
{
	private int maxFlow;
	private HashMap<E, Vertex<E>> S, T, P;
	private ArrayList<Vertex<E>> A, O;
	public GraphCut() {}
	public GraphCut(Graph source) {}
	public setGraph(Graph g) {}
	public void setSource(Point p) {}
	public void setSource(int x, int y) {}
	public void addSink(Point p) {}
	public void addSink(int x, int y) {}
	public void removeSink(Point p) {}
	public void removeSink(int x, int y) {}
	public HashMap<E, Vertex<E>> getSourceTree() {}
	public void run() {}
	private void grow() {}
	private void augment() {}
	private void adopt() {}
}
