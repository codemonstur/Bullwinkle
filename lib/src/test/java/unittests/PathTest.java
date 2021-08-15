package unittests;

import bullwinkle.util.NodePath;
import org.junit.Test;

import java.io.IOException;

import static bullwinkle.BnfParserBuilder.newBnfParser;

public class PathTest {

	@Test
	public void test1() throws IOException {
		final var node = newBnfParser()
			.addResourceAsGrammar("/grammars/tests/0.bnf")
			.startRule("<S>")
			.build().parse("SELECT a FROM t");
		/*List<ParseNode> result =*/ NodePath.getPath(node, "<S>.<selection>.<criterion>");
		//fail("Not yet implemented");
	}

	@Test
	public void test2() throws IOException {
		final var node = newBnfParser()
			.addResourceAsGrammar("/grammars/tests/0.bnf")
			.startRule("<S>")
			.build().parse("SELECT a FROM t");

		/*List<ParseNode> result =*/ NodePath.getPath(node, "<S>.<selection>.*");
		//fail("Not yet implemented");
	}

}
