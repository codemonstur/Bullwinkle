package examples;

import bullwinkle.error.InvalidGrammar;
import bullwinkle.error.ParseException;
import bullwinkle.parsing.BnfParser;
import bullwinkle.model.nodes.Node;

public class JavaShortParsing {

    public static void main(final String... args) throws ParseException, InvalidGrammar {
        BnfParser parser = new BnfParser(JavaShortParsing.class.getResourceAsStream("/grammars/examples/java-short.bnf"));
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
