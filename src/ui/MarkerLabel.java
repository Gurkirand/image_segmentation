package ui;
import javax.swing.JLabel;
import java.awt.Point;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Color;

public class MarkerLabel extends javax.swing.JLabel{
	public Point source;
	public ArrayList<Point> sinks;

	public MarkerLabel(String text){
		super(text);
		sinks = new ArrayList<>();
	}

	public void clear()
	{
		sinks.clear();
		source = null;
	}

	public void setSource(int x, int y)
	{
		source = new Point(x, y);
		repaint();
	}

	public void addSink(int x, int y)
	{
		sinks.add(new Point(x, y));
		repaint();
	}

	public void removeSource()
	{
		source = null;
		repaint();
	}

	public void removeSink(int x, int y)
	{
		sinks.remove(new Point(x, y));
		repaint();
	}

	public Point removeClosestSink(int x, int y)
	{
		if (sinks.isEmpty())
		{
			return null;
		}
		double dist = 1000,
		       _dist;
		Point sink = null;
		for (Point p: sinks)
		{
			_dist = p.distance(x, y);
			if (_dist < dist)
			{
				dist = _dist;
				sink = p;
			}
		}
		sinks.remove(sink);
		repaint();
		return sink;
	}

	@Override
	public void paint(Graphics g){
		if (source != null)
		{
			g.setColor(Color.red);
			g.fillOval(source.x - 5, source.y - 5, 10, 10);
		}
		g.setColor(Color.blue);
		for (Point p: sinks)
		{
			g.fillOval(p.x - 5, p.y - 5, 10, 10);
		}
	}
}
