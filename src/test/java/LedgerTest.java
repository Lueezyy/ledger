import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    void transferToSameAccount_throwsSelfTransfer() {
        assertThrows(
            SelfTransferException.class,
            () -> ledger.transfer("apple", "apple", new BigDecimal("10")));
    }

    @Test
    void transferUnknownToItself_throwsUnknownAccountFirst() {
        assertThrows(
            UnknownAccountException.class,
            () -> ledger.transfer("unknown", "unknown", new BigDecimal("10")));
    }

    @Test
    void validTransfer_movesTheRightAmounts() {
        ledger.createAccount("banana");
        ledger.transfer("apple", "banana", new BigDecimal("30"));
        assertAmount("70", ledger.balanceOf("apple"));
        assertAmount("30", ledger.balanceOf("banana"));
    }

    @Test
    void netTotal_staysZeroAfterEveryOperation() {
        assertAmount("0", ledger.netTotal());

        ledger.createAccount("banana");
        assertAmount("0", ledger.netTotal());

        ledger.deposit("banana", new BigDecimal("50.25"));
        assertAmount("0", ledger.netTotal());

        ledger.withdraw("apple", new BigDecimal("20"));
        assertAmount("0", ledger.netTotal());

        ledger.transfer("apple", "banana", new BigDecimal("30"));
        assertAmount("0", ledger.netTotal());
    }

    private static void assertAmount(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual));
    }
}