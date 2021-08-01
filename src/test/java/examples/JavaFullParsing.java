package examples;

import bullwinkle.error.InvalidGrammar;
import bullwinkle.error.ParseException;
import bullwinkle.parsing.BnfParser;
import bullwinkle.model.nodes.Node;

public class JavaFullParsing {

    public static void main(final String... args) throws InvalidGrammar, ParseException {
        BnfParser parser = new BnfParser(JavaFullParsing.class.getResourceAsStream("/grammars/examples/java-full.bnf"));
        Node tree = parser.parse("""
            package boe;
            class Test {
                public static void main(String[] args) {
                    System.out.println("Hello, world!");
                }
            }""");
        System.out.println(tree);
    }

}
