package examples;

import bullwinkle.error.InvalidGrammar;
import bullwinkle.error.ParseException;
import bullwinkle.error.VisitException;
import bullwinkle.parsing.BnfParser;
import bullwinkle.model.nodes.Node;
import bullwinkle.output.Graphviz;

public class SimpleExample {

	public static void main(final String... args) throws VisitException, InvalidGrammar, ParseException {
		BnfParser parser = new BnfParser(SimpleExample.class.getResourceAsStream("/grammars/examples/math-simple.bnf"));
		parser.setDebugMode(true);
		Node node2 = parser.parse("(10 + (3 - 4))");
		Graphviz visitor = new Graphviz();
		node2.prefixAccept(visitor);
		System.out.println(visitor.toOutputString());
	}

}
