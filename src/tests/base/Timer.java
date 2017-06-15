package tests.base;
import java.util.function.Function;

public class Timer
{
	public static <A, R> R time(String name, Function<A, R> f, A a, R expected)
	{
		long start, end;
		start = System.nanoTime();
		R r = f.apply(a);
		end = System.nanoTime();
		System.out.println("\n" + name + " Returned: " + r);
		if (expected != null)
		{
			System.out.println("Expected: " + expected);
			System.out.println("Method " + (r.equals(expected) ? "Succeeded": "Failed"));
		}
		else
		{
			System.out.println("Method " + (r != null ? "Succeeded": "Failed"));
		}
		System.out.println(name + " elapsed time: " + ((double) (end - start)));
		return r;
	}

	public static <A, R> R time(String name, Function<A, R> f, A a)
	{
		return time(name, f, a, null);
	}
}
