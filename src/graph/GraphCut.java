import util.*;
import graph.*;
import java.util.*;
import java.awt.Point;

public class GraphCut<E> extends Graph<E>
{
	private int maxFlow;
	private Node<E> source;
	private Node<E> global_sink;
	private ArrayList<Node<E>> sinks;
	private HashMap<Vertex<E>, Node<E>> nodeSet;
	private LinkedQueue<Node<E>> A, O;
	private HashSet<Node<E>> S, T;
	private Pair<Node<E>, Node<E>> P;

	public GraphCut()
	{
		super();
		init();
	}

	public GraphCut(Graph<E> g)
	{
		init();
		vertexSet = g.vertexSet;
	}

	private void init()
	{
		S = new HashSet<>();
		T = new HashSet<>();
		P = new Pair<>(null, null);
		sinks = new ArrayList<>();
		A = new LinkedQueue<>();
		O = new LinkedQueue<>();
	}

	public boolean setSource(E s)
	{
		Node<E> _source = createNode(vertexSet.get(s));
		if (_source != null)
		{
			source = _source;
			return true;
		}
		return false;
	}

	public boolean addSink(E s)
	{
		Node<E> sink = createNode(vertexSet.get(s));
		if (sink != null)
		{
			return sinks.add(sink);
		}
		return false;
	}

	public boolean removeSink(E s)
	{
		Node<E> sink = nodeSet.get(vertexSet.get(s));
		if (sink != null)
		{
			return sinks.remove(sink);
		}
		return false;
	}

	public void run()
	{
		initRun();
		while (true)
		{
			grow();
			if (P.first == null)
			{
				break;
			}
			augment();
			adopt();
		}
	}

	private void initRun()
	{
		S.clear();
		T.clear();
		A.clear();
		O.clear();

		P.first = null;
		P.second = null;
		
		S.add(source);
		A.enqueue(source);

		for (Node<E> sink: sinks)
		{
			T.add(sink);
			A.enqueue(sink);
		}
	}

	private void grow()
	{
		Vertex<E> _child;
		Node<E> child;
		Node<E> parent;
		Iterator<Vertex<E>> itr;

		while (!A.isEmpty())
		{
			parent = A.dequeue();
			itr = parent.iterator();
			while (itr.hasNext())
			{
				_child = itr.next();
				if (parent.adjList.get(_child) <= 0)
				{
					continue;
				}

				child = createNode(_child);
				if (child.tree == Tree.FREE)
				{
					child.tree = parent.tree;
					A.enqueue(child);
				} 
				else if (child.tree != parent.tree)
				{
					if (parent.tree == Tree.SOURCE)
					{
						P.first = parent;
						P.second = child;
					}
					else
					{
						P.first = child;
						P.second = parent;
					}
					return;
				}
			}
		}
		return;
	}
	
	private void augment()
	{
		Node<E> parent;
		Node<E> child;
		int flow = findBottleneck();
		for (Vertex<E> v: nodeSet.keySet())
		{
			pushFlow(nodeSet.get(v), flow);
			v.unvisit();
		}
		maxFlow += flow;
	}

	private void adopt()
	{
		Node<E> parent;
		while (!O.isEmpty())
		{
			parent = O.dequeue();
		}
	}
	
	protected int findBottleneck()
	{
		int bottleneck = 10000;
		int capacity;

		Iterator<Vertex<E>> itr;
		Vertex<E> _child;
		Node<E> child;	
		Node<E> current = null;
		HashSet<Node<E>> tree = S;
		
		LinkedQueue<Node<E>> searchSet = new LinkedQueue<>();
		searchSet.enqueue(source);
		
		while (!searchSet.isEmpty() && current != global_sink)
		{
			current = searchSet.dequeue();

			itr = current.iterator();
			while (itr.hasNext())
			{
				_child = itr.next();
				child = nodeSet.get(_child);
				if (child != null && child.tree != Tree.FREE && !_child.isVisited())
				{
					_child.visit();
					if (tree.contains(child))
					{
						capacity = current.adjList.get(_child);
						bottleneck = bottleneck > capacity ? capacity: bottleneck;
						searchSet.enqueue(child);
					}
					if (child == P.first)
					{
						tree = T;
					}
				}
			}
		}

		return bottleneck;
	}

	private void pushFlow(Node<E> n, int f)
	{
		Iterator<Vertex<E>> itr = n.adjList.keySet().iterator();
		Vertex<E> _child;
		Node<E> child;
		while (itr.hasNext())
		{
			_child = itr.next();
			int cap = n.adjList.get(_child) - f;
			n.adjList.put(_child, cap);
			if (cap <= 0 && !_child.isVisited())
			{
				child = nodeSet.get(_child);
				if (child != null)
				{
					O.enqueue(child);
				}
			}
			else
			{
				_child.unvisit();
			}
		}
	}



	private Node<E> createNode(Vertex<E> v)
	{
		Node<E> n = nodeSet.get(v);
		if (n == null)
		{
			n = new Node<E>(v);
			n.pushFlow(maxFlow);
		}
		return n;
	}

	private enum Tree
	{
		SOURCE, SINK, FREE
	}

	private class Node<E>
	{
		private Vertex<E> vertex;
		private Vertex<E> parent;
		private HashMap<Vertex<E>, Integer> adjList;
		private HashMap<Vertex<E>, Integer> forwardAdjList;
		private HashMap<Vertex<E>, Integer> backwardAdjList;
		private Tree tree = Tree.FREE;

		// Maybe needed?
		// private int depth;

		private Node(Vertex<E> v)
		{
			vertex = v;
			init();
		}

		private Node(Vertex<E> v, Vertex<E> p)
		{
			this(v);
			parent = p;
		}

		private Node(Vertex<E> v, Vertex<E> p, Tree t)
		{
			this(v, p);
			tree = t;
		}

		private void init()
		{
			forwardAdjList = new HashMap<>();
			backwardAdjList = new HashMap<>();
			Pair<Vertex<E>, Double> p;
			Iterator<E> itr = vertex.adjList.keySet().iterator();
			while (itr.hasNext())
			{
				p = vertex.adjList.get(itr.next());
				adjList.put(p.first, (int) Math.floor(p.second));
			}
		}

		// private int compareDistances(Vertex<E> n)
		// {
		// 	E s = source.vertex.getData();
		// 	int d1 = vertex.getData().distance(s);
		// 	int d2 = n.getData().distance(s);
		// 	return d1 - d2;
		// }

		private Iterator<Vertex<E>> iterator()
		{
			return adjList.keySet().iterator();
		}

		public void pushFlow(int f)
		{
			Iterator<Vertex<E>> itr = adjList.keySet().iterator();
			Vertex<E> v;
			while (itr.hasNext())
			{
				v = itr.next();
				adjList.put(v, adjList.get(v) - f);
			}
		}

		public boolean equals(Object o)
		{
			if (!(o instanceof Node))
			{
				return false;
			}
			Node<E> other = (Node<E>) o;
			return vertex.equals(other.vertex);
		}

		public int hashCode()
		{
			return vertex.hashCode();
		}
	}
}
