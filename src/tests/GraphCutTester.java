package tests;

import image.*;
import graph.*;
import java.util.*;


public class GraphCutTester
{
	public static void main(String[] args)
	{
		Graph<Pixel> g = new Graph<>();
		Pixel[][] img = new Pixel[10][10];
		int i, j;
		int x = 5;
		for (i = 0; i < 10; i++)
		{
			for (j = 0; j < 10; j++)
			{
				img[i][j] = new Pixel(x, i, j);
				x += 5;
			}
		}

		Pixel p;
		Pixel q;
		double w;
		for (i = 0; i < 10; i++)
		{
			for (j = 0; j < 10; j++)
			{
				p = img[i][j];
				if (j > 0)
				{
					q = img[i][j-1];
					w = 100 * (q.value > p.value ? p.value / (1.0 * q.value): (1.0 * q.value) / p.value);
					g.addEdge(p, q, w);
				}
				if (i > 0)
				{
					q = img[i-1][j];
					w = 100 * (q.value > p.value ? p.value / (1.0 * q.value): (1.0 * q.value) / p.value);
					g.addEdge(p, q, w);
				}
				if (j < 9)
				{
					q = img[i][j+1];
					w = 100 * (q.value > p.value ? p.value / (1.0 * q.value): (1.0 * q.value) / p.value);
					g.addEdge(p, q, w);
				}
				if (i < 9)
				{
					q = img[i+1][j];
					w = 100 * (q.value > p.value ? p.value / (1.0 * q.value): (1.0 * q.value) / p.value);
					g.addEdge(p, q, w);
				}
			}
		}
	
		GraphCut<Pixel> gc = new GraphCut<>(g, new ImageDirector());
		gc.setSource(img[4][4]);
		gc.addSink(img[9][9]);
		// gc.addSink(img[0][0]);
		gc.run();
		ArrayList<Pixel> s = gc.getSourceTree();
		ArrayList<Pixel> t = gc.getSinkTree();
		System.out.println(s.size());
		System.out.println(t.size());
		System.out.println(gc.getMaxFlow());

		for (Pixel pix: s)
			img[pix.coordinate.x][pix.coordinate.y].value = 1000;

		for (Pixel pix: t)
			img[pix.coordinate.x][pix.coordinate.y].value = 0;
		for (i = 0; i < 10; i ++)
		{
			System.out.print("[");
			System.out.print(String.format("%5d", img[i][0].value));
			for (j = 1; j < 10; j++)
			{
				System.out.print(", " + String.format("%5d", img[i][j].value));
			}
			System.out.print("]\n");
		}
	}

}
