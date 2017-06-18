package image;
import java.awt.Point;

public class Pixel
{
	int value;
	Point coordinate;
	
	public Pixel(int value, Point coordinate) {
		this.value = value;
		this.coordinate = coordinate;
	}
	
	public Pixel(int value, int x, int y) {
		this.value = value;
		this.coordinate.setLocation(x, y);
	}
	
	public Pixel() {this(0, 0, 0);}
	
	//method to compute edge weight by finding difference(or similarity) b/w two pixels
	public Double weightDiff(Pixel other){
		int diff = this.value - other.value;
		diff = Math.abs(diff);
		
		Double result = 255./diff; 
		
		return result;
	}
}
