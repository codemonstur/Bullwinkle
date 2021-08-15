package examples;

import app.parsing.Builds;
import app.parsing.ParseTreeObjectBuilder;
import app.parsing.ParseTreeObjectBuilder.BuildException;
import bullwinkle.nodes.Node;

import java.io.IOException;

import static bullwinkle.BnfParserBuilder.newBnfParser;

/**
 * In this example, we show the use of the "pop" and "clean" options of the
 * <tt>@Builds</tt> annotation.
 * <ul>
 * <li>The "pop" option avoids us from popping objects from the object stack
 * manually; rather, the method can expect to receive the proper number of
 * objects in an array</li>
 * <li>The "clean" option removes from the argument array all elements that
 * match <em>terminal</em> tokens in the corresponding grammar rule. In the
 * object builder for this example, this removes all parentheses and symbols
 * and keeps only the operands of an operator.</li>
 * </ul>  
 */
public class BuildExamplePop {

	public static void main(final String... args) throws BuildException, IOException {
		/* We first read a grammar and parse a simple expression */
		Node tree = newBnfParser()
			.addResourceAsGrammar("/grammars/examples/math-simple.bnf")
			.build().parse("(10 ÷ (2 × 3)) + (6 - 4)");
		
		/* We then create a builder, and ask it to create an ArithExp 
		 * object from the expression */
		MyBuilder builder = new MyBuilder();
		ArithmeticExpression exp = builder.build(tree);
		
		/* Just to show that it worked, we ask the resulting object to print
		 * itself. There are superfluous parentheses, due to the behaviour of
		 * the toString() method of ArithExp. */
		System.out.println(exp);
	}

	/**
	 * An object builder that creates ArithExp objects from the rules of the
	 * grammar. Notice how we can parse arithmetical expressions using 6 lines of
	 * BNF grammar, and about 15 lines for this builder. 
	 */
	public static class MyBuilder extends ParseTreeObjectBuilder<ArithmeticExpression> {
		@Builds(rule = "<add>", pop = true, clean = true)
		public ArithmeticExpression buildAdd(Object ... parts) {
			return new ArithmeticExpression.Add((ArithmeticExpression) parts[0], (ArithmeticExpression) parts[1]);
		}

		@Builds(rule = "<sub>", pop = true, clean = true)
		public ArithmeticExpression buildSub(Object ... parts) {
			return new ArithmeticExpression.Subtract((ArithmeticExpression) parts[0], (ArithmeticExpression) parts[1]);
		}
		
		@Builds(rule = "<mul>", pop = true, clean = true)
		public ArithmeticExpression buildMul(Object ... parts) {
			return new ArithmeticExpression.Multiply((ArithmeticExpression) parts[0], (ArithmeticExpression) parts[1]);
		}
		
		@Builds(rule = "<div>", pop = true, clean = true)
		public ArithmeticExpression buildDiv(Object ... parts) {
			return new ArithmeticExpression.Divide((ArithmeticExpression) parts[0], (ArithmeticExpression) parts[1]);
		}

		@Builds(rule = "<num>", pop = true)
		public ArithmeticExpression buildNum(Object ... parts) {
			return new ArithmeticExpression.Number(Integer.parseInt((String) parts[0]));
		}
	}

}
