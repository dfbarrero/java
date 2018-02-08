import java.util.Scanner;

class Hello {
  public static void main (String [] args) {
    String name;
    int age; // int means an integer variable
   	Scanner input = new Scanner(System.in);

    System.out.println("Please, insert your name");
    name = input.next();
    System.out.println("Please, insert your age");
    age = input.nextInt();
    System.out.println("Hi, " + name);
    System.out.println("You are "+age+" years old");
  }
}
