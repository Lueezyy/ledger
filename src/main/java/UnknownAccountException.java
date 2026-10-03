public class UnknownAccountException extends LedgerException {
    public UnknownAccountException(String name) {
        super("No account named '" + name + "'.");
    }
}