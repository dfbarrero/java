public class Bicycle {
  private int cadence = 0;
  private int speed = 0;
  private int gear = 1;

  public void changeCadence(int newValue) {
    cadence = newValue; 
  }
  public void changeGear(int newValue) {
    gear = newValue; 
  }
  public void speedUp(int increment) {
    speed = speed + increment;   
  }
  public void applyBrakes(int decrement) {
    speed = speed - decrement;
  }
}
