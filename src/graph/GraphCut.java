package graph;
import util.*;
import graph.*;
import java.util.*;
import java.awt.Point;

public class GraphCut<E> extends Graph<E>
{
	private int maxFlow;
	private Director<E> director;
	private Node<E> source;
	private Node<E> global_sink;
	private ArrayList<Node<E>> sinks;
	private HashMap<Vertex<E>, Node<E>> nodeSet;
	private LinkedList<Node<E>> A, O;
	private HashSet<Node<E>> S, T;
	private Pair<Node<E>, Node<E>> P;

	public GraphCut(Graph<E> g, Director<E> d)
	{
		vertexSet = g.vertexSet;
		director = d;
		init();
	}

	private void init()
	{
		sinks = new ArrayList<>();
		nodeSet = new HashMap<>();
		S = new HashSet<>();
		T = new HashSet<>();
		P = new Pair<>(null, null);
		A = new LinkedList<>();
		O = new LinkedList<>();
	}

	public boolean setSource(E s)
	{
		director.source = s;
		Node<E> _source = createNode(vertexSet.get(s));
		if (_source != null)
		{
			source = _source;
			source.tree = Tree.SOURCE;
			return true;
		}
		return false;
	}

	public boolean addSink(E s)
	{
		Node<E> sink = createNode(vertexSet.get(s));
		if (sink != null)
		{
			sink.tree = Tree.SINK;
			global_sink = sink;
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

	public ArrayList<E> getSourceTree()
	{
		Iterator<Node<E>> itr = S.iterator();
		ArrayList<E> tree = new ArrayList<>();

		while (itr.hasNext())
		{
			tree.add(itr.next().vertex.data);
		}
		return tree;
	}

	public ArrayList<E> getSinkTree()
	{
		Iterator<Node<E>> itr = T.iterator();
		ArrayList<E> tree = new ArrayList<>();

		while (itr.hasNext())
		{
			tree.add(itr.next().vertex.data);
		}
		return tree;
	}

	public int getMaxFlow()
	{
		return maxFlow;
	}

	public void run()
	{
		if (source == null || sinks.isEmpty())
		{
			return;
		}
		int counter = 0;
		initRun();
		while (true)
		{
			grow();
			if (P.first == null)
			{
				break;
			}
			augment();

			if (maxFlow >= 10000)
			{
				break;
			}
			adopt();
			counter++;
		}
	}

	private Node<E> createNode(Vertex<E> v)
	{
		Node<E> n = nodeSet.get(v);
		if (n == null)
		{
			n = new Node<E>(v);
			n.pushFlow(maxFlow);
			n.createDirectedAdjLists(director);
			nodeSet.put(v, n);
		}
		return n;
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
		A.add(source);

		for (Node<E> sink: sinks)
		{
			T.add(sink);
			A.add(sink);
		}
	}

	private void grow()
	{
		P.first = null;
		P.second = null;
		Vertex<E> _child;
		Node<E> child;
		Node<E> parent;
		Iterator<Vertex<E>> itr;
		HashSet<Node<E>> tree;
		int choose = 0;

		while (!A.isEmpty())
		{
			choose = (int) Math.floor(Math.random() * A.size());
			parent = A.remove(choose);
			if (parent.tree == Tree.SOURCE)
			{
				tree = S;
				itr = parent.forwardIterator();
			}
			else
			{
				tree = T;
				itr = parent.backwardIterator();
			}
			while (itr.hasNext())
			{
				_child = itr.next();
				if (parent.adjList.get(_child) <= 0)
				{
					continue;
				}

				child = createNode(_child);
				if (A.contains(child))
				{
					continue;
				}
				if (child.tree == Tree.FREE)
				{
					child.tree = parent.tree;
					A.add(child);
					tree.add(child);
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
		int flow = findBottleneck();
		for (Vertex<E> v: nodeSet.keySet())
		{
			pushFlow(nodeSet.get(v), flow);
			v.unvisit();
		}
		maxFlow += flow;
	}

	protected int findBottleneck()
	{		
		int bottleneck = 10000;
		int capacity;

		Iterator<Vertex<E>> itr;
		Node<E> current = null;
		Node<E> child;	
		Vertex<E> _child;
		HashSet<Node<E>> tree = S;
		Tree treeType = Tree.SOURCE;
		
		LinkedList<Node<E>> searchSet = new LinkedList<>();
		searchSet.add(source);
		
		while (!searchSet.isEmpty() && (current != global_sink))
		{
			current = searchSet.remove();

			itr = current.forwardIterator();
			while (itr.hasNext())
			{
				_child = itr.next();
				child = nodeSet.get(_child);
				if (child != null && child.tree == treeType && !_child.isVisited())
				{
					_child.visit();
					capacity = current.adjList.get(_child);
					if (capacity <= 0)
					{
						continue;
					}
					bottleneck = bottleneck > capacity ? capacity: bottleneck;
					searchSet.add(child);
					if (child == P.first)
					{
						tree = T;
						treeType = Tree.SINK;
						searchSet.clear();
						searchSet.add(P.second);
						break;
					}
				}
			}
		}

		return bottleneck;
	}

	private void pushFlow(Node<E> n, int f)
	{
		Iterator<Vertex<E>> itr = n.adjList.keySet().iterator();
		Vertex<E> _child = null;
		Node<E> child = null;
		boolean orphan = O.contains(n);
		while (itr.hasNext())
		{
			_child = itr.next();
			int cap = n.adjList.get(_child) - f;
			n.adjList.put(_child, cap);
			if (cap <= 0)
			{
				if (n.forwardAdjList.get(_child) == null)
				{
					continue;
				}
				child = nodeSet.get(_child);
				if (child != null && child.tree != Tree.FREE && child.tree == n.tree && !O.contains(child))
				{
					if (n.tree == Tree.SOURCE)
					{
						child.parent = null;
						O.add(child);
					}
					else if (!orphan)
					{
						n.parent = null;
						O.add(n);
					}
				}
			}
		}
	}

	private void adopt()
	{
		Node<E> orphan;
		Vertex<E> vertex;
		Node<E> node;
		Iterator<Vertex<E>> parentItr;
		Iterator<Vertex<E>> neighborItr;
		boolean foundParent;

		while (!O.isEmpty())
		{
			foundParent = false;
			orphan = O.remove();
			if (orphan.tree == Tree.SOURCE)
			{
				parentItr = orphan.backwardIterator();
			}
			else
			{
				parentItr = orphan.forwardIterator();
			}
			while (parentItr.hasNext())
			{
				vertex = parentItr.next();
				node = nodeSet.get(vertex);
				if (node == null)
				{
					continue;
				}
				if (orphan.adjList.get(vertex) > 0 && !O.contains(node))
				{
					orphan.parent = node;
					foundParent = true;
					break;
				}
			}
			if (foundParent)
			{
				continue;
			}
			neighborItr = orphan.iterator();
			while (neighborItr.hasNext())
			{
				vertex = neighborItr.next();
				node = nodeSet.get(vertex);
				if (node == null || node.tree != orphan.tree)
				{
					continue;
				}
				if (node.parent == orphan)
				{
					if (!O.contains(node))
					{
						node.parent = null;
						O.add(node);
					}
				}
				else if (orphan.adjList.get(vertex) > 0)
				{
					A.add(node);
				}
			}
			orphan.parent = null;
			if (orphan.tree == Tree.SOURCE)
			{
				S.remove(orphan);
			} 
			else
			{
				T.remove(orphan);
			}
			orphan.tree = Tree.FREE;
			A.remove(orphan);
		}
	}

	private enum Tree
	{
		SOURCE, SINK, FREE
	}

	private class Node<E>
	{
		private Vertex<E> vertex;
		private Node<E> parent;
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

		private Node(Vertex<E> v, Node<E> p)
		{
			this(v);
			parent = p;
		}

		private Node(Vertex<E> v, Node<E> p, Tree t)
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

		private void createDirectedAdjLists(Director<E> director)
		{
			forwardAdjList = new HashMap<>();
			backwardAdjList = new HashMap<>();
			Iterator<Vertex<E>> itr = adjList.keySet().iterator();
			Vertex<E> v;
			int d;

			while (itr.hasNext())
			{
				v = itr.next();
				d = director.direct(vertex.data, v.data);
				if (d > 0)
				{
					forwardAdjList.put(v, adjList.get(v));
				}
				else
				{
					backwardAdjList.put(v, adjList.get(v));
				}
			}
		}

		private Iterator<Vertex<E>> iterator()
		{
			return adjList.keySet().iterator();
		}

		private Iterator<Vertex<E>> forwardIterator()
		{
			return forwardAdjList.keySet().iterator();
		}

		private Iterator<Vertex<E>> backwardIterator()
		{
			return backwardAdjList.keySet().iterator();
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
