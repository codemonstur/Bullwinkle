package examples;

import app.output.IndentedPlainText;
import bullwinkle.error.VisitException;

import java.io.IOException;

import static bullwinkle.BnfParserBuilder.newBnfParser;
import static java.util.logging.Level.ALL;

public class SimpleExample {

	public static void main(final String... args) throws VisitException, IOException {
		final var visitor = new IndentedPlainText();

		newBnfParser().logLevel(ALL)
			.addResourceAsGrammar("/grammars/examples/math-simple.bnf")
			.build().parse("(10 + (3 - 4))")
			.prefixAccept(visitor);

		System.out.println(visitor.toOutputString());
	}

}
