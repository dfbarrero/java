public class Bicycle {
	public int gear;
	private int speed;

	public Bicycle(int gear, int speed) {
		this.gear = gear;
		this.speed = speed;
	}

	public void speedUp() { speed = speed + 5; }

	private void speedDown() { speed = speed - 5; }

	public static void main(String [] args) {
		Bycicle bike = new Bicycle(5, 10);
		bike.gear = 2;
		bike.speed = 5; // Error
		bike.speedUp();
		bike.speedDown(); // Error
	}
}
