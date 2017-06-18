package graph;

public abstract class Director<E>
{
	public E source;

	public Director() {};

	public Director(E s)
	{
		source = s;
	}

	public void setSource(E s)
	{
		source = s;
	}
	
	public abstract int direct(E v1, E v2);
}
