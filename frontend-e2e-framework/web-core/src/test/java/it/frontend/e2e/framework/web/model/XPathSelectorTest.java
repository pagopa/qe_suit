package it.frontend.e2e.framework.web.model;

import it.frontend.e2e.framework.core.model.selector.XPathSelector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("WebSelector")
class XPathSelectorTest {

    @Test
    @DisplayName("accetta un XPath assoluto")
    void shouldAcceptAbsoluteXPath() {
        XPathSelector selector = new XPathSelector("//div[@id='root']");
        assertEquals("//div[@id='root']", selector.getSelector());
    }

    @Test
    @DisplayName("accetta un XPath relativo")
    void shouldAcceptRelativeXPath() {
        XPathSelector selector = new XPathSelector(".//button[@type='submit']");
        assertEquals(".//button[@type='submit']", selector.getSelector());
    }

    @Test
    @DisplayName("trim del selettore")
    void shouldTrimSelector() {
        XPathSelector selector = new XPathSelector("  /div[1]/span  ");
        assertEquals("/div[1]/span", selector.getSelector());
    }

    @Test
    @DisplayName("rifiuta selettori non XPath")
    void shouldRejectNonXPathSelectors() {
        assertThrows(IllegalArgumentException.class, () -> new XPathSelector("myElement"));
        assertThrows(IllegalArgumentException.class, () -> new XPathSelector("#root > div"));
        assertThrows(IllegalArgumentException.class, () -> new XPathSelector("div.container"));
    }

    @Test
    @DisplayName("rifiuta parentesi non bilanciate")
    void shouldRejectUnbalancedParentheses() {
        assertThrows(IllegalArgumentException.class, () -> new XPathSelector("(//div[@id='root']"));
    }

    @Test
    @DisplayName("rifiuta parentesi quadre non bilanciate")
    void shouldRejectUnbalancedBrackets() {
        assertThrows(IllegalArgumentException.class, () -> new XPathSelector("//div[@id='root'"));
    }

    @Test
    @DisplayName("rifiuta null o stringhe vuote")
    void shouldRejectNullOrBlankSelector() {
        assertThrows(IllegalArgumentException.class, () -> new XPathSelector(null));
        assertThrows(IllegalArgumentException.class, () -> new XPathSelector(""));
        assertThrows(IllegalArgumentException.class, () -> new XPathSelector("   "));
    }

    @Test
    @DisplayName("toString include valore e tipo")
    void shouldRenderToString() {
        XPathSelector selector = new XPathSelector("//div[@id='root']");
        String text = selector.toString();

        assertTrue(text.contains("//div[@id='root']"));
        assertTrue(text.contains("XPath"));
    }
}
