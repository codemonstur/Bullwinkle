package bullwinkle.error;

public class ParsingFailed extends RuntimeException {

    public ParsingFailed() {}
    public ParsingFailed(final String message) {
        super(message);
    }

}
