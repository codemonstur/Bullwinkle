package app.error;

import static app.Constants.ERROR_UNKNOWN_FORMAT;

public final class UnknownOutputFormat extends Exception implements HasCliExitCode {

    public UnknownOutputFormat(final String selectedFormat) {
        super("The selected format '" + selectedFormat + "' was not recognised");
    }

    public int getExitCode() {
        return ERROR_UNKNOWN_FORMAT;
    }

}
