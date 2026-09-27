import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Ledger {
    private final Set<String> accounts = new LinkedHashSet<>();
    private final List<Transaction> transactions = new ArrayList<>();

    public void createAccount(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Account name can't be empty.");
        }
        if (accounts.contains(name)) {
            throw new IllegalArgumentException("An account named '" + name + "' already exists.");
        }
        accounts.add(name);
    }

    public boolean hasAccount(String name) {
        return accounts.contains(name);
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

    public Set<String> accounts() {
        return Collections.unmodifiableSet(accounts);
    }

    public List<Transaction> transactions() {
        return Collections.unmodifiableList(transactions);
    }
}