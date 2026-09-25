import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HashMap<String, Double> accounts = new HashMap<>();
        boolean running = true;

        while (running) {
            System.out.println("1. Create");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Show");
            System.out.println("5. Quit");
            System.out.print("Pick an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();
            System.out.println("You picked " + choice);

            if (choice == 1) {
                System.out.print("Account name: ");
                String name = scanner.nextLine();
                accounts.put(name, 0.0);
                System.out.println("Account created.");
            }

            if (choice == 5) {
                System.out.println("Goodbye!");
                running = false;
            }
        }

        scanner.close();
    }
}