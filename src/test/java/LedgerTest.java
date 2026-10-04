import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    @Test
    void rejectedOperations_changeNothing() {
        ledger.createAccount("banana");
        ledger.deposit("banana", new BigDecimal("40"));

        assertRejectedChangesNothing(
            InsufficientFundsException.class,
            () -> ledger.withdraw("apple", new BigDecimal("100.01")));
        assertRejectedChangesNothing(
            InvalidAmountException.class,
            () -> ledger.deposit("apple", BigDecimal.ZERO));
        assertRejectedChangesNothing(
            InvalidAmountException.class,
            () -> ledger.withdraw("apple", new BigDecimal("-5")));
        assertRejectedChangesNothing(
            UnknownAccountException.class,
            () -> ledger.deposit("ghost", new BigDecimal("10")));
        assertRejectedChangesNothing
        (SelfTransferException.class,
            () -> ledger.transfer("apple", "apple", new BigDecimal("10")));
        assertRejectedChangesNothing(
            InsufficientFundsException.class,
            () -> ledger.transfer("banana", "apple", new BigDecimal("40.01")));
        assertRejectedChangesNothing(
            UnknownAccountException.class,
            () -> ledger.transfer("apple", "unknown", new BigDecimal("10")));
        assertRejectedChangesNothing(
            InvalidAmountException.class,
            () -> ledger.transfer("apple", "banana", new BigDecimal("-1")));
    }

    private void assertRejectedChangesNothing(
        Class<? extends Throwable> expected, Executable action) {
        Map<String, BigDecimal> balancesBefore = balances();
        int countBefore = ledger.transactions().size();

        assertThrows(expected, action);

        assertEquals(balancesBefore, balances());
        assertEquals(countBefore, ledger.transactions().size());
    }

    private Map<String, BigDecimal> balances() {
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        for (String name : ledger.accounts()) {
            result.put(name, ledger.balanceOf(name));
        }
        return result;
    }

    @Test
    void statement_showsEachChangeWithRunningBalance() {
        ledger.createAccount("banana");
        ledger.withdraw("apple", new BigDecimal("30"));
        ledger.transfer("apple", "banana", new BigDecimal("20"));

        List<StatementLine> lines = ledger.statementFor("apple");

        assertEquals(3, lines.size());
        assertAmount("100", lines.get(0).amount());
        assertAmount("100", lines.get(0).balance());
        assertAmount("-30", lines.get(1).amount());
        assertAmount("70", lines.get(1).balance());
        assertAmount("-20", lines.get(2).amount());
        assertAmount("50", lines.get(2).balance());
    }
}