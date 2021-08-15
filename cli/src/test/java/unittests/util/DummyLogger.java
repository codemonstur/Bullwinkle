package unittests.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class DummyLogger extends Logger {
    private boolean hasLogged = false;

    public DummyLogger()
    {
        super("unittests.GrammarTests.DummyLogger", null);
    }

    @Override
    public void log(final Level level, final String message)
    {
        hasLogged = true;
    }

    @Override
    public void log(final Level level, final String message, final Object param1)
    {
        hasLogged = true;
    }

    public boolean hasLogged()
    {
        return hasLogged;
    }
}
