package it.frontend.e2e.framework.web.adapter.enums;

import java.util.Locale;

public enum Browser {
    CHROME,
    FIREFOX,
    SAFARI,
    EDGE;

    public static Browser from(String value) {
        if (value == null || value.isBlank()) {
            return CHROME;
        }
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "chrome" -> CHROME;
            case "firefox" -> FIREFOX;
            case "edge" -> EDGE;
            case "safari" -> SAFARI;
            default -> throw new IllegalArgumentException("Browser non supportato: " + value + ". Valori ammessi: chrome, firefox, edge, safari");
        };
    }
}
