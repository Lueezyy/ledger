import static org.junit.jupiter.api.Assertions.assertThrows;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LedgerTest {
    private Ledger ledger;

    @BeforeEach
    void setUp() {
        ledger = new Ledger();
        ledger.createAccount("apple");
        ledger.deposit("apple", new BigDecimal("100.00"));
    }

    @Test
    void withdrawMoreThanBalance_throwsInsufficientFunds() {
        assertThrows(
            InsufficientFundsException.class,
            () -> ledger.withdraw("apple", new BigDecimal("100.01")));
    }

    @Test
    void withdrawFromEmptyAccount_throwsInsufficientFunds() {
        ledger.createAccount("banana");
        assertThrows(
            InsufficientFundsException.class,
            () -> ledger.withdraw("banana", new BigDecimal("0.01")));
    }

    @Test
    void depositZero_throwsInvalidAmount() {
        assertThrows(
            InvalidAmountException.class,
            () -> ledger.deposit("apple", BigDecimal.ZERO));
    }

    @Test
    void withdrawZero_throwsInvalidAmount() {
        assertThrows(
            InvalidAmountException.class,
            () -> ledger.withdraw("apple", BigDecimal.ZERO));
    }

    @Test
    void depositNegative_throwsInvalidAmount() {
        assertThrows(
            InvalidAmountException.class,
            () -> ledger.deposit("apple", new BigDecimal("-5")));
    }

    @Test
    void withdrawNegative_throwsInvalidAmount() {
        assertThrows(
            InvalidAmountException.class,
            () -> ledger.withdraw("apple", new BigDecimal("-5")));
    }

    @Test
    void depositToUnknownAccount_throwsUnknownAccount() {
        assertThrows(
            UnknownAccountException.class,
            () -> ledger.deposit("unknown", new BigDecimal("10")));
    }

    @Test
    void withdrawFromUnknownAccount_throwsUnknownAccount() {
        assertThrows(
            UnknownAccountException.class,
            () -> ledger.withdraw("unknown", new BigDecimal("10")));
    }

    @Test
    void balanceOfUnknownAccount_throwsUnknownAccount() {
        assertThrows(
            UnknownAccountException.class,
            () -> ledger.balanceOf("unknown"));
    }
}