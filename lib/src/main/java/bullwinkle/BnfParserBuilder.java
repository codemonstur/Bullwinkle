package bullwinkle;

import bullwinkle.error.InvalidRule;
import bullwinkle.tokens.NonTerminalToken;
import bullwinkle.tokens.Token;
import bullwinkle.tokens.TokenString;

import java.io.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Level.*;

public class BnfParserBuilder {

    public static BnfParserBuilder newBnfParser() {
        return new BnfParserBuilder();
    }

    private final List<BnfRule> rules = new LinkedList<>();
    private BnfRule startRule;
    private int maxRecursionSteps = 50;
    private boolean partialParsing = false;
    private boolean useStickyRules = false;

    private Logger logger = Logger.getAnonymousLogger();

    private BnfParserBuilder() {
        logger.setLevel(OFF);
    }

    /**
     * Adds a new case to an existing rule
     *
     * @param index      The location in the list of cases where to put the new case. Use 0 to
     *                   put the new case at the beginning.
     * @param ruleName   The name of the rule
     * @param caseString The case to add
     */
    public BnfParserBuilder addCaseToRule(final int index, final String ruleName, final String caseString) {
        final var rule = getRule(ruleName, rules);
        if (rule == null) return this;

        rule.addAlternative(index, new TokenString(new NonTerminalToken(caseString)));
        return this;
    }

    public BnfParserBuilder addCaseToRule(final String ruleName, final String caseString) {
        return addCaseToRule(0, ruleName, caseString);
    }

    /**
     * Sets the maximum number of recursion steps that the parsing will use.
     * This setting is there to avoid infinite loops in the parsing.
     * Default is 50.
     * @param steps The maximum number of recursion steps. Must be positive.
     */
    public BnfParserBuilder maxRecursionSteps(final int steps) {
        if (steps > 0) maxRecursionSteps = steps;
        return this;
    }

    /**
     * Instructs the parser to perform partial parsing. In partial parsing,
     * a string can contain instances of non-terminal tokens. For example,
     * given the rules
     * <pre>
     * &lt;S&gt; := &lt;A&gt; b
     * &lt;A&gt; := foo | bar
     * </pre>
     * With partial parsing, the string <tt>&lt;A&gt; b</tt> will parse.
     * In this case, note that the resulting parse tree can have non-terminal
     * tokens as leaves.
     * default is false.
     * @param partialParsing Set to true to enable partial parsing
     */
    public BnfParserBuilder partialParsing(final boolean partialParsing) {
        this.partialParsing = partialParsing;
        return this;
    }

    /**
     * A sticky rule is a rule that has the || operator. It does something weird
     * I don't full understand. It also clashes with Java BNF parsing which uses
     * the || symbols for the boolean OR.
     * Default value is false.
     * @param stickyRules true for enabled, false for disabled
     */
    public BnfParserBuilder stickyRules(final boolean stickyRules) {
        this.useStickyRules = stickyRules;
        return this;
    }

    public BnfParserBuilder logger(final Logger logger) {
        this.logger = logger;
        return this;
    }

    public BnfParserBuilder logLevel(final Level level) {
        logger.setLevel(level);
        return this;
    }

    /**
     * Sets the start rule to be used for the parsing
     * @param tokenName The name of the non-terminal to be used. It must
     *   be defined in the grammar, otherwise a <code>NullPointerException</code>
     *   will be thrown when attempting to parse a string.
     */
    public BnfParserBuilder startRule(final String tokenName) {
        return startRule(new NonTerminalToken(tokenName));
    }

    public BnfParserBuilder startRule(final NonTerminalToken token) {
        startRule = getRule(token, rules);
        return this;
    }

    public BnfParserBuilder addGrammar(final String grammar) throws InvalidRule {
        try (final var reader = new BufferedReader(new StringReader(grammar))) {
            return addRules(parseRules(reader, useStickyRules));
        } catch (IOException ignored) {
            throw new IllegalStateException("Strings don't throw IOExceptions");
        }
    }
    public BnfParserBuilder addGrammar(final InputStream is) throws IOException, InvalidRule {
        if (is == null) throw new InvalidRule("The InputStream provided is null");
        return addRules(parseRules(new BufferedReader(new InputStreamReader(is)), useStickyRules));
    }
    public BnfParserBuilder addResourceAsGrammar(final String resource) throws IOException, InvalidRule {
        try (final var reader = new BufferedReader(new InputStreamReader(BnfParserBuilder.class.getResourceAsStream(resource)))) {
            return addRules(parseRules(reader, useStickyRules));
        }
    }

    public BnfParserBuilder addRules(final Collection<BnfRule> rules) {
        for (final var rule : rules) addRule(rule);
        return this;
    }

    public BnfParserBuilder addRule(final String rule) {
        return addRule(BnfRule.parseRule(rule, useStickyRules));
    }
    public BnfParserBuilder addRule(final BnfRule rule) {
        final var r_left = rule.getLeftHandSide();
        for (final var in_rule : rules) {
            final var in_left = in_rule.getLeftHandSide();
            if (r_left.equals(in_left)) {
                in_rule.addAlternatives(rule.getAlternatives());
                break;
            }
        }
        // No rule with the same LHS was found
        rules.add(rule);
        return this;
    }

    public BnfParserBuilder addRule(final int position, final BnfRule rule) {
        final var r_left = rule.getLeftHandSide();
        for (final var in_rule : rules) {
            final var in_left = in_rule.getLeftHandSide();
            if (r_left.equals(in_left)) {
                in_rule.addAlternatives(position, rule.getAlternatives());
                break;
            }
        }
        // No rule with the same LHS was found
        rules.add(rule);
        return this;
    }

    public BnfParser build() {
        if (startRule == null) {
            if (rules.isEmpty()) throw new IllegalArgumentException("No start rule could be found");
            startRule = rules.get(0);
        }
        return new BnfParser(rules, startRule, logger, maxRecursionSteps, partialParsing);
    }

    private static BnfRule getRule(final Token token, final List<BnfRule> rules) {
        return token == null ? null : getRule(token.getName(), rules);
    }

    private static BnfRule getRule(final String ruleName, final List<BnfRule> rules) {
        for (final var rule : rules) {
            final var lhs = rule.getLeftHandSide().getName();
            if (ruleName.equals(lhs)) return rule;
        }
        return null;
    }

    public static List<BnfRule> parseRules(final BufferedReader reader, final boolean useSticky) throws IOException {
        final var rules = new LinkedList<BnfRule>();

        String line; while ( (line = reader.readLine()) != null) {
            // Remove comments and empty lines
            final int offsetPound = line.indexOf('#');
            if (offsetPound != -1) line = line.substring(0, offsetPound);
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            rules.add(BnfRule.parseRule(line.trim(), useSticky));
        }

        return rules;
    }

}
