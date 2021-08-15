package bullwinkle.error;

public final class InvalidRule extends RuntimeException {
    public InvalidRule(final String message)
    {
        super(message);
    }
    public InvalidRule(final int lineNumber, final String rule, final String message) {
        super("On line " + lineNumber + ", with rule '" + rule + "', encountered error: " + message);
    }

}