import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class LedgerStore {
    private LedgerStore() {
    }

    public static void save(Ledger ledger, Path path) throws IOException {
        List<String> lines = new ArrayList<>();
        for (String name : ledger.userAccounts()) {
            lines.add("ACCOUNT," + name);
        }
        for (Transaction transaction : ledger.transactions()) {
            List<String> fields = new ArrayList<>();
            fields.add("TXN");
            fields.add(transaction.timestamp().toString());
            fields.add(transaction.description());
            for (Entry entry : transaction.entries()) {
                fields.add(entry.account());
                fields.add(entry.amount().toPlainString());
            }
            lines.add(String.join(",", fields));
        }
        Files.write(path, lines);
    }
}