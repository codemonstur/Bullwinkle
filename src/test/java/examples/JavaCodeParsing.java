package examples;

import bullwinkle.error.InvalidGrammar;
import bullwinkle.error.ParseException;
import bullwinkle.parsing.BnfParser;
import bullwinkle.model.nodes.Node;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

public class JavaCodeParsing {

    public static void main(final String... args) throws InvalidGrammar, ParseException {
        InputStream stream = new ByteArrayInputStream("""
            <package_declaration> := package <package_name>? ;
            <package_name> := ^[a-z]+
            """.getBytes(UTF_8));

        BnfParser parser = new BnfParser(stream);
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
