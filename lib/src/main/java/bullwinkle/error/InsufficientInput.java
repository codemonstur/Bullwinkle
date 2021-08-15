package bullwinkle.error;

import bullwinkle.BnfRule;
import bullwinkle.util.MutableString;

public final class InsufficientInput extends ParsingFailed {

    public InsufficientInput(final MutableString input, final BnfRule rule) {
        super("Did not consume anything of " + input + " with rule " + rule);
    }

}
