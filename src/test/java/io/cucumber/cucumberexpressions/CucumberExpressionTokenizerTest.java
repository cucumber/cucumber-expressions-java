package io.cucumber.cucumberexpressions;


import io.cucumber.cucumberexpressions.CucumberExpressionTokenizer.Token;
import org.junit.jupiter.api.Test;

import static io.cucumber.cucumberexpressions.CucumberExpressionTokenizer.Token.Type.ALTERNATION;
import static io.cucumber.cucumberexpressions.CucumberExpressionTokenizer.Token.Type.BEGIN_OPTIONAL;
import static io.cucumber.cucumberexpressions.CucumberExpressionTokenizer.Token.Type.BEGIN_PARAMETER;
import static io.cucumber.cucumberexpressions.CucumberExpressionTokenizer.Token.Type.END_OPTIONAL;
import static io.cucumber.cucumberexpressions.CucumberExpressionTokenizer.Token.Type.END_PARAMETER;
import static io.cucumber.cucumberexpressions.CucumberExpressionTokenizer.Token.Type.ESCAPED_ALTERNATION;
import static io.cucumber.cucumberexpressions.CucumberExpressionTokenizer.Token.Type.ESCAPED_BEGIN_OPTIONAL;
import static io.cucumber.cucumberexpressions.CucumberExpressionTokenizer.Token.Type.ESCAPED_BEGIN_PARAMETER;
import static io.cucumber.cucumberexpressions.CucumberExpressionTokenizer.Token.Type.ESCAPED_END_OPTIONAL;
import static io.cucumber.cucumberexpressions.CucumberExpressionTokenizer.Token.Type.ESCAPED_END_PARAMETER;
import static io.cucumber.cucumberexpressions.CucumberExpressionTokenizer.Token.Type.ESCAPED_WHITE_SPACE;
import static io.cucumber.cucumberexpressions.CucumberExpressionTokenizer.Token.Type.TEXT;
import static io.cucumber.cucumberexpressions.CucumberExpressionTokenizer.Token.Type.WHITE_SPACE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;

class CucumberExpressionTokenizerTest {

    private final CucumberExpressionTokenizer tokenizer = new CucumberExpressionTokenizer();


    @Test
    void emptyString() {
        assertThat(tokenizer.tokenize(""), empty());
    }

    @Test
    void phrase() {
        assertThat(tokenizer.tokenize("three blind mice"), contains(
                new Token("three", TEXT),
                new Token(" ", WHITE_SPACE),
                new Token("blind", TEXT),
                new Token(" ", WHITE_SPACE),
                new Token("mice", TEXT)
        ));
    }

    @Test
    void optional() {
        assertThat(tokenizer.tokenize("(blind)"), contains(
                new Token("(", BEGIN_OPTIONAL),
                new Token("blind", TEXT),
                new Token(")", END_OPTIONAL)
        ));
    }

    @Test
    void escapedOptional() {
        assertThat(tokenizer.tokenize("\\(blind\\)"), contains(
                new Token("\\(", ESCAPED_BEGIN_OPTIONAL),
                new Token("blind", TEXT),
                new Token("\\)", ESCAPED_END_OPTIONAL)
        ));
    }

    @Test
    void optionalPhrase() {
        assertThat(tokenizer.tokenize("three (blind) mice"), contains(
                new Token("three", TEXT),
                new Token(" ", WHITE_SPACE),
                new Token("(", BEGIN_OPTIONAL),
                new Token("blind", TEXT),
                new Token(")", END_OPTIONAL),
                new Token(" ", WHITE_SPACE),
                new Token("mice", TEXT)
        ));
    }

    @Test
    void parameter() {
        assertThat(tokenizer.tokenize("{string}"), contains(
                new Token("{", BEGIN_PARAMETER),
                new Token("string", TEXT),
                new Token("}", END_PARAMETER)
        ));
    }

    @Test
    void EscapedParameter() {
        assertThat(tokenizer.tokenize("\\{string\\}"), contains(
                new Token("\\{", ESCAPED_BEGIN_PARAMETER),
                new Token("string", TEXT),
                new Token("\\}", ESCAPED_END_PARAMETER)
        ));
    }

    @Test
    void parameterPhrase() {
        assertThat(tokenizer.tokenize("three {string} mice"), contains(
                new Token("three", TEXT),
                new Token(" ", WHITE_SPACE),
                new Token("{", BEGIN_PARAMETER),
                new Token("string", TEXT),
                new Token("}", END_PARAMETER),
                new Token(" ", WHITE_SPACE),
                new Token("mice", TEXT)
        ));
    }


    @Test
    void alternation() {
        assertThat(tokenizer.tokenize("(blind)"), contains(
                new Token("(", BEGIN_OPTIONAL),
                new Token("blind", TEXT),
                new Token(")", END_OPTIONAL)
        ));
    }

    @Test
    void escapedAlternation() {
        assertThat(tokenizer.tokenize("blind\\ and\\ famished\\/cripple"), contains(
                new Token("blind", TEXT),
                new Token("\\ ", ESCAPED_WHITE_SPACE),
                new Token("and", TEXT),
                new Token("\\ ", ESCAPED_WHITE_SPACE),
                new Token("famished", TEXT),
                new Token("\\/", ESCAPED_ALTERNATION),
                new Token("cripple", TEXT)
        ));
    }

    @Test
    void alternationPhrase() {
        assertThat(tokenizer.tokenize("three blind/cripple mice"), contains(
                new Token("three", TEXT),
                new Token(" ", WHITE_SPACE),
                new Token("blind", TEXT),
                new Token("/", ALTERNATION),
                new Token("cripple", TEXT),
                new Token(" ", WHITE_SPACE),
                new Token("mice", TEXT)
        ));
    }
}
