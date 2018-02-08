public class Rectangle { 
	public int width = 0; 
	public int height = 0; 
	public Point origin; 
	public Rectangle() { 
		origin = new Point(0, 0); 
	} 
	public Rectangle(Point p) {
		origin = p; 
	} 
	public Rectangle(int w, int h) { 
		origin = new Point(0, 0); 
		width = w; 
		height = h; 
	} 
	public Rectangle(Point p, int w, int h) {
		origin = p; 
		width = w; 
		height = h; 
	} 
	public void move(int x, int y) { 
		origin.x = x; 
		origin.y = y; 
	} 
	public int getArea() { 
		return width * height; 
	} 
}
