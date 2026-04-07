package it.frontend.e2e.framework.annotation.processor.xpath;

import it.frontend.e2e.framework.annotation.processor.xpath.rule.XPathValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class XPathValidatorTest {

    private final XPathValidator validator = new XPathValidator();

    @Test
    @DisplayName("Accetta XPath assoluto valido")
    void shouldAcceptAbsoluteXPath() {
        assertTrue(validator.isSyntacticallyValid("//*[@id='username']"));
    }

    @Test
    @DisplayName("Rifiuta XPath con prefisso non supportato")
    void shouldRejectUnsupportedNotationPrefix() {
        assertThrows(IllegalArgumentException.class,
                () -> validator.isSyntacticallyValid("mlmlml//*[@id='username']"));
    }

    @Test
    @DisplayName("Rifiuta XPath con parentesi tonde non bilanciate")
    void shouldRejectUnbalancedParentheses() {
        assertThrows(IllegalArgumentException.class,
                () -> validator.isSyntacticallyValid("(//*[@id='username']"));
    }

    @Test
    @DisplayName("Rifiuta XPath con parentesi quadre non bilanciate")
    void shouldRejectUnbalancedSquareBrackets() {
        assertThrows(IllegalArgumentException.class,
                () -> validator.isSyntacticallyValid("//*[@id='username'"));
    }

    @Test
    @DisplayName("Rifiuta XPath di metodo senza prefisso punto")
    void shouldRejectMethodXPathWithoutDotPrefix() {
        assertThrows(IllegalArgumentException.class,
                () -> validator.isValidMethodXPath("//*[@id='username']"));
    }

    @Test
    @DisplayName("Rifiuta espressioni arbitrarie tra parentesi anche se contengono slash")
    void shouldRejectArbitraryExpressionsInParentheses() {
        assertThrows(IllegalArgumentException.class,
                () -> validator.isSyntacticallyValid("(qualsiasi/cosa)"));
    }

    @Test
    @DisplayName("Rifiuta espressioni tra parentesi con sintassi non-XPath")
    void shouldRejectInvalidParenthesesExpressions() {
        assertThrows(IllegalArgumentException.class,
                () -> validator.isSyntacticallyValid("(hello/world)"));
        assertThrows(IllegalArgumentException.class,
                () -> validator.isSyntacticallyValid("(123/456)"));
    }

    @Test
    @DisplayName("Accetta espressioni XPath valide tra parentesi")
    void shouldAcceptValidXPathInParentheses() {
        assertTrue(validator.isSyntacticallyValid("(//*[@id='Notifiche-page'])"));
        assertTrue(validator.isSyntacticallyValid("(//div[@class='header'])"));
        assertTrue(validator.isSyntacticallyValid("(.//*[@id='test'])"));
    }

    @Test
    @DisplayName("Rifiuta espressioni tra parentesi che non iniziano con percorso XPath")
    void shouldRejectParenthesesWithoutXPathPrefix() {
        assertThrows(IllegalArgumentException.class,
                () -> validator.isSyntacticallyValid("(qualsiasi/cosa)"));
        assertThrows(IllegalArgumentException.class,
                () -> validator.isSyntacticallyValid("(test/path)"));
    }
}
