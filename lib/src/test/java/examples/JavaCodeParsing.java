package examples;

import bullwinkle.nodes.Node;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static bullwinkle.BnfParserBuilder.newBnfParser;
import static java.nio.charset.StandardCharsets.UTF_8;

public class JavaCodeParsing {

    public static void main(final String... args) throws IOException {
        InputStream stream = new ByteArrayInputStream("""
            <package_declaration> := package <package_name>? ;
            <package_name> := ^[a-z]+
            """.getBytes(UTF_8));

        Node tree = newBnfParser().addGrammar(stream)
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
