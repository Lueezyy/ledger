import java.util.List;
import java.util.Scanner;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
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
        Path dataFile = Path.of(args.length > 0 ? args[0] : "ledger.csv");
        Ledger ledger;
        if (Files.exists(dataFile)) {
            try {
                ledger = LedgerStore.load(dataFile);
            } catch (IOException e) {
                System.out.println("Couldn't load " + dataFile + ": " + e.getMessage());
                return;
            }
        } else {
            ledger = new Ledger();
        }
        boolean running = true;

        while (running) {
            System.out.println("1. Create");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Show");
            System.out.println("5. History");
            System.out.println("6. View statement");
            System.out.println("7. Quit");
            System.out.print("Pick an option: ");

            String input = scanner.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number from 1 to 7.");
                continue;
            }
            if (choice < 1 || choice > 7) {
                System.out.println("Please enter a number from 1 to 7.");
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
                if (ledger.transactions().isEmpty()) {
                    System.out.println("No transactions yet.");
                }
                int number = 1;
                for (Transaction transaction : ledger.transactions()) {
                    System.out.printf("%d. %s%n", number, transaction.description());
                    for (Entry entry : transaction.entries()) {
                        System.out.printf("     %s: %+.2f%n", entry.account(), entry.amount());
                    }
                    number++;
                }
                System.out.printf("Net total of all accounts: $%.2f%n", ledger.netTotal());
            }

            if (choice == 6) {
                System.out.print("Account name: ");
                String name = scanner.nextLine().trim();
                if (!ledger.hasAccount(name)) {
                    System.out.println("No account named '" + name + "'.");
                    continue;
                }
                List<StatementLine> lines = ledger.statementFor(name);
                if (lines.isEmpty()) {
                    System.out.println("No transactions yet.");
                    continue;
                }
                System.out.println("Statement for " + name + ":");
                for (StatementLine line : lines) {
                    System.out.printf(
                        "%s  %-28s %+10.2f  $%.2f%n",
                        line.timestamp().format(DATE_FORMAT),
                        line.description(), 
                        line.amount(), 
                        line.balance());
                }
            }

            if (choice == 7) {
                System.out.println("Goodbye!");
                running = false;
            }
        }

        scanner.close();
    }
}