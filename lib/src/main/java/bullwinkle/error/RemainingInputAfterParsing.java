package bullwinkle.error;

public final class RemainingInputAfterParsing extends ParsingFailed {
    public RemainingInputAfterParsing() {
        super("The top-level rule must parse the complete string");
    }
}
