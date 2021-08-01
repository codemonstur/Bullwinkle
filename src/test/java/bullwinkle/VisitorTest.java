package bullwinkle;

import static org.junit.Assert.*;

import bullwinkle.error.InvalidGrammar;
import bullwinkle.error.ParseException;
import bullwinkle.error.VisitException;
import bullwinkle.model.nodes.Node;
import bullwinkle.parsing.BnfParser;
import bullwinkle.parsing.PrintVisitor;
import org.junit.Test;

import bullwinkle.output.Graphviz;
import bullwinkle.output.IndentedPlainText;
import bullwinkle.output.Xml;

public class VisitorTest {

	@Test
	public void testPrefix() throws VisitException {
		Node root = new Node("foo");
		{
			Node c = new Node("bar");
			{
				Node d = new Node("0");
				c.addChild(d);
			}
			{
				Node d = new Node("1");
				c.addChild(d);
			}
			root.addChild(c);
		}
		PrintVisitor v = new PrintVisitor();
		root.prefixAccept(v);
		String obtained = v.getString();
		assertEquals("foo,bar,0,pop,1,pop,pop,pop,", obtained);
	}
	
	@Test
	public void testPostfix() throws VisitException {
		Node root = new Node("foo");
		{
			Node c = new Node("bar");
			{
				Node d = new Node("0");
				c.addChild(d);
			}
			{
				Node d = new Node("1");
				c.addChild(d);
			}
			root.addChild(c);
		}
		PrintVisitor v = new PrintVisitor();
		root.postfixAccept(v);
		String obtained = v.getString();
		assertEquals("0,pop,1,pop,bar,pop,foo,pop,", obtained);
	}
	
	@Test
	public void testText() throws InvalidGrammar, ParseException, VisitException {
		BnfParser parser = new BnfParser(VisitorTest.class.getResourceAsStream("/grammars/tests/grammar-0.bnf"));
		Node node = parser.parse("SELECT a FROM t");
		IndentedPlainText visitor = new IndentedPlainText();
		node.postfixAccept(visitor);
		String output = visitor.toOutputString();
		assertNotNull(output);
	}
	
	@Test
	public void testGraphviz() throws InvalidGrammar, ParseException, VisitException {
		BnfParser parser = new BnfParser(VisitorTest.class.getResourceAsStream("/grammars/tests/grammar-0.bnf"));
		Node node = parser.parse("SELECT a FROM t");
		Graphviz visitor = new Graphviz();
		node.postfixAccept(visitor);
		String output = visitor.toOutputString();
		assertNotNull(output);
	}
	
	@Test
	public void testXml() throws InvalidGrammar, ParseException, VisitException {
		BnfParser parser = new BnfParser(VisitorTest.class.getResourceAsStream("/grammars/tests/grammar-0.bnf"));
		Node node = parser.parse("SELECT a FROM t");
		Xml visitor = new Xml();
		visitor.setTokenElementName("tok");
		visitor.setTopElementName("top");
		node.postfixAccept(visitor);
		String output = visitor.toOutputString();
		assertNotNull(output);
		assertTrue(output.contains("<top>"));
		assertTrue(output.contains("<tok>"));
	}
	
}
