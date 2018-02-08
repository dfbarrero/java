public class CastDemo {
	public static void main(String[] args) {
		float a = 1.5; // Error
		float b = (float) 1.5; // Good

		int res;
		res = java.lang.Math.pow(2,2); // Error
		res = (int) java.lang.Math.pow(2,2); // Good
	}
}
