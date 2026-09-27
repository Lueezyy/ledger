import java.util.Scanner;
import java.math.BigDecimal;

public class Main {
    private static BigDecimal readAmount(Scanner scanner) {
        System.out.print("Amount: ");
        String input = scanner.nextLine().trim();
        BigDecimal amount;
        try {
            amount = new BigDecimal(input);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid amount.");
            return null;
        }
        if (amount.signum() <= 0) {
            System.out.println("Amount must be a number greater than zero.");
            return null;
        }
        if (amount.stripTrailingZeros().scale() > 2) {
            System.out.println("Amount can't have more than 2 decimal places.");
            return null;
        }
        return amount;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Ledger ledger = new Ledger();
        boolean running = true;

        while (running) {
            System.out.println("1. Create");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Show");
            System.out.println("5. Quit");
            System.out.print("Pick an option: ");

            String input = scanner.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number from 1 to 5.");
                continue;
            }
            if (choice < 1 || choice > 5) {
                System.out.println("Please enter a number from 1 to 5.");
                continue;
            }

            if (choice == 1) {
                System.out.print("Account name: ");
                String name = scanner.nextLine().trim();
                try {
                    ledger.createAccount(name);
                    System.out.println("Account created.");
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }

            if (choice == 2) {
                System.out.print("Account name: ");
                String name = scanner.nextLine().trim();
                if (!ledger.hasAccount(name)) {
                    System.out.println("No account named '" + name + "'.");
                    continue;
                }
                BigDecimal amount = readAmount(scanner);
                if (amount == null) {
                    continue;
                }
                ledger.deposit(name, amount);
                System.out.println("Deposit done.");
            }

            if (choice == 3) {
                System.out.print("Account name: ");
                String name = scanner.nextLine().trim();
                if (!ledger.hasAccount(name)) {
                    System.out.println("No account named '" + name + "'.");
                    continue;
                }
                BigDecimal amount = readAmount(scanner);
                if (amount == null) {
                    continue;
                }
                BigDecimal balance = ledger.balanceOf(name);
                if (amount.compareTo(balance) > 0) {
                    System.out.printf("Insufficient funds. Balance is $%.2f.%n", balance);
                    continue;
                }
                ledger.withdraw(name, amount);
                System.out.println("Withdrawal done.");
            }

            if (choice == 4) {
                for (String name : ledger.userAccounts()) {
                    System.out.printf("%s: $%.2f%n", name, ledger.balanceOf(name));
                }
            }

            if (choice == 5) {
                System.out.println("Goodbye!");
                running = false;
            }
        }

        scanner.close();
    }
}