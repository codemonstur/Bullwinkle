package bullwinkle;

import bullwinkle.error.ParseException;
import bullwinkle.model.nodes.Node;
import bullwinkle.parsing.BnfParser;
import bullwinkle.util.NodePath;
import org.junit.Test;

public class PathTest {

	@Test
	public void test1() throws ParseException {
		BnfParser parser = GrammarTests.readGrammar("/grammars/tests/grammar-0.bnf", "<S>", false);
		Node pn = parser.parse("SELECT a FROM t");
		String path = "<S>.<selection>.<criterion>";
		/*List<ParseNode> result =*/ NodePath.getPath(pn, path);
		//fail("Not yet implemented");
	}

	@Test
	public void test2() throws ParseException {
		BnfParser parser = GrammarTests.readGrammar("/grammars/tests/grammar-0.bnf", "<S>", false);
		Node pn = parser.parse("SELECT a FROM t");
		String path = "<S>.<selection>.*";
		/*List<ParseNode> result =*/ NodePath.getPath(pn, path);
		//fail("Not yet implemented");
	}

}
