package examples;

import app.output.Graphviz;
import bullwinkle.error.VisitException;

import java.io.IOException;

import static bullwinkle.BnfParserBuilder.newBnfParser;

public class ShowParseTree {

	public static void main(final String... args) throws VisitException, IOException {
		final var visitor = new Graphviz();

		newBnfParser()
			.addResourceAsGrammar("/grammars/examples/ltl-fo.bnf").build()
			.parse("G (∀ x ∈ /path/to/pingus : (∀ y ∈ /x/position : ((x = \"0\") → (X (∃ z ∈ /y/abcd : ((z=x) ∨ (z=y)))))))")
			.prefixAccept(visitor);

		System.out.println(visitor.toOutputString());
	}

}
