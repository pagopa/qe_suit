package it.frontend.e2e.framework.annotation.processor.xpath.rule;

import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class XPathValidator {

    public boolean isValidTypeXPath(String xpath) {
        return isSyntacticallyValid(xpath);
    }

    public boolean isValidMethodXPath(String xpath) {
        String normalized = normalize(xpath);
        if (normalized == null)
            throw new IllegalArgumentException("XPath is empty or contains only whitespace");

        if (!normalized.startsWith("."))
            throw new IllegalArgumentException("XPath path must start with '.'");

        return isCompilable(normalized);
    }

    public boolean isSyntacticallyValid(String xpath) {
        String normalized = normalize(xpath);
        if (normalized == null)
            throw new IllegalArgumentException("XPath is empty or contains only whitespace");

        if (!isXPathNotation(normalized))
            throw new IllegalArgumentException("XPath must start with '/', './', './/' or '(...)'");

        ensureBalanced(normalized, '(', ')', "XPath has unbalanced parentheses");
        ensureBalanced(normalized, '[', ']', "XPath has unbalanced square brackets");

        return isCompilable(normalized);
    }

    public boolean isValid(String xpath) {
        return isSyntacticallyValid(xpath);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean isCompilable(String xpath) {
        try {
            XPathFactory.newInstance().newXPath().compile(xpath);
        } catch (XPathExpressionException e) {
            throw new IllegalArgumentException(e);
        }

        return true;
    }

    private boolean isXPathNotation(String xpath) {
        if (xpath.startsWith("/") || xpath.startsWith("./") || xpath.startsWith(".//") || xpath.startsWith("//")) {
            return true;
        }
        // Gestione espressioni tra parentesi con eventuali predicati o funzioni dopo la chiusura
        if (xpath.startsWith("(")) {
            int closeIdx = findMatchingParenthesis(xpath, 0);
            if (closeIdx > 0) {
                String inner = xpath.substring(1, closeIdx).trim();
                return isXPathNotation(inner);
            }
        }
        return false;
    }

    // Trova la parentesi chiusa corrispondente alla prima aperta
    private int findMatchingParenthesis(String s, int openIdx) {
        int depth = 0;
        for (int i = openIdx; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '(') depth++;
            else if (c == ')') {
                depth--;
                if (depth == 0) return i;
            }
        }
        return -1; // non trovata
    }

    private void ensureBalanced(String value, char open, char close, String message) {
        int count = 0;
        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);
            if (current == open) {
                count++;
            } else if (current == close) {
                count--;
            }

            if (count < 0) {
                throw new IllegalArgumentException(message);
            }
        }

        if (count != 0) {
            throw new IllegalArgumentException(message);
        }
    }
}