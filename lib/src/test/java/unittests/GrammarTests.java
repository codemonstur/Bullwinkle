package unittests;

import static bullwinkle.BnfParserBuilder.newBnfParser;
import static java.lang.String.format;
import static java.util.logging.Level.ALL;
import static org.junit.Assert.*;
import static unittests.util.Functions.newTestBnfParser;

import java.io.IOException;

import bullwinkle.error.InvalidRule;
import bullwinkle.error.MaximumRecursionReached;
import bullwinkle.error.ParsingFailed;
import bullwinkle.BnfParser;
import unittests.util.DummyLogger;
import org.junit.Test;

public class GrammarTests {

	@Test
	public void parseGrammar0() throws IOException {
		final var expression = "SELECT a FROM t";

		final int actualSize = newTestBnfParser("0.bnf")
			.startRule("<S>").build().parse(expression).getSize();

		assertEquals("Node tree size incorrect", 9, actualSize);
	}

	@Test
	public void testAddCaseValid() throws IOException {
		newTestBnfParser("0.bnf")
			.startRule("<S>")
			.addCaseToRule("<S>", "<foo>")
			.addRule("<foo> := bar")
			.build().parse("bar");
	}
	
	@Test(expected = ParsingFailed.class)
	public void testAddCaseInvalid() throws IOException {
		newTestBnfParser("10.bnf")
			.startRule("<S>")
			.addCaseToRule("<a>", "<foo>")
			.addRule("<a> := bar")
			.build().parse("bar");
	}
	
	@Test
	public void testAddRule() throws IOException {
		newTestBnfParser("10.bnf")
			.startRule("<S>")
			.addRule("<S> := bar")
			.build().parse("bar");
	}
	
	@Test(expected = InvalidRule.class)
	public void testInvalid1() throws IOException {
		newTestBnfParser("invalid-1.bnf").build();
	}
	
	@Test(expected = InvalidRule.class)
	public void testInvalid2() throws IOException {
		newTestBnfParser("invalid-2.bnf").build();
	}
	
	@Test(expected = InvalidRule.class)
	public void testInvalid3() throws IOException {
		newTestBnfParser("invalid-3.bnf").build();
	}
	
	@Test(expected = MaximumRecursionReached.class)
	public void testTooMuchRecursion() throws IOException {
		newTestBnfParser("15.bnf")
			.maxRecursionSteps(2)
			.build().parse("a a b");
	}

	@Test
	public void testCopy() throws IOException {
		new BnfParser(newTestBnfParser("0.bnf")
			.startRule("<S>")
			.addCaseToRule("<S>", "<foo>")
			.addRule("<foo> := bar")
			.build()).parse("bar");
	}

	// A rather dummy test, just to ensure we cover the lines in the toString() method
	@Test
	public void testToString() throws IOException {
		final int actualLength = newTestBnfParser("0.bnf")
			.startRule("<S>").build().toString().length();
		assertTrue("toString() doesn't return enough output", actualLength > 10);
	}
	
	@Test
	public void testInputStreamPreset() throws IOException {
		try (final var in = GrammarTests.class.getResourceAsStream("/grammars/tests/0.bnf")) {
			newBnfParser().addGrammar(in).build().parse("SELECT a FROM t");
		}
	}

	@Test(expected = InvalidRule.class)
	public void testInputStreamMissing() throws IOException {
		try (final var in = GrammarTests.class.getResourceAsStream("/no-such.bnf")) {
			newBnfParser().addGrammar(in).build();
		}
	}

	@Test(expected = ParsingFailed.class)
	public void parseGrammarError0() throws IOException {
		newTestBnfParser("0.bnf")
			.startRule("<S>").build().parse("SELECT");
	}

	@Test
	public void parseGrammar1() throws IOException {
		final var expression = "SELECT a FROM t";

		final int actualSize = newTestBnfParser("1.bnf")
			.startRule("<S>").build().parse(expression).getSize();

		assertEquals("Node tree size incorrect", 9, actualSize);
	}

	@Test
	public void parseGrammar2a() throws IOException {
		final var expression = "SELECT a FROM (t)";

		final int actualSize = newTestBnfParser("1.bnf")
			.startRule("<S>").build().parse(expression).getSize();

		assertEquals("Node tree size incorrect", 13, actualSize);
	}

	@Test
	public void parseGrammar2b() throws IOException {
		final var expression = "SELECT a FROM (SELECT b FROM t)";

		final int actualSize = newTestBnfParser("1.bnf")
			.startRule("<S>").build().parse(expression).getSize();

		assertEquals("Node tree size incorrect", 19, actualSize);
	}

	@Test
	public void parseGrammar3a() throws IOException {
		final var expression = "(a) & (a)";

		final int actualSize = newTestBnfParser("9.bnf")
			.startRule("<S>").build().parse(expression).getSize();

		assertEquals("Node tree size incorrect", 8, actualSize);
	}

	@Test
	public void parseGrammar3b() throws IOException {
		final var expression = "(a) & (a) & (a)";

		final int actualSize = newTestBnfParser("9.bnf")
			.startRule("<S>").build().parse(expression).getSize();

		assertEquals("Node tree size incorrect", 13, actualSize);
	}

	@Test
	public void parseGrammar4() throws IOException {
		final var expression = "a WHERE b";

		final int actualSize = newTestBnfParser("10.bnf")
			.startRule("<S>").build().parse(expression).getSize();

		assertEquals("Node tree size incorrect", 5, actualSize);
	}

	@Test(expected = ParsingFailed.class)
	public void parseGrammar5() throws IOException {
		newTestBnfParser("10.bnf").startRule("<S>").build().parse("a WHERE");
	}

	@Test(expected = ParsingFailed.class)
	public void parseGrammar6() throws IOException {
		newTestBnfParser("11.bnf").startRule("<eml_select>")
			.build().parse("SELECT 0 AS att FROM 0 AS");
	}

	@Test
	public void parseGrammar7() throws IOException {
		newTestBnfParser("11.bnf").startRule("<processor>")
			.build().parse("(THE TUPLES OF FILE \"a\") WHERE (a) = (0)");
	}

	@Test
	public void parseGrammar8a() throws IOException {
		newBnfParser().stickyRules(true)
			.addResourceAsGrammar("/grammars/tests/14.bnf")
			.startRule("<processor>").build().parse("0 FOO");
	}

	@Test
	public void parseGrammar8b() throws IOException {
		newBnfParser().stickyRules(true)
			.addResourceAsGrammar("/grammars/tests/14.bnf")
			.startRule("<processor>").build().parse("(0) FOO");
	}

	@Test
	public void parseNumber1() throws IOException {
		newTestBnfParser("12.bnf").startRule("<processor>").build().parse("3.5");
	}

	@Test
	public void parseNumber2() throws IOException {
		newTestBnfParser("12.bnf").startRule("<processor>").build().parse("3");
	}

	@Test
	public void parseGrammarLtlFo1() throws IOException {
		final var expression = "G (∃ x ∈ /a/b/c : (x lt y))";

		final int actualSize = newTestBnfParser("2.bnf")
			.startRule("<phi>").build().parse(expression).getSize();

		assertEquals("Node tree size incorrect", 23, actualSize);
	}

	@Test
	public void parseGrammarWithEpsilon1() throws IOException {
		final var expression = "hello hello";

		final int actualSize = newTestBnfParser("3.bnf")
			.startRule("<S>").build().parse(expression).getSize();

		assertEquals("Node tree size incorrect", 6, actualSize);
	}

	@Test
	public void parseGrammarWithEpsilon2() throws IOException {
		final var expression = "hello hello foo";

		final int actualSize = newTestBnfParser("4.bnf")
			.startRule("<S>").build().parse(expression).getSize();

		assertEquals("Node tree size incorrect", 9, actualSize);
	}

	@Test
	public void parseGrammarWithEpsilon3() throws IOException {
		final var expression = "[ ]";

		final int actualSize = newTestBnfParser("7.bnf")
			.startRule("<S>").build().parse(expression).getSize();

		assertEquals("Node tree size incorrect", 5, actualSize);
	}

	@Test
	public void parseGrammarWithEpsilon4() throws IOException {
		final var expression = "[ ]";

		final int actualSize = newTestBnfParser("8.bnf")
			.startRule("<S>").build().parse(expression).getSize();

		assertEquals("Node tree size incorrect", 6, actualSize);
	}

	@Test
	public void parseGrammarWithEntity1() throws IOException {
		final var expression = "a|a";

		final int actualSize = newTestBnfParser("5.bnf")
			.startRule("<S>").build().parse(expression).getSize();

		assertEquals("Node tree size incorrect", 2, actualSize);
	}

	@Test
	public void parseGrammarWithCaptureBlock() throws IOException {
		final var expression = "A tomato is a type of fruit";

		final int actualSize = newTestBnfParser("6.bnf")
			.startRule("<S>").build().parse(expression).getSize();

		assertEquals("Node tree size incorrect", 4, actualSize);
	}

	@Test
	public void parseGrammarPartial0() throws IOException {
		final var expression = "SELECT <criterion> FROM t";

		final int actualSize = newTestBnfParser("0.bnf")
			.startRule("<S>").partialParsing(true)
			.build().parse(expression).getSize();

		assertEquals("Node tree size incorrect", 8, actualSize);
	}

	@Test(expected = ParsingFailed.class)
	public void parseGrammarPartial1() throws IOException {
		// parseItNot did not set partialParsing.. wtf
		newTestBnfParser("0.bnf")
			.partialParsing(true).startRule("<S>")
			.build().parse("SELECT <foo> FROM t");
	}

	@Test
	public void parseGrammarPartial2() throws IOException {
		newTestBnfParser("13.bnf")
			.startRule("<S>").partialParsing(true)
			.build().parse("<A> <B> c");
	}

	@Test
	public void parseGrammarPartial3() throws IOException {
		newTestBnfParser("13.bnf")
			.startRule("<S>").partialParsing(true)
			.build().parse("foo <B> c");
	}

	@Test
	public void parseGrammarPartial4() throws IOException {
		newTestBnfParser("13.bnf")
			.startRule("<S>").partialParsing(true)
			.build().parse("foo <Z> d c");
	}

	@Test(expected = ParsingFailed.class)
	public void parseGrammarPartial4b() throws IOException {
		// parseItNot did not set partialParsing.. wtf
		newTestBnfParser("13.bnf").partialParsing(true)
			.startRule("<S>").build().parse("foo <Y> d c");
	}

	@Test(expected = ParsingFailed.class)
	public void parseGrammarPartial4c() throws IOException {
		// parseItNot did not set partialParsing.. wtf
		newTestBnfParser("13.bnf").partialParsing(true)
			.startRule("<S>").build().parse("foo <B> d c");
	}

	@Test
	public void parseGrammarPartial5() throws IOException {
		newTestBnfParser("13.bnf").partialParsing(true)
			.startRule("<S>").build().parse("foo 0 d c");
	}
	
	@Test
	public void parseGrammarDebug() throws IOException {
		final var logger = new DummyLogger();
		try {
			newTestBnfParser("10.bnf").startRule("<S>")
				.logger(logger).logLevel(ALL)
				.build().parse("a WHERE");
		} catch (ParsingFailed e) {
			assertTrue(logger.hasLogged());
		}
	}

}
