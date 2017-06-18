

import util.*;
import java.util.*;
import java.util.Map.Entry;

class Vertex<E>
{
	public static final double INFINITY = Double.MAX_VALUE;
	public HashMap<E, Pair<Vertex<E>, Double> > adjList
		= new HashMap<E, Pair<Vertex<E>, Double> >();
	public E data;
	public boolean visited;

	public Vertex( E x )
	{
		data = x;
	}

	public Vertex() { this(null); }

	public E getData(){ return data; }

	public boolean isVisited(){ return visited; }

	public void visit(){ visited = true; }

	public void unvisit(){ visited = false; }

	public Iterator<Map.Entry<E, Pair<Vertex<E>, Double>>> iterator()
	{
		return adjList.entrySet().iterator();
	}

	public void addToAdjList(Vertex<E> neighbor, double cost)
	{
		if( adjList.get(neighbor.data) == null)
			adjList.put(neighbor.data, new Pair<Vertex<E>, Double> (neighbor, cost) );
		// Note: if you want to change the cost, you'll need to remove it and then add it back
	}

	public void addToAdjList(Vertex<E> neighbor, int cost)
	{
		addToAdjList( neighbor, (double)cost );
	}

	public boolean equals(Object rhs)
	{
		if( !( rhs instanceof Vertex<?>) )
			return false;
		Vertex<E> other = (Vertex<E>)rhs;

		return (data.equals(other.data));

	}

	public int hashCode()
	{
		return (data.hashCode());
	}

	public void showAdjList()
	{
		Iterator<Entry<E, Pair<Vertex<E>, Double>>> iter ;
		Entry<E, Pair<Vertex<E>, Double>> entry;
		Pair<Vertex<E>, Double> pair;

		System.out.print( "Adj List for " + data + ": ");
		iter = adjList.entrySet().iterator();
		while( iter.hasNext() )
		{
			entry = iter.next();
			pair = entry.getValue();
			System.out.print( pair.first.data + "("
					+ String.format("%3.1f", pair.second)
					+ ") " );
		}
		System.out.println();
	}

}

