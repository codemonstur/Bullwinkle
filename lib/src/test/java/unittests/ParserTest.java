package unittests;

import static bullwinkle.BnfParserBuilder.newBnfParser;
import static org.junit.Assert.*;
import static unittests.util.Functions.assertSize;

import bullwinkle.error.InvalidRule;
import org.junit.Test;

import java.io.IOException;

public class ParserTest {

	@Test(expected = InvalidRule.class)
	public void simpleInvalidGrammar() {
		newBnfParser().addGrammar("A");
		fail("Invalid grammar 'A' should throw an exception when parsed.");
	}

	@Test
	public void simpleValidGrammar() {
		newBnfParser().addGrammar("<S> := a | b");
	}

	@Test
	public void simpleValidGrammarFromFile() throws IOException {
		try (final var in = ParserTest.class.getResourceAsStream("/grammars/tests/1.bnf")) {
			newBnfParser().addGrammar(in);
		}
	}

	@Test
	public void getAlternativesTest() {
		@SuppressWarnings("ConstantConditions")
		final var alternatives = newBnfParser()
			.addGrammar("<S> := <a> | b\n<a> := c")
			.build().getRule("<S>").getAlternatives();

		assertSize(alternatives, 2);
		assertEquals("<a>", alternatives.get(0).toString());
		assertEquals("b", alternatives.get(1).toString());
	}

}
