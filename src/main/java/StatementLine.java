import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StatementLine(
    LocalDateTime timestamp, 
    String description, 
    BigDecimal amount, 
    BigDecimal balance) {
}