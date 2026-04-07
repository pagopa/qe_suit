package it.frontend.e2e.framework.web.adapter.model;

import it.frontend.e2e.framework.web.adapter.enums.Browser;

import java.util.List;

public record BrowserSettings(Browser browser, boolean headless, List<String> arguments) {
    public BrowserSettings {
        browser = browser == null ? Browser.CHROME : browser;
        arguments = arguments == null ? List.of() : List.copyOf(arguments);
    }

    public static BrowserSettings defaults() {
        return new BrowserSettings(Browser.CHROME, false, List.of());
    }

    public static BrowserSettings of(String browserName, boolean headless, List<String> arguments) {
        return new BrowserSettings(Browser.from(browserName), headless, arguments);
    }
}
