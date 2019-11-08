package io.cucumber.cucumberexpressions;

import io.cucumber.cucumberexpressions.Ast.AstNode;
import io.cucumber.cucumberexpressions.Ast.Token;
import org.junit.jupiter.api.Test;

import static io.cucumber.cucumberexpressions.Ast.AstNode.Type.ALTERNATION_NODE;
import static io.cucumber.cucumberexpressions.Ast.AstNode.Type.ALTERNATIVE_NODE;
import static io.cucumber.cucumberexpressions.Ast.AstNode.Type.EXPRESSION_NODE;
import static io.cucumber.cucumberexpressions.Ast.AstNode.Type.OPTIONAL_NODE;
import static io.cucumber.cucumberexpressions.Ast.AstNode.Type.PARAMETER_NODE;
import static io.cucumber.cucumberexpressions.Ast.AstNode.Type.TEXT_NODE;
import static io.cucumber.cucumberexpressions.Ast.Token.Type.ALTERNATION;
import static io.cucumber.cucumberexpressions.Ast.Token.Type.BEGIN_OPTIONAL;
import static io.cucumber.cucumberexpressions.Ast.Token.Type.BEGIN_PARAMETER;
import static io.cucumber.cucumberexpressions.Ast.Token.Type.END_OPTIONAL;
import static io.cucumber.cucumberexpressions.Ast.Token.Type.ESCAPE;
import static io.cucumber.cucumberexpressions.Ast.Token.Type.TEXT;
import static io.cucumber.cucumberexpressions.Ast.Token.Type.WHITE_SPACE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class CucumberExpressionParserTest {

    private final CucumberExpressionParser parser = new CucumberExpressionParser();

    @Test
    void emptyString() {
        assertThat(astOf(""), equalTo(
                new AstNode(EXPRESSION_NODE)
        ));
    }

    @Test
    void phrase() {
        assertThat(astOf("three blind mice"), equalTo(
                new AstNode(EXPRESSION_NODE,
                        new AstNode(TEXT_NODE, new Token("three", TEXT)),
                        new AstNode(TEXT_NODE, new Token(" ", WHITE_SPACE)),
                        new AstNode(TEXT_NODE, new Token("blind", TEXT)),
                        new AstNode(TEXT_NODE, new Token(" ", WHITE_SPACE)),
                        new AstNode(TEXT_NODE, new Token("mice", TEXT))
                )
        ));
    }

    @Test
    void optional() {
        assertThat(astOf("(blind)"), equalTo(
                new AstNode(EXPRESSION_NODE,
                        new AstNode(OPTIONAL_NODE,
                                new AstNode(TEXT_NODE, new Token("blind", TEXT))
                        )
                )
        ));
    }

    @Test
    void parameter() {
        assertThat(astOf("{string}"), equalTo(
                new AstNode(EXPRESSION_NODE,
                        new AstNode(PARAMETER_NODE,
                                new AstNode(TEXT_NODE, new Token("string", TEXT))
                        )
                )
        ));
    }

    @Test
    void anonymousParameter() {
        assertThat(astOf("{}"), equalTo(
                new AstNode(EXPRESSION_NODE,
                        new AstNode(PARAMETER_NODE)
                )
        ));
    }

    @Test
    void optionalPhrase() {
        assertThat(astOf("three (blind) mice"), equalTo(
                new AstNode(EXPRESSION_NODE,
                        new AstNode(TEXT_NODE, new Token("three", TEXT)),
                        new AstNode(TEXT_NODE, new Token(" ", WHITE_SPACE)),
                        new AstNode(OPTIONAL_NODE,
                                new AstNode(TEXT_NODE, new Token("blind", TEXT))
                        ),
                        new AstNode(TEXT_NODE, new Token(" ", WHITE_SPACE)),
                        new AstNode(TEXT_NODE, new Token("mice", TEXT))
                )
        ));
    }

    @Test
    void slash() {
        assertThat(astOf("\\"), equalTo(
                new AstNode(EXPRESSION_NODE,
                        new AstNode(TEXT_NODE, new Token("\\", ESCAPE))
                )
        ));
    }

    @Test
    void openingBrace() {
        assertThat(astOf("{"), equalTo(
                new AstNode(EXPRESSION_NODE,
                        new AstNode(TEXT_NODE, new Token("{", BEGIN_PARAMETER))
                )
        ));
    }

    @Test
    void openingParenthesis() {
        assertThat(astOf("("), equalTo(
                new AstNode(EXPRESSION_NODE,
                        new AstNode(TEXT_NODE, new Token("(", BEGIN_OPTIONAL))
                )
        ));
    }

    @Test
    void escapedOpeningParenthesis() {
        assertThat(astOf("\\("), equalTo(
                new AstNode(EXPRESSION_NODE,
                        new AstNode(TEXT_NODE, new Token("(", BEGIN_OPTIONAL))
                )
        ));
    }

    @Test
    void escapedOptional() {
        assertThat(astOf("\\(blind)"), equalTo(
                new AstNode(EXPRESSION_NODE,
                        new AstNode(TEXT_NODE, new Token("(", BEGIN_OPTIONAL)),
                        new AstNode(TEXT_NODE, new Token("blind", TEXT)),
                        new AstNode(TEXT_NODE, new Token(")", END_OPTIONAL))
                )
        ));
    }

    @Test
    void escapedOptionalPhrase() {
        assertThat(astOf("three \\(blind) mice"), equalTo(
                new AstNode(EXPRESSION_NODE,
                        new AstNode(TEXT_NODE, new Token("three", TEXT)),
                        new AstNode(TEXT_NODE, new Token(" ", WHITE_SPACE)),
                        new AstNode(TEXT_NODE, new Token("(", BEGIN_OPTIONAL)),
                        new AstNode(TEXT_NODE, new Token("blind", TEXT)),
                        new AstNode(TEXT_NODE, new Token(")", END_OPTIONAL)),
                        new AstNode(TEXT_NODE, new Token(" ", WHITE_SPACE)),
                        new AstNode(TEXT_NODE, new Token("mice", TEXT))
                )
        ));
    }

    @Test
    void escapedOptionalFollowedByOptional() {
        assertThat(astOf("three \\((very) blind) mice"), equalTo(
                new AstNode(EXPRESSION_NODE,
                        new AstNode(TEXT_NODE, new Token("three", TEXT)),
                        new AstNode(TEXT_NODE, new Token(" ", WHITE_SPACE)),
                        new AstNode(TEXT_NODE, new Token("(", BEGIN_OPTIONAL)),
                        new AstNode(OPTIONAL_NODE,
                                new AstNode(TEXT_NODE, new Token("very", TEXT))
                        ),
                        new AstNode(TEXT_NODE, new Token(" ", WHITE_SPACE)),
                        new AstNode(TEXT_NODE, new Token("blind", TEXT)),
                        new AstNode(TEXT_NODE, new Token(")", END_OPTIONAL)),
                        new AstNode(TEXT_NODE, new Token(" ", WHITE_SPACE)),
                        new AstNode(TEXT_NODE, new Token("mice", TEXT))
                )
        ));
    }

    @Test
    void optionalContainingEscapedOptional() {
        assertThat(astOf("three ((very\\) blind) mice"), equalTo(
                new AstNode(EXPRESSION_NODE,
                        new AstNode(TEXT_NODE, new Token("three", TEXT)),
                        new AstNode(TEXT_NODE, new Token(" ", WHITE_SPACE)),
                        new AstNode(OPTIONAL_NODE,
                                new AstNode(TEXT_NODE, new Token("(", BEGIN_OPTIONAL)),
                                new AstNode(TEXT_NODE, new Token("very", TEXT)),
                                new AstNode(TEXT_NODE, new Token(")", END_OPTIONAL)),
                                new AstNode(TEXT_NODE, new Token(" ", WHITE_SPACE)),
                                new AstNode(TEXT_NODE, new Token("blind", TEXT))
                        ),
                        new AstNode(TEXT_NODE, new Token(" ", WHITE_SPACE)),
                        new AstNode(TEXT_NODE, new Token("mice", TEXT))
                )
        ));
    }


    @Test
    void alternation() {
        assertThat(astOf("mice/rats"), equalTo(
                new AstNode(EXPRESSION_NODE,
                        new AstNode(ALTERNATION_NODE,
                                new AstNode(ALTERNATIVE_NODE,
                                        new AstNode(TEXT_NODE, new Token("mice", TEXT))
                                ),
                                new AstNode(ALTERNATIVE_NODE,
                                        new AstNode(TEXT_NODE, new Token("rats", TEXT)))
                        )
                )
        ));
    }

    @Test
    void escapedAlternation() {
        assertThat(astOf("mice\\/rats"), equalTo(
                new AstNode(EXPRESSION_NODE,
                        new AstNode(TEXT_NODE, new Token("mice", TEXT)),
                        new AstNode(TEXT_NODE, new Token("/", ALTERNATION)),
                        new AstNode(TEXT_NODE, new Token("rats", TEXT))
                )
        ));
    }


    @Test
    void alternationPhrase() {
        assertThat(astOf("three hungry/blind mice"), equalTo(
                new AstNode(EXPRESSION_NODE,
                        new AstNode(TEXT_NODE, new Token("three", TEXT)),
                        new AstNode(TEXT_NODE, new Token(" ", WHITE_SPACE)),
                        new AstNode(ALTERNATION_NODE,
                                new AstNode(ALTERNATIVE_NODE,
                                        new AstNode(TEXT_NODE, new Token("hungry", TEXT))
                                ),
                                new AstNode(ALTERNATIVE_NODE,
                                        new AstNode(TEXT_NODE, new Token("blind", TEXT))
                                )
                        ),
                        new AstNode(TEXT_NODE, new Token(" ", WHITE_SPACE)),
                        new AstNode(TEXT_NODE, new Token("mice", TEXT))
                )
        ));
    }

    @Test
    void alternationWithWhiteSpace() {
        assertThat(astOf("\\ three\\ hungry/blind\\ mice\\ "), equalTo(
                new AstNode(EXPRESSION_NODE,
                        new AstNode(ALTERNATION_NODE,
                                new AstNode(ALTERNATIVE_NODE,
                                        new AstNode(TEXT_NODE, new Token(" ", WHITE_SPACE)),
                                        new AstNode(TEXT_NODE, new Token("three", TEXT)),
                                        new AstNode(TEXT_NODE, new Token(" ", WHITE_SPACE)),
                                        new AstNode(TEXT_NODE, new Token("hungry", TEXT))
                                ),
                                new AstNode(ALTERNATIVE_NODE,
                                        new AstNode(TEXT_NODE, new Token("blind", TEXT)),
                                        new AstNode(TEXT_NODE, new Token(" ", WHITE_SPACE)),
                                        new AstNode(TEXT_NODE, new Token("mice", TEXT)),
                                        new AstNode(TEXT_NODE, new Token(" ", WHITE_SPACE))
                                )
                        )

                )
        ));
    }

    @Test
    void alternationWithUnusedEndOptional() {
        assertThat(astOf("three )blind\\ mice/rats"), equalTo(
                new AstNode(EXPRESSION_NODE,
                        new AstNode(TEXT_NODE, new Token("three", TEXT)),
                        new AstNode(TEXT_NODE, new Token(" ", WHITE_SPACE)),
                        new AstNode(ALTERNATION_NODE,
                                new AstNode(ALTERNATIVE_NODE,
                                        new AstNode(TEXT_NODE, new Token(")", END_OPTIONAL)),
                                        new AstNode(TEXT_NODE, new Token("blind", TEXT)),
                                        new AstNode(TEXT_NODE, new Token(" ", WHITE_SPACE)),
                                        new AstNode(TEXT_NODE, new Token("mice", TEXT))
                                ),
                                new AstNode(ALTERNATIVE_NODE,
                                        new AstNode(TEXT_NODE, new Token("rats", TEXT))
                                )
                        )
                )
        ));
    }

    @Test
    void alternationWithUnusedStartOptional() {
        assertThat(astOf("three blind\\ mice/rats("), equalTo(
                new AstNode(EXPRESSION_NODE,
                        new AstNode(TEXT_NODE, new Token("three", TEXT)),
                        new AstNode(TEXT_NODE, new Token(" ", WHITE_SPACE)),
                        new AstNode(ALTERNATION_NODE,
                                new AstNode(ALTERNATIVE_NODE,
                                        new AstNode(TEXT_NODE, new Token("blind", TEXT)),
                                        new AstNode(TEXT_NODE, new Token(" ", WHITE_SPACE)),
                                        new AstNode(TEXT_NODE, new Token("mice", TEXT))
                                ),
                                new AstNode(ALTERNATIVE_NODE,
                                        new AstNode(TEXT_NODE, new Token("rats", TEXT)),
                                        new AstNode(TEXT_NODE, new Token("(", BEGIN_OPTIONAL))
                                )
                        )
                )
        ));
    }

    @Test
    void alternationFollowedByOptional() {
        assertThat(astOf("three blind\\ rat/cat(s)"), equalTo(
                new AstNode(EXPRESSION_NODE,
                        new AstNode(TEXT_NODE, new Token("three", TEXT)),
                        new AstNode(TEXT_NODE, new Token(" ", WHITE_SPACE)),
                        new AstNode(ALTERNATION_NODE,
                                new AstNode(ALTERNATIVE_NODE,
                                        new AstNode(TEXT_NODE, new Token("blind", TEXT)),
                                        new AstNode(TEXT_NODE, new Token(" ", WHITE_SPACE)),
                                        new AstNode(TEXT_NODE, new Token("rat", TEXT))
                                ),
                                new AstNode(ALTERNATIVE_NODE,
                                        new AstNode(TEXT_NODE, new Token("cat", TEXT)),
                                        new AstNode(OPTIONAL_NODE,
                                                new AstNode(TEXT_NODE, new Token("s", TEXT))
                                        )
                                )
                        )
                )
        ));
    }

    private AstNode astOf(String expression) {
        return parser.parse(expression);
    }

}
