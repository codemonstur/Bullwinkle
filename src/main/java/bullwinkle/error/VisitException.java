package bullwinkle.error;

import static bullwinkle.Constants.ERROR_PARSE;

public final class VisitException extends Exception implements HasCliExitCode {
    public VisitException(String message)
    {
        super(message);
    }
    public VisitException(Throwable t)
    {
        super(t);
    }

    public int getExitCode() {
        return ERROR_PARSE;
    }

}
