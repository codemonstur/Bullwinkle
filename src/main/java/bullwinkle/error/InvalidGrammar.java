package bullwinkle.error;

import static bullwinkle.Constants.ERROR_GRAMMAR;

public final class InvalidGrammar extends Exception implements HasCliExitCode {
    public InvalidGrammar(final String message)
    {
        super(message);
    }
    public InvalidGrammar(final Throwable t)
    {
        super(t);
    }

    public int getExitCode() {
        return ERROR_GRAMMAR;
    }
}
