import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record Transaction(LocalDateTime timestamp, String description, List<Entry> entries) {
    public Transaction {
        if (timestamp == null) {
            throw new IllegalArgumentException("Transaction needs a timestamp.");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Transaction needs a description.");
        }
        if (entries == null) {
            throw new IllegalArgumentException("Transaction needs entries.");
        }
        entries = List.copyOf(entries);
        if (entries.size() < 2) {
            throw new IllegalArgumentException("Transaction needs at least 2 entries.");
        }
        BigDecimal sum = BigDecimal.ZERO;
        for (Entry entry : entries) {
            sum = sum.add(entry.amount());
        }
        if (sum.compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalArgumentException("Entries must sum to zero, but sum to " + sum + ".");
        }
    }
}