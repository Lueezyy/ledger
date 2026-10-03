public class SelfTransferException extends LedgerException {
    public SelfTransferException(String name) {
        super("Can't transfer from '" + name + "' to itself.");
    }
}