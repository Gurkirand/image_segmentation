package graph;
import util.*;
import graph.*;
import java.util.*;
import java.awt.Point;

public class GraphCut<E> extends Graph<E>
{
	private int maxFlow;
	private Vertex<E> source;
	private ArrayList<Vertex<E>> sinks;
	private HashMap<Vertex<E>, Node<E>> nodeSet;
	private ArrayList<Node<E>> A, O;
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
		A = new ArrayList<>();
		O = new ArrayList<>();
	}

	public boolean setSource(E s)
	{
		Vertex<E> _source = vertexSet.get(s);
		if (_source != null)
		{
			source = _source;
			return true;
		}
		return false;
	}

	public boolean addSink(E s)
	{
		Vertex<E> sink = vertexSet.get(s);
		if (sink != null)
		{
			return sinks.add(sink);
		}
		return false;
	}

	public boolean removeSink(E s)
	{
		Vertex<E> sink = vertexSet.get(s);
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
		
		Node<E> s = createNode(source);
		S.add(s);
		A.add(s);

		for (Vertex<E> sink: sinks)
		{
			s = createNode(sink);
			T.add(s);
			A.add(s);
		}
	}

	private void grow()
	{
		Node<E> child;
		Iterator<Vertex<E>> itr;

		for (Node<E> parent: A)
		{
			itr = parent.iterator();
			while (itr.hasNext())
			{
				child = createNode(itr.next());
				if (child.tree == Tree.FREE)
				{
					child.tree = parent.tree;
					A.add(child);
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
			A.remove(parent);
		}
		return;
	}

	private void augment()
	{
	}

	private void adopt() {}

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
			adjList = new HashMap<>();
			Pair<Vertex<E>, Double> p;
			Iterator<E> itr = vertex.adjList.keySet().iterator();
			while (itr.hasNext())
			{
				p = vertex.adjList.get(itr.next());
				adjList.put(p.first, (int) Math.floor(p.second));
			}
		}

		private void pushFlow(int f)
		{
			Iterator<Vertex<E>> itr = adjList.keySet().iterator();
			while (itr.hasNext())
			{
				Vertex<E> v = itr.next();
				adjList.put(v, adjList.get(v) - f);
			}
		}

		private Iterator<Vertex<E>> iterator()
		{
			return adjList.keySet().iterator();
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
