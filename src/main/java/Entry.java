import java.math.BigDecimal;

public record Entry(String account, BigDecimal amount) {
    public Entry {
        if (account == null || account.isBlank()) {
            throw new IllegalArgumentException("Entry needs an account name.");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Entry needs an amount.");
        }
        if (amount.signum() == 0) {
            throw new IllegalArgumentException("Entry amount can't be zero.");
        }
    }
}