package it.frontend.e2e.framework.web.adapter.selenium;

import it.frontend.e2e.framework.web.adapter.enums.Browser;
import it.frontend.e2e.framework.web.adapter.model.BrowserSettings;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.ChromiumOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class WebDriverFactory {

    private static final String CHROME_HEADLESS_ARG = "--headless=new";

    private WebDriverFactory() {
        // Utility class: stateless, non istanziabile
    }

    public static WebDriver create(Browser browser, BrowserSettings settings) {
        BrowserSettings safeSettings = settings == null ? BrowserSettings.defaults() : settings;
        Browser resolvedBrowser = browser != null ? browser : safeSettings.browser();

        return switch (resolvedBrowser) {
            case CHROME -> createChromeDriver(safeSettings);
            case FIREFOX -> createFirefoxDriver(safeSettings);
            case EDGE -> createEdgeDriver(safeSettings);
            case SAFARI -> createSafariDriver(safeSettings);
        };
    }

    private static WebDriver createChromeDriver(BrowserSettings settings) {
        ChromeOptions options = new ChromeOptions();

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        options.setExperimentalOption("prefs", prefs);

        options.addArguments("--disable-notifications");
        options.addArguments("--disable-save-password-bubble");

        if (settings.headless()) {
            options.addArguments(CHROME_HEADLESS_ARG);
        }
        addExtraArguments(options, settings.arguments());

        return new ChromeDriver(options);
    }

    private static WebDriver createFirefoxDriver(BrowserSettings settings) {
        FirefoxOptions options = new FirefoxOptions();
        if (settings.headless()) {
            options.addArguments("-headless");
        }
        addExtraArguments(options, settings.arguments());
        return new FirefoxDriver(options);
    }

    private static WebDriver createEdgeDriver(BrowserSettings settings) {
        EdgeOptions options = new EdgeOptions();
        if (settings.headless()) {
            options.addArguments(CHROME_HEADLESS_ARG);
        }
        addExtraArguments(options, settings.arguments());
        return new EdgeDriver(options);
    }

    @SuppressWarnings("unused")
    private static WebDriver createSafariDriver(BrowserSettings settings) {
        SafariOptions options = new SafariOptions();
        // Nota: Safari non supporta nativamente la modalità headless tramite WebDriver.
        // La modalità headless richiede l'uso di Safari Technology Preview o configurazioni speciali.
        // Gli argomenti extra vengono ignorati da Safari poiché non li supporta come Chrome/Firefox.
        return new SafariDriver(options);
    }

    private static void addExtraArguments(ChromiumOptions<?> options, List<String> args) {
        if (args == null || args.isEmpty()) {
            return;
        }
        options.addArguments(args);
    }

    private static void addExtraArguments(FirefoxOptions options, List<String> args) {
        if (args == null || args.isEmpty()) {
            return;
        }
        options.addArguments(args);
    }
}
