package bullwinkle.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;
import java.util.function.Supplier;

import static bullwinkle.Constants.TWO_SPACES;
import static java.lang.Math.max;

public enum Functions {;

    public static boolean isNullOrEmpty(final String value) {
        return value == null || value.isEmpty();
    }

    public static <V, T extends Throwable> V orThrow(final V value, final Supplier<T> exception) throws T {
        if (value == null) throw exception.get();
        return value;
    }

    public static <T> T orDefault(final T value, final T defaultValue) {
        return value != null ? value : defaultValue;
    }

    /**
     * Interprets UTF-8 escaped characters and converts them back into
     * a UTF-8 string. The solution used here (going through a
     * <tt>Property</tt> object) can be found on
     * <a href="http://stackoverflow.com/a/24046962">StackOverflow</a>.
     * It has the advantage of not relying on (yet another) external
     * library (as the accepted solution does) just for using a single
     * method.
     * @param string The input string
     * @return The converted (unescaped) string
     */
    public static String unescapeString(final String string) {
        // We want only the unicode characters to be resolved;
        // double all other backslashes
        final var newString = string.replaceAll("\\\\([^u])", "\\\\\\\\$1");
        final var properties = new Properties();
        try {
            properties.load(new StringReader("key=" + newString));
        } catch (IOException ignored) {}

        return properties.getProperty("key");
    }

    public static String indent(final int indent, final String message) {
        return TWO_SPACES.repeat(max(0, indent)) + message;
    }

}
