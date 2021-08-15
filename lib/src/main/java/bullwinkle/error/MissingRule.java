package bullwinkle.error;

import bullwinkle.tokens.Token;

public final class MissingRule extends ParsingFailed {
    public MissingRule(final Token altToken) {
        super("Cannot find rule for token " + altToken.getName());
    }
}
