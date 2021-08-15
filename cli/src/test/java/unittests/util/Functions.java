package unittests.util;

import bullwinkle.BnfParserBuilder;

import java.io.IOException;

import static bullwinkle.BnfParserBuilder.newBnfParser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public enum Functions {;

    public static BnfParserBuilder newTestBnfParser(final String testBnf) throws IOException {
        try (final var in = Functions.class.getResourceAsStream("/grammars/tests/" + testBnf)) {
            return newBnfParser().addGrammar(in);
        }
    }

}
