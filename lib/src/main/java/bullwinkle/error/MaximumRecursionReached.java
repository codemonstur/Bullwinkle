package bullwinkle.error;

public final class MaximumRecursionReached extends ParsingFailed {
    public MaximumRecursionReached(final int steps) {
        super("Maximum recursion steps of "+steps+" reached. If the input string is indeed valid, try increasing the limit.");
    }
}
