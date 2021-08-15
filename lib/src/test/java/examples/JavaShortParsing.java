package examples;

import bullwinkle.nodes.Node;

import java.io.IOException;

import static bullwinkle.BnfParserBuilder.newBnfParser;

public class JavaShortParsing {

    public static void main(final String... args) throws IOException {
        Node tree = newBnfParser()
            .addResourceAsGrammar("/grammars/examples/java-short.bnf")
            .build().parse("""
            package boe;
            class Test {
                public static void main(String[] args) {
                    System.out.println("Hello, world!");
                }
            }""");
        System.out.println(tree);
    }

}
