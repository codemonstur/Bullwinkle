package bullwinkle;

import static org.junit.Assert.*;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import bullwinkle.error.InvalidGrammar;
import bullwinkle.error.InvalidRule;
import bullwinkle.error.ParseException;
import bullwinkle.model.BnfRule;
import bullwinkle.model.nodes.Node;
import bullwinkle.parsing.BnfParser;
import org.junit.Test;

public class GrammarTests {

	@Test
	public void parseGrammar0() {
		String expression = "SELECT a FROM t";
		Node node = parseIt("/grammars/tests/grammar-0.bnf", "<S>", expression, false, false);
		checkParseTreeSize(expression, 9, node.getSize());
	}

	@Test
	public void testAddCaseValid() throws ParseException, InvalidRule {
		String expression = "bar";
		BnfParser parser = readGrammar("/grammars/tests/grammar-0.bnf", "<S>", false);
		parser.addCaseToRule("<S>", "<foo>");
		parser.addRule(BnfRule.parseRule("<foo> := bar"));
		Node node = parser.parse(expression);
		assertNotNull(node);
	}
	
	@Test
	public void testAddCaseInvalid() throws ParseException, InvalidRule {
		String expression = "bar";
		BnfParser parser = readGrammar("/grammars/tests/grammar-10.bnf", "<S>", false);
		parser.addCaseToRule("<a>", "<foo>");
		parser.addRule(BnfRule.parseRule("<a> := bar"));
		Node node = parser.parse(expression);
		assertNull(node);
	}
	
	@Test
	public void testAddRule() throws ParseException, InvalidRule {
		BnfParser parser = readGrammar("/grammars/tests/grammar-10.bnf", "<S>", false);
		parser.addRule(0, BnfRule.parseRule("<S> := bar"));
		Node node = parser.parse("bar");
		assertNotNull(node);
	}
	
	@Test(expected= InvalidGrammar.class)
	public void testNulLRules() throws InvalidGrammar {
		BnfParser.getRules((String)null);
	}
	
	@Test(expected= InvalidGrammar.class)
	public void testInvalid1() throws InvalidGrammar {
		new BnfParser(GrammarTests.class.getResourceAsStream("/grammars/tests/grammar-invalid-1.bnf"));
	}
	
	@Test(expected= InvalidGrammar.class)
	public void testInvalid2() throws InvalidGrammar {
		new BnfParser(GrammarTests.class.getResourceAsStream("/grammars/tests/grammar-invalid-2.bnf"));
	}
	
	@Test(expected= InvalidGrammar.class)
	public void testInvalid3() throws InvalidGrammar {
		new BnfParser(GrammarTests.class.getResourceAsStream("/grammars/tests/grammar-invalid-3.bnf"));
	}
	
	@Test(expected=ParseException.class)
	public void testTooMuchRecursion() throws ParseException, InvalidGrammar {
		BnfParser parser = new BnfParser(GrammarTests.class.getResourceAsStream("/grammars/tests/grammar-15.bnf"));
		parser.setMaxRecursionSteps(2);
		parser.parse("a a b");
	}

	@Test
	public void testCopy() throws ParseException, InvalidRule {
		BnfParser parser = readGrammar("/grammars/tests/grammar-0.bnf", "<S>", false);
		parser.addCaseToRule("<S>", "<foo>");
		parser.addRule(BnfRule.parseRule("<foo> := bar"));
		BnfParser parser2 = new BnfParser(parser);
		Node node = parser2.parse("bar");
		assertNotNull(node);
	}

	// A rather dummy test, just to ensure we cover the lines in the toString() method
	@Test
	public void testToString() {
		BnfParser parser = readGrammar("/grammars/tests/grammar-0.bnf", "<S>", true);
		String s = parser.toString();
		assertNotNull(s);
		assertTrue(s.length() > 10);
	}
	
	@Test
	public void testInputStream() throws InvalidGrammar, ParseException {
		BnfParser parser = new BnfParser(GrammarTests.class.getResourceAsStream("/grammars/tests/grammar-0.bnf"));
		Node node = parser.parse("SELECT a FROM t");
		assertNotNull(node);
	}

	@Test
	public void parseGrammarError0() {
		parseItNot("/grammars/tests/grammar-0.bnf", "<S>", "SELECT", false, false);
	}

	@Test
	public void parseGrammar1() {
		String expression = "SELECT a FROM t";
		Node node = parseIt("/grammars/tests/grammar-1.bnf", "<S>", expression, false, false);
		checkParseTreeSize(expression, 9, node.getSize());
	}

	@Test
	public void parseGrammar2a() {
		String expression = "SELECT a FROM (t)";
		Node node = parseIt("/grammars/tests/grammar-1.bnf", "<S>", expression, false, false);
		checkParseTreeSize(expression, 13, node.getSize());
	}

	@Test
	public void parseGrammar2b() {
		String expression = "SELECT a FROM (SELECT b FROM t)";
		Node node = parseIt("/grammars/tests/grammar-1.bnf", "<S>", expression, false, false);
		checkParseTreeSize(expression, 19, node.getSize());
	}

	@Test
	public void parseGrammar3a() {
		String expression = "(a) & (a)";
		Node node = parseIt("/grammars/tests/grammar-9.bnf", "<S>", expression, false, false);
		checkParseTreeSize(expression, 8, node.getSize());
	}

	@Test
	public void parseGrammar3b() {
		String expression = "(a) & (a) & (a)";
		Node node = parseIt("/grammars/tests/grammar-9.bnf", "<S>", expression, false, false);
		checkParseTreeSize(expression, 13, node.getSize());
	}

	@Test
	public void parseGrammar4()
	{
		String expression = "a WHERE b";
		Node node = parseIt("/grammars/tests/grammar-10.bnf", "<S>", expression, false, false);
		checkParseTreeSize(expression, 5, node.getSize());
	}

	@Test
	public void parseGrammar5() {
		parseItNot("/grammars/tests/grammar-10.bnf", "<S>", "a WHERE", false, false);
	}

	@Test
	public void parseGrammar6() {
		String expression = "SELECT 0 AS att FROM 0 AS";
		parseItNot("/grammars/tests/grammar-11.bnf", "<eml_select>", expression, false, false);
	}

	@Test
	public void parseGrammar7() {
		String expression = "(THE TUPLES OF FILE \"a\") WHERE (a) = (0)";
		parseIt("/grammars/tests/grammar-11.bnf", "<processor>", expression, false, false);
	}

	@Test
	public void parseGrammar8a() {
		String expression = "0 FOO";
		parseIt("/grammars/tests/grammar-14.bnf", "<processor>", expression, false, false);
	}

	@Test
	public void parseGrammar8b() {
		String expression = "(0) FOO";
		parseIt("/grammars/tests/grammar-14.bnf", "<processor>", expression, false, false);
	}

	@Test
	public void parseNumber1() {
		String expression = "3.5";
		parseIt("/grammars/tests/grammar-12.bnf", "<processor>", expression, false, false);
	}

	@Test
	public void parseNumber2() {
		String expression = "3";
		parseIt("/grammars/tests/grammar-12.bnf", "<processor>", expression, false, false);
	}

	@Test
	public void parseGrammarLtlFo1() {
		String expression = "G (∃ x ∈ /a/b/c : (x lt y))";
		Node node = parseIt("/grammars/tests/grammar-2.bnf", "<phi>", expression, false, false);
		checkParseTreeSize(expression, 23, node.getSize());
	}

	@Test
	public void parseGrammarWithEpsilon1() {
		String expression = "hello hello";
		Node node = parseIt("/grammars/tests/grammar-3.bnf", "<S>", expression, false, false);
		checkParseTreeSize(expression, 6, node.getSize());  
	}

	@Test
	public void parseGrammarWithEpsilon2() {
		String expression = "hello hello foo";
		Node node = parseIt("/grammars/tests/grammar-4.bnf", "<S>", expression, false, false);
		checkParseTreeSize(expression, 9, node.getSize());     
	}

	@Test
	public void parseGrammarWithEpsilon3() {
		String expression = "[ ]";
		Node node = parseIt("/grammars/tests/grammar-7.bnf", "<S>", expression, false, false);
		checkParseTreeSize(expression, 5, node.getSize());    
	}

	@Test
	public void parseGrammarWithEpsilon4() {
		String expression = "[ ]";
		Node node = parseIt("/grammars/tests/grammar-8.bnf", "<S>", expression, false, false);
		checkParseTreeSize(expression, 6, node.getSize());   
	}

	@Test
	public void parseGrammarWithEntity1() {
		String expression = "a|a";
		Node node = parseIt("/grammars/tests/grammar-5.bnf", "<S>", expression, false, false);
		checkParseTreeSize(expression, 2, node.getSize());    
	}

	@Test
	public void parseGrammarWithCaptureBlock() {
		String expression = "A tomato is a type of fruit";
		Node node = parseIt("/grammars/tests/grammar-6.bnf", "<S>", expression, false, false);
		checkParseTreeSize(expression, 4, node.getSize());   
	}

	@Test
	public void parseGrammarPartial0() {
		String expression = "SELECT <criterion> FROM t";
		Node node = parseIt("/grammars/tests/grammar-0.bnf", "<S>", expression, false, true);
		checkParseTreeSize(expression, 8, node.getSize());
	}

	@Test
	public void parseGrammarPartial1() {
		String expression = "SELECT <foo> FROM t";
		parseItNot("/grammars/tests/grammar-0.bnf", "<S>", expression, false, true);
	}

	@Test
	public void parseGrammarPartial2() {
		String expression = "<A> <B> c";
		parseIt("/grammars/tests/grammar-13.bnf", "<S>", expression, false, true);
	}

	@Test
	public void parseGrammarPartial3() {
		String expression = "foo <B> c";
		parseIt("/grammars/tests/grammar-13.bnf", "<S>", expression, false, true);
	}

	@Test
	public void parseGrammarPartial4() {
		String expression = "foo <Z> d c";
		parseIt("/grammars/tests/grammar-13.bnf", "<S>", expression, false, true);
	}

	@Test
	public void parseGrammarPartial4b() {
		String expression = "foo <Y> d c";
		parseItNot("/grammars/tests/grammar-13.bnf", "<S>", expression, false, true);
	}

	@Test
	public void parseGrammarPartial4c() {
		String expression = "foo <B> d c";
		parseItNot("/grammars/tests/grammar-13.bnf", "<S>", expression, false, true);
	}

	@Test
	public void parseGrammarPartial5() {
		String expression = "foo 0 d c";
		parseIt("/grammars/tests/grammar-13.bnf", "<S>", expression, false, true);
	}
	
	@Test
	public void parseGrammarDebug() throws ParseException {
		String expression = "a WHERE";
		BnfParser parser = readGrammar("/grammars/tests/grammar-10.bnf", "<S>", true);
		DummyLogger dl = new DummyLogger();
		parser.setDebugMode(true, dl);
		parser.parse(expression);
		assertTrue(dl.hasLogged());
	}

	private static void checkParseTreeSize(final String expression, final int expected, final int size) {
		if (size != expected)
		{
			fail("Incorrect parsing of expression '" + expression + "': expected a parse tree of size " + expected + ", got " + size);
		}    
	}

	private static Node parseIt(final String grammarFilename, final String startSymbol,
								final String expression, final boolean debugMode, final boolean partialParsing) {
		BnfParser parser = readGrammar(grammarFilename, startSymbol, debugMode);
		parser.setPartialParsing(partialParsing);
		return shouldParseAndNotNull(expression, parser);
	}

	private static void parseItNot(final String grammarFilename, final String startSymbol,
								   final String expression, final boolean debugMode, boolean partialParsing) {
		BnfParser parser = readGrammar(grammarFilename, startSymbol, debugMode);
		shouldNotParse(expression, parser);
	}

	private static Node shouldParseAndNotNull(final String expression, final BnfParser parser) {
		final Node node = shouldParse(expression, parser);
		if (node == null) fail("Parsing '" + expression + "' returned null; a non-null result was expected");
		return node;
	}

	/**
	 * Attempts to parse an expression with the given parser, and
	 * expects the parsing not to raise any exception.
	 * @param expression The expression to parse
	 * @param parser The parser to use
	 * @return The parse node if any, null if expression could not parse
	 */
	private static Node shouldParse(final String expression, final BnfParser parser) {
		try {
			return parser.parse(expression);
		} catch (ParseException e) {
			fail("Parsing '" + expression + "' threw exception " + e);
			return null;
		}
	}

	/**
	 * Attempts to parse an expression with the given parser, and
	 * expects the parsing not to raise an exception or to return null.
	 * @param expression The expression to parse
	 * @param parser The parser to use
	 */
	private static void shouldNotParse(final String expression, final BnfParser parser) {
		try {
			final Node parse = parser.parse(expression);
			if (parse != null) fail("The parsing of " + expression + " should have failed");
		} catch (ParseException ignored) {}
	}

	public static BnfParser readGrammar(final String filename, final String startRule, final boolean debugMode)
	{
		BnfParser parser = new BnfParser();
		parser.setDebugMode(debugMode);
		try
		{
			Scanner grammar = new Scanner(GrammarTests.class.getResourceAsStream(filename));
			parser.setGrammar(grammar);
		}
		catch (InvalidGrammar e)
		{
			fail("Error parsing grammar file " + filename + ": " + e);
		}
		parser.setStartRule(startRule);
		return parser;
	}

	private static class DummyLogger extends Logger {
		private boolean hasLogged = false;
		
		protected DummyLogger()
		{
			super("bullwinkle.GrammarTests.DummyLogger", null);
		}
		
		@Override
		public void log(Level l, String m)
		{
			hasLogged = true;
		}
		
		@Override
		public void log(Level l, String m, Object param1)
		{
			hasLogged = true;
		}
		
		public boolean hasLogged()
		{
			return hasLogged;
		}
	}

}
