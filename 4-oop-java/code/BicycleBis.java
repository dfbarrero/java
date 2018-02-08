public class Bicycle {
	private int gear;
	private int speed;
	private int cadence;
	public Bicycle(int startCadence, int startSpeed,
			int startGear) { 
		gear = startGear; 
		cadence = startCadence; 
		speed = startSpeed; 
	} 
	public void setCadence(int newValue) { 
		cadence = newValue;
	} 
	public void setGear(int newValue) { 
		gear = newValue; 
	} 
	public void applyBrake(int decrement) { 
		speed -= decrement; 
	} 
	public void speedUp(int increment) { 
		speed += increment; 
	} 
}
