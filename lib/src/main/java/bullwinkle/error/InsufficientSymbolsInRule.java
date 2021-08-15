package bullwinkle.error;

import bullwinkle.BnfRule;

public final class InsufficientSymbolsInRule extends ParsingFailed {
    public InsufficientSymbolsInRule(final BnfRule rule) {
        super("Expected more symbols with rule " + rule);
    }
}
