package examples;

import java.util.Deque;

import bullwinkle.error.InvalidGrammar;
import bullwinkle.error.ParseException;
import bullwinkle.parsing.BnfParser;
import bullwinkle.model.Builds;
import bullwinkle.model.nodes.Node;
import bullwinkle.parsing.ParseTreeObjectBuilder;
import bullwinkle.parsing.ParseTreeObjectBuilder.BuildException;

/**
 * In this example, we show how the <tt>@Builds</tt> annotation can be used
 * to identify methods that handle specific non-terminal symbols in a
 * grammar.
 */
public class BuildExampleStack {

	public static void main(final String... args) throws InvalidGrammar, ParseException, BuildException {
		/* We first read a grammar and parse a simple expression */
		BnfParser parser = new BnfParser(BuildExampleStack.class.getResourceAsStream("/grammars/examples/math-pn.bnf"));
		Node tree = parser.parse("+ 10 - 3 4");
		
		/* We then create a builder, and ask it to create an ArithExp 
		 * object from the expression */
		MyBuilder builder = new MyBuilder();
		ArithmeticExpression exp = builder.build(tree);
		
		/* Just to show that it worked, we ask the resulting object to print
		 * itself. Notice how the way ArithExp objects print themselves is
		 * different from the syntax that was used to create them. */
		System.out.println(exp);
	}

	public static class MyBuilder extends ParseTreeObjectBuilder<ArithmeticExpression> {
		@Builds(rule = "<add>")
		public void buildAdd(Deque<Object> stack) {
			ArithmeticExpression e2 = (ArithmeticExpression) stack.pop();
			ArithmeticExpression e1 = (ArithmeticExpression) stack.pop();
			stack.pop(); // symbol
			stack.push(new ArithmeticExpression.Add(e1, e2));
		}

		@Builds(rule = "<sub>")
		public void buildSub(Deque<Object> stack) {
			ArithmeticExpression e2 = (ArithmeticExpression) stack.pop();
			ArithmeticExpression e1 = (ArithmeticExpression) stack.pop();
			stack.pop(); // symbol
			stack.push(new ArithmeticExpression.Subtract(e1, e2));
		}

		@Builds(rule = "<num>")
		public void buildNum(Deque<Object> stack) {
			String s = (String) stack.pop(); // symbol
			stack.push(new ArithmeticExpression.Number(Integer.parseInt(s)));
		}
	}

}
