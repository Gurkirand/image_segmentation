package graph;

import util.*;
import java.util.*;
import java.awt.Point;

public class GraphCut<E> extends Graph<E>
{
	private int maxFlow;
	private Director<E> director;
	private Node<E> source;
	private ArrayList<Node<E>> sinks;
	private HashMap<Vertex<E>, Node<E>> nodeSet;
	private HashSet<Node<E>> S, T;
	private LinkedList<Node<E>> activeSource, activeSink, orphans;
	private Pair<Node<E>, Node<E>> path;

	public GraphCut(Graph<E> g, Director<E> d)
	{
		vertexSet = g.vertexSet;
		director = d;
		init();
	}

	public void setGraph(Graph<E> g)
	{
		vertexSet = g.vertexSet;
		init();
	}

	private void init()
	{
		maxFlow = 0;
		source = null;
		sinks = new ArrayList<>();
		nodeSet = new HashMap<>();
		S = new HashSet<>();
		T = new HashSet<>();
		path = new Pair<>(null, null);
		activeSource = new LinkedList<>();
		activeSink = new LinkedList<>();
		orphans = new LinkedList<>();
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
			System.out.println("No source or sink");
			return;
		}
		initRun();
		int counter = 0;
		while (true)
		{
			grow();
			// System.out.print("\nGROW ");
			if (path.first == null)
			{
				break;
			}
			augment();
			// System.out.print("AUGMENT ");
			adopt();
			// System.out.print("ADOPT");
			counter++;
			if (counter % 5 == 0)
				System.out.print("\nCOUNT: " + counter);
		}
		System.out.println("\nCOUNT: " + counter);
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
		activeSource.clear();
		activeSink.clear();
		orphans.clear();

		path.first = null;
		path.second = null;
		
		S.add(source);
		activeSource.add(source);

		for (Node<E> sink: sinks)
		{
			T.add(sink);
			activeSink.add(sink);
		}
	}

	private void grow()
	{
		path.first = null;
		path.second = null;
		Vertex<E> _child;
		Node<E> child;
		Node<E> parent;
		Iterator<Vertex<E>> itr;

		Tree currentTree = Tree.SOURCE;
		HashSet<Node<E>> tree = S;
		LinkedList<Node<E>> active = activeSource;

		do
		{
			parent = active.remove();
			itr = parent.iterator(currentTree);
			while (itr.hasNext())
			{
				_child = itr.next();
				if (parent.adjList.get(_child) <= 0)
				{
					continue;
				}

				child = createNode(_child);
				if (active.contains(child))
				{
					continue;
				}
				if (child.tree == Tree.FREE)
				{
					child.parent = parent;
					child.tree = parent.tree;
					active.add(child);
					tree.add(child);
					parent.children.add(child.vertex);
				} 
				else if (child.tree != parent.tree)
				{
					if (parent.tree == Tree.SOURCE)
					{
						path.first = parent;
						path.second = child;
					}
					else
					{
						path.first = child;
						path.second = parent;
					}
					return;
				}
			}
			if (currentTree == Tree.SOURCE)
			{
				currentTree = Tree.SINK;
				tree = T;
				active = activeSink;
			}
			else
			{
				currentTree = Tree.SOURCE;
				tree = S;
				active = activeSource;
			}
		}
		while (!active.isEmpty());
		return;
	}

	private void augment()
	{
		int flow = findBottleneck();
		for (Vertex<E> v: nodeSet.keySet())
		{
			nodeSet.get(v).pushFlow(flow);
			// pushFlow(nodeSet.get(v), flow);
			// v.unvisit();
		}
		findOrphans();
		maxFlow += flow;
	}

	private int findBottleneck()
	{
		int bottleneck = Integer.MAX_VALUE,
		    cap;
		Node<E> currentNode,
			parent;

		for (currentNode = path.first; currentNode != source; currentNode = currentNode.parent)
		{
			parent = currentNode.parent;
			if (parent == null)
				break;
			cap = currentNode.adjList.get(parent.vertex);
			if (bottleneck > cap)
			{
				bottleneck = cap;
			}
			if (parent == source)
				break;
		}


		for (currentNode = path.second; !sinks.contains(currentNode); currentNode = currentNode.parent)
		{
			parent = currentNode.parent;
			if (parent == null)
				break;
			cap = currentNode.adjList.get(parent.vertex);
			if (bottleneck > cap)
			{
				bottleneck = cap;
			}
			if (sinks.contains(parent))
				break;
		}

		return bottleneck;
	}
	
	private void findOrphans()
	{
		Node<E> parent;
		Iterator<Vertex<E>> childrenItr;
		Vertex<E> _child = null;
		Node<E> child = null;
		int cap = 0;
		
		Iterator<Node<E>> itr = S.iterator();
		while (itr.hasNext())
		{
			parent = itr.next();
			if (parent == source || parent.vertex.visited)
			{
				continue;
			}

			childrenItr = parent.children.iterator();
			while (childrenItr.hasNext())
			{
				_child = childrenItr.next();
				cap = parent.adjList.get(_child);
				if (cap > 0 || _child.visited)
				{
					continue;
				}

				child = nodeSet.get(_child);
				if (child != null && child.tree == parent.tree)
				{
					_child.visit();
					child.parent = null;
					parent.children.remove(child);
					orphans.add(child); 
				}
			}
		}
		itr = T.iterator();
		while (itr.hasNext())
		{
			child = itr.next();
			if (sinks.contains(child) || child.vertex.visited)
			{
				continue;
			}

			parent = child.parent;
			if (parent != null && !parent.vertex.visited && child.adjList.get(parent.vertex) <= 0)
			{
				child.vertex.visit();
				child.parent = null;
				parent.children.remove(child);
				orphans.add(child);
			}
		}
	}


	private void pushFlow(Node<E> node, int f)
	{
		node.pushFlow(f);
		if (node == source || sinks.contains(node))
		{
			return;
		}
		boolean isOrphan = orphans.contains(node);
		if (isOrphan)
		{
			return;
		}
		Vertex<E> _child = null;
		Node<E> child = null;
		int cap = 0;
		
		if (node.tree == Tree.SOURCE)
		{
			Iterator<Vertex<E>> itr = node.children.iterator();
			while (itr.hasNext())
			{
				_child = itr.next();
				cap = node.adjList.get(_child);
				if (cap > 0)
				{
					continue;
				}

				child = nodeSet.get(_child);
				if (child != null && child.tree == node.tree)
				{
					child.parent = null;
					orphans.add(child);
					node.children.remove(child);
					// activeSource.add(node);
				}
			}
		}
		else if (node.tree == Tree.SINK)
		{
			if (node.adjList.get(node.parent.vertex) <= 0)
			{
				orphans.add(node);
				// activeSource.add(node.parent);
				node.parent.children.remove(node);
				node.parent = null;
			}
		}
	}

	private void adopt()
	{
		Node<E> orphan;
		int counter = 0;
		while (!orphans.isEmpty())
		{
			orphan = orphans.remove();
			processOrphan(orphan);
			counter++;
			orphan.vertex.unvisit();
			// if (counter % 100 == 0)
				// System.out.println("Orphans: " + counter);
		}
	}

	private void processOrphan(Node<E> orphan)
	{
		Vertex<E> vertex;
		Node<E> node;
		HashSet<Node<E>> tree;
		LinkedList<Node<E>> active;
		Tree treeType = orphan.tree;
		if (treeType == Tree.SOURCE)
		{
			tree = S;
			active = activeSource;
			treeType = Tree.SINK;
		}
		else
		{
			tree = T;
			active = activeSink;
			treeType = Tree.SOURCE;
		}

		Iterator<Vertex<E>> parentItr = orphan.iterator(treeType);
		boolean foundParent = false;
		
		while (parentItr.hasNext())
		{
			vertex = parentItr.next();
			node = nodeSet.get(vertex);
			if (node != null && node.tree == orphan.tree && !node.vertex.visited && orphan.adjList.get(vertex) > 0)
			{
				if (!foundParent)
				{
					orphan.parent = node;
					node.children.add(orphan.vertex);
					foundParent = true;
				}
				
			}
		}
		
		if (foundParent)
		{
			return;
		}
		
		Iterator<Vertex<E>> children = orphan.childrenIterator();
		while (children.hasNext())
		{
			vertex = children.next();
			node = nodeSet.get(vertex);
			if (orphan.parent == node)
			{
				node.children.remove(orphan);
			}
			if (node != null && node.tree == orphan.tree)
			{
				node.parent = null;
				orphan.children.remove(node);
				orphans.add(node);
			}
		}
		tree.remove(orphan);
		orphan.tree = Tree.FREE;
		active.remove(orphan);
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
		private ArrayList<Vertex<E>> forwardAdjList;
		private ArrayList<Vertex<E>> backwardAdjList;
		private HashSet<Vertex<E>> children = new HashSet<>();
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
			forwardAdjList = new ArrayList<>();
			backwardAdjList = new ArrayList<>();
			Iterator<Vertex<E>> itr = adjList.keySet().iterator();
			Vertex<E> v;
			int d;

			while (itr.hasNext())
			{
				v = itr.next();
				d = director.direct(vertex.data, v.data);
				if (d > 0)
				{
					forwardAdjList.add(v);
				}
				else
				{
					backwardAdjList.add(v);
				}
			}
		}

		private Iterator<Vertex<E>> iterator(Tree t)
		{
			switch (t){
				case FREE:
					return adjList.keySet().iterator();
				case SOURCE:
					return forwardAdjList.iterator();
				case SINK:
					return backwardAdjList.iterator();
			}
			return null;
		}
		
		private Iterator<Vertex<E>> childrenIterator()
		{
			return children.iterator();
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
