package examples;

/**
 * A simple class (and descendants) to represent arithmetical
 * expressions. These classes are used by {@link BuildExampleStack} and
 * {@link BuildExamplePop} to illustrate the use of the
 */
public interface ArithmeticExpression {

	/**
	 * Object that represents an integer
	 */
	class Number implements ArithmeticExpression {
		int n;

		public Number(int n)
		{
			this.n = n;
		}

		@Override
		public String toString()
		{
			return Integer.toString(n);
		}
	}

	/**
	 * Generic class that represents binary operators, with a left and a right operand
	 */
	abstract class BinaryExpression implements ArithmeticExpression {
		ArithmeticExpression left;
		ArithmeticExpression right;
		String symbol;

		public BinaryExpression(ArithmeticExpression left, ArithmeticExpression right, String symbol) {
			this.left = left;
			this.right = right;
			this.symbol = symbol;
		}

		@Override
		public String toString()
		{
			return "(" + left.toString() + ")" + symbol + "(" + right.toString() + ")"; 
		}
	}

	class Add extends BinaryExpression {
		public Add(ArithmeticExpression left, ArithmeticExpression right)
		{
			super(left, right, "+");
		}
	}
	class Subtract extends BinaryExpression {
		public Subtract(ArithmeticExpression left, ArithmeticExpression right)
		{
			super(left, right, "-");
		}
	}
	class Multiply extends BinaryExpression {
		public Multiply(ArithmeticExpression left, ArithmeticExpression right)
		{
			super(left, right, "×");
		}
	}
	class Divide extends BinaryExpression {
		public Divide(ArithmeticExpression left, ArithmeticExpression right)
		{
			super(left, right, "÷");
		}
	}

}