package bullwinkle.error;

import static bullwinkle.Constants.ERROR_PARSE;

public final class ParseException extends Exception implements HasCliExitCode {
    public ParseException(final String message)
    {
        super(message);
    }

    public int getExitCode() {
        return ERROR_PARSE;
    }

}
