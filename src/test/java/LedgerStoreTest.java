import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LedgerStoreTest {
    @TempDir
    Path tempDir;

    @Test
    void saveThenLoad_restoresAccountsAndHistory() throws IOException {
        Ledger original = new Ledger();
        original.createAccount("apple");
        original.createAccount("banana");
        original.deposit("apple", new BigDecimal("250.50"));
        original.withdraw("apple", new BigDecimal("75.25"));
        original.transfer("apple", "banana", new BigDecimal("30"));

        Path file = tempDir.resolve("ledger.csv");
        LedgerStore.save(original, file);
        Ledger loaded = LedgerStore.load(file);

        assertEquals(
            new ArrayList<>(original.userAccounts()),
            new ArrayList<>(loaded.userAccounts()));
        assertEquals(original.transactions(), loaded.transactions());
        for (String name : original.accounts()) {
            assertEquals(original.balanceOf(name), loaded.balanceOf(name));
        }
    }

    @Test
    void loadFileWithUnknownAccount_failsWithLineNumber() throws IOException {
        Path file = tempDir.resolve("bad.csv");
        Files.write(file, List.of(
            "ACCOUNT,apple",
            "TXN,2026-10-03T11:56,Bad,unknown10,External,-10"));

        IOException e = assertThrows(
            IOException.class, 
            () -> LedgerStore.load(file));
        assertTrue(e.getMessage().contains("Line 2"));
    }

    @Test
    void loadFileWithUnknownRecordType_failsWithLineNumber() throws IOException {
        Path file = tempDir.resolve("bad2.csv");
        Files.write(file, List.of("ACCOUNT,apple", "NONSENSE,1,2"));

        IOException e = assertThrows(
            IOException.class, 
            () -> LedgerStore.load(file));
        assertTrue(e.getMessage().contains("Line 2"));
    }
}