import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Ledger {
    private final Set<String> accounts = new LinkedHashSet<>();
    private final List<Transaction> transactions = new ArrayList<>();

    public static final String EXTERNAL = "External";

    public Ledger() {
        accounts.add(EXTERNAL);
    }

    public void createAccount(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Account name can't be empty.");
        }
        if (name.equalsIgnoreCase(EXTERNAL)) {
            throw new IllegalArgumentException("'" + name + "' is a reserved account name.");
        }
        if (accounts.contains(name)) {
            throw new IllegalArgumentException("An account named '" + name + "' already exists.");
        }
        accounts.add(name);
    }

    public boolean hasAccount(String name) {
        return accounts.contains(name) && !name.equals(EXTERNAL);
    }

    public void post(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Can't post a null transaction.");
        }
        for (Entry entry : transaction.entries()) {
            if (!accounts.contains(entry.account())) {
                throw new IllegalArgumentException("No account named '" + entry.account() + "'.");
            }
        }
        transactions.add(transaction);
    }

    public void deposit(String name, BigDecimal amount) {
        requireUserAccount(name);
        requirePositive(amount);
        post(new Transaction(now(), "Deposit to " + name, List.of(
                new Entry(EXTERNAL, amount.negate()),
                new Entry(name, amount))));
    }

    public void withdraw(String name, BigDecimal amount) {
        requireUserAccount(name);
        requirePositive(amount);
        if (amount.compareTo(balanceOf(name)) > 0) {
            throw new IllegalArgumentException("Insufficient funds.");
        }
        post(new Transaction(now(), "Withdrawal from " + name, List.of(
                new Entry(name, amount.negate()),
                new Entry(EXTERNAL, amount))));
    }

    private void requireUserAccount(String name) {
        if (!hasAccount(name)) {
            throw new IllegalArgumentException("No account named '" + name + "'.");
        }
    }

    private void requirePositive(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
    }

    private static LocalDateTime now() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }

    public BigDecimal balanceOf(String name) {
        if (!accounts.contains(name)) {
            throw new IllegalArgumentException("No account named '" + name + "'.");
        }
        BigDecimal balance = BigDecimal.ZERO;
        for (Transaction transaction : transactions) {
            for (Entry entry : transaction.entries()) {
                if (entry.account().equals(name)) {
                    balance = balance.add(entry.amount());
                }
            }
        }
        return balance;
    }

    public List<StatementLine> statementFor(String name) {
        requireUserAccount(name);
        List<StatementLine> lines = new ArrayList<>();
        BigDecimal balance = BigDecimal.ZERO;
        for (Transaction transaction : transactions) {
            BigDecimal change = BigDecimal.ZERO;
            boolean touchesAccount = false;
            for (Entry entry : transaction.entries()) {
                if (entry.account().equals(name)) {
                    change = change.add(entry.amount());
                    touchesAccount = true;
                }
            }
            if (touchesAccount) {
                balance = balance.add(change);
                lines.add(new StatementLine(
                    transaction.timestamp(),
                    transaction.description(), 
                    change, 
                    balance));
            }
        }
        return Collections.unmodifiableList(lines);
    }

    public BigDecimal netTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (String name : accounts) {
            total = total.add(balanceOf(name));
        }
        return total;
    }

    public Set<String> accounts() {
        return Collections.unmodifiableSet(accounts);
    }

    public Set<String> userAccounts() {
        Set<String> result = new LinkedHashSet<>(accounts);
        result.remove(EXTERNAL);
        return Collections.unmodifiableSet(result);
    }

    public List<Transaction> transactions() {
        return Collections.unmodifiableList(transactions);
    }
}