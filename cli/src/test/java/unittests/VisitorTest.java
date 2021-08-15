package unittests;

import static org.junit.Assert.*;
import static unittests.util.Functions.newTestBnfParser;

import app.parsing.PrintVisitor;
import bullwinkle.error.VisitException;
import bullwinkle.nodes.Node;
import org.junit.Test;

import app.output.Graphviz;
import app.output.IndentedPlainText;
import app.output.Xml;

import java.io.IOException;

public class VisitorTest {

	@Test
	public void testPrefix() throws VisitException {
		final var visitor = new PrintVisitor();

		new Node("foo")
			.addChild(new Node("bar")
				.addChild(new Node("0"))
				.addChild(new Node("1")))
			.prefixAccept(visitor);

		assertEquals("foo,bar,0,pop,1,pop,pop,pop,", visitor.getString());
	}
	
	@Test
	public void testPostfix() throws VisitException {
		final var visitor = new PrintVisitor();

		new Node("foo")
			.addChild(new Node("bar")
				.addChild(new Node("0"))
				.addChild(new Node("1")))
			.postfixAccept(visitor);

		assertEquals("0,pop,1,pop,bar,pop,foo,pop,", visitor.getString());
	}

	private static final String TEXT_OUTPUT = """
		<S>
		 <selection>
		  SELECT
		  <criterion>
		   a
		  FROM
		  <S>
		   <tablename>
		    t
		""";
	@Test
	public void testText() throws VisitException, IOException {
		final var visitor = new IndentedPlainText();

		newTestBnfParser("0.bnf")
			.build().parse("SELECT a FROM t")
			.prefixAccept(visitor);

		assertEquals(TEXT_OUTPUT, visitor.toOutputString());
	}
	
	@Test
	public void testGraphviz() throws VisitException, IOException {
		final var visitor = new Graphviz();

		newTestBnfParser("0.bnf")
			.build().parse("SELECT a FROM t")
			.postfixAccept(visitor);

		assertNotNull(visitor.toOutputString());
	}
	
	@Test
	public void testXml() throws VisitException, IOException {
		final var visitor = new Xml();
		visitor.setTokenElementName("tok");
		visitor.setTopElementName("top");

		newTestBnfParser("0.bnf")
			.build().parse("SELECT a FROM t")
			.postfixAccept(visitor);

		final var output = visitor.toOutputString();
		assertNotNull(output);
		assertTrue(output.contains("<top>"));
		assertTrue(output.contains("<tok>"));
	}
	
}
