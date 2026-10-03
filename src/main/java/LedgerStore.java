import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeParseException;
import java.time.LocalDateTime;
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
        Path temp = path.resolveSibling(path.getFileName() + ".tmp");
        try {
            Files.write(temp, lines);
            Files.move(
                temp, 
                path,
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            try {
                Files.deleteIfExists(temp);
            } catch (IOException ignored) {
            }
            throw new IOException(describe(e), e);
        }
    }

    public static Ledger load(Path path) throws IOException {
        List<String> lines;
        try {
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            throw new IOException(describe(e), e);
        }
        Ledger ledger = new Ledger();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }
            try {
                parseLine(ledger, line);
            } catch (IllegalArgumentException | LedgerException | DateTimeParseException e) {
                throw new IOException("Line " + (i + 1) + " of " + path + ": " + e.getMessage(), e);
            }
        }
        return ledger;
    }

    private static void parseLine(Ledger ledger, String line) {
        String[] fields = line.split(",", -1);
        switch (fields[0]) {
            case "ACCOUNT" -> {
                if (fields.length != 2) {
                    throw new IllegalArgumentException("ACCOUNT line needs exactly one name.");
                }
                ledger.createAccount(fields[1]);
            }
            case "TXN" -> {
                if (fields.length < 3 || (fields.length - 3) % 2 != 0) {
                    throw new IllegalArgumentException(
                            "TXN line needs a timestamp, a description, and account/amount pairs.");
                }
                LocalDateTime timestamp = LocalDateTime.parse(fields[1]);
                List<Entry> entries = new ArrayList<>();
                for (int j = 3; j < fields.length; j += 2) {
                    entries.add(new Entry(fields[j], parseAmount(fields[j + 1])));
                }
                ledger.post(new Transaction(timestamp, fields[2], entries));
            }
            default -> throw new IllegalArgumentException("Unknown record type '" + fields[0] + "'.");
        }
    }

    private static BigDecimal parseAmount(String text) {
        try {
            return new BigDecimal(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Bad amount '" + text + "'.");
        }
    }

    private static String describe(IOException e) {
        String type = e.getClass().getSimpleName();
        return e.getMessage() == null ? type : type + ": " + e.getMessage();
    }
}