import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("1. Create");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Show");
            System.out.println("5. Quit");
            System.out.print("Pick an option: ");

            int choice = scanner.nextInt();
            System.out.println("You picked " + choice);

            if (choice == 5) {
                System.out.println("Goodbye!");
                running = false;
            }
        }

        scanner.close();
    }
}