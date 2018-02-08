public class UnaryDemo {
  public static void main(String[] args){
	  int a=1, b=2; 
	  int res; 
	  res = a+b; 
	  System.out.println("Res=" + res); 
	  res = a*b; 
	  res *= 2; 
	  System.out.println("Res=" + res); 
	  a++; 
	  System.out.println("a=" + a); 
	  System.out.println("a=" + a++); 
	  System.out.println("a=" + ++a); 
  }
}
