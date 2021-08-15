package app.error;

import static java.lang.Integer.MAX_VALUE;

public interface HasCliExitCode {

    default int getExitCode() {
        return MAX_VALUE;
    }

}
