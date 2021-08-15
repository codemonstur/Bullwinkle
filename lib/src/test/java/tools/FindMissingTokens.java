package tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toSet;

public class FindMissingTokens {

    private static final Pattern FIND_TOKENS = Pattern.compile("(<[a-z_-]+>)");

    public static void main(final String... args) throws IOException {

        final var grammarFile = Paths.get("bullwinkle/src/test/resources/grammars/examples/java-full.bnf");

        final var definedRules = new HashSet<String>();
        final var usedRules = Files.lines(grammarFile)
            .map(line -> {
                final int poundOffset = line.indexOf('#');
                return poundOffset != -1 ? line.substring(0, poundOffset): line;
            })
            .map(String::trim)
            .filter(line -> !line.isEmpty())
            .filter(line -> line.contains(":="))
            .peek(line -> definedRules.add(line.substring(0, line.indexOf(":=")).trim()))
            .map(line -> line.substring(line.indexOf(":=")+2).trim())
            .flatMap(line -> toTokens(line).stream())
            .collect(toSet());

        System.out.println(stuff(definedRules, usedRules));
        System.out.println(stuff(usedRules, definedRules));
    }

    private static ArrayList<String> toTokens(final String line) {
        final var list = new ArrayList<String>();
        final Matcher matcher = FIND_TOKENS.matcher(line);
        while (matcher.find()) {
            list.add(matcher.group());
        }
        return list;
    }

    private static Set<String> stuff(final Set<String> source, final Set<String> remove) {
        final var copy = new HashSet<>(source);
        copy.removeAll(remove);
        return copy;
    }

}
