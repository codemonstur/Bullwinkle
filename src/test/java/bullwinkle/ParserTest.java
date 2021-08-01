package bullwinkle;

import static org.junit.Assert.*;

import bullwinkle.error.InvalidGrammar;
import bullwinkle.parsing.BnfParser;
import org.junit.Test;

public class ParserTest {

	@Test(expected = InvalidGrammar.class)
	public void simpleInvalidGrammar() throws InvalidGrammar {
		new BnfParser().setGrammar("A");
		fail("Invalid grammar 'A' should throw an exception when parsed.");
	}

	@Test
	public void simpleValidGrammar() {
		try {
			new BnfParser().setGrammar("<S> := a | b;");
		} catch (InvalidGrammar e) {
			fail("Valid grammar has thrown an exception when parsed.");
		}
	}

	@Test
	public void simpleValidGrammarFromFile() {
		try {
			new BnfParser(ParserTest.class.getResourceAsStream("/grammars/tests/grammar-1.bnf"));
		} catch (InvalidGrammar e) {
			e.printStackTrace();
			fail("Valid grammar has thrown an exception when parsed.");
		}
	}

	@Test
	public void getAlternativesTest() {
		try {
			final var parser = new BnfParser();
			parser.setGrammar("<S> := <a> | b\n<a> := c");

			final var alternatives = parser.getAlternatives("<S>");
			assertEquals(0, alternatives.get(0).compareTo("<a>"));
			assertEquals(0, alternatives.get(1).compareTo("b"));
		} catch (InvalidGrammar e) {
			fail("Valid grammar has thrown an exception when parsed.");
		}
	}

}
