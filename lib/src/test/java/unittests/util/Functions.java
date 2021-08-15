package unittests.util;

import bullwinkle.BnfParserBuilder;

import java.io.IOException;
import java.util.Collection;

import static bullwinkle.BnfParserBuilder.newBnfParser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public enum Functions {;

    public static BnfParserBuilder newTestBnfParser(final String testBnf) throws IOException {
        return newBnfParser().addResourceAsGrammar("/grammars/tests/" + testBnf);
    }

    public static <T> void assertSize(final Collection<T> collection, final int actualSize) {
        assertEquals("Collection has invalid size", collection.size(), actualSize);
    }

    public static <T> void assertContains(final Collection<T> collection, final T item) {
        assertTrue("Missing item " + item + " in collection", collection.contains(item));
    }

}
