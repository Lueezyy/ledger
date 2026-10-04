import static org.junit.jupiter.api.Assertions.assertThrows;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class TransactionTest {

    @Test
    void unbalancedTransaction_isRejected() {
        List<Entry> entries = List.of(
            new Entry("apple", new BigDecimal("10")),
            new Entry("banana", new BigDecimal("-5")));
        assertThrows(
            IllegalArgumentException.class,
            () -> new Transaction(LocalDateTime.now(), "Bad", entries));
    }

    @Test
    void zeroEntry_isRejected() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new Entry("apple", BigDecimal.ZERO));
    }
}