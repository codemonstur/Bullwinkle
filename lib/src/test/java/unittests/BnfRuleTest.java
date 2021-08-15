package unittests;

import static bullwinkle.BnfRule.parseRule;
import static unittests.util.Functions.assertContains;
import static unittests.util.Functions.assertSize;

import org.junit.Test;

import bullwinkle.tokens.TerminalToken;

public class BnfRuleTest {

	@Test
	public void ruleWithNonTerminalAndTokens() {
		final var expression = "<S> := ( <S> )";
		final var alternatives = parseRule(expression, false).getAlternatives();

		assertSize(alternatives, 1);

		final var allTerminalTokens = alternatives.get(0).getTerminalTokens();
		assertContains(allTerminalTokens, new TerminalToken("("));
		assertContains(allTerminalTokens, new TerminalToken(")"));
	}

    @Test
    public void alternativesForOptionals() {
		final var expression = "<S> := <package_declaration>? <import declarations>? <type declarations>?";
		final var alternatives = parseRule(expression, false).getAlternatives();

		assertSize(alternatives, 7);
    }

}
