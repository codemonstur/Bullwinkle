package bullwinkle.error;

public final class InvalidRule extends Exception {
    public InvalidRule(final String message)
    {
        super(message);
    }
}