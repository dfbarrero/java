public class Bicycle {
	// Fields ...
	public Bicycle(int startCadence, int startSpeed,
			int startGear) {
		gear = startGear;
		cadence = startCadence;
		speed = startSpeed;
	}
	public Bicycle() {
		gear = 1;
		cadence = 10;
		speed = 0;
	}
	 public static void main(String[] args) {
	 	Bicycle bike1 = new Bicycle();
	 	Bicycle bike2 = new Bicycle(0, 10, 10);
	 }
}
