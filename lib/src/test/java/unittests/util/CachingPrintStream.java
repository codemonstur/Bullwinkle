package unittests.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public final class CachingPrintStream extends PrintStream {

    private final ByteArrayOutputStream out;

    public CachingPrintStream() {
        this(new ByteArrayOutputStream());
    }
    private CachingPrintStream(final ByteArrayOutputStream out) {
        super(out);
        this.out = out;
    }

    @Override
    public String toString() {
        return out.toString();
    }

}
