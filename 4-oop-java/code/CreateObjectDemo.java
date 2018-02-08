public static void main(String[] args) { 
	Point originOne = new Point(23, 94); 
	Rectangle rectOne = new Rectangle(originOne, 100, 200); 
	Rectangle rectTwo = new Rectangle(50, 100); 

	System.out.println("Width rectOne:"+rectOne.width); 
	System.out.println("Height rectOne:"+rectOne.height); 
	System.out.println("Area rectOne:"+rectOne.getArea()); 
	rectTwo.origin = originOne; 
	System.out.println("X rectTwo:"+rectTwo.origin.x); 
	System.out.println("Y rectTwo:"+rectTwo.origin.y); 

	rectTwo.move(40, 72); 
	System.out.println("X rectTwo:"+rectTwo.origin.x); 
	System.out.println("Y rectTwo:"+rectTwo.origin.y); 
} 
