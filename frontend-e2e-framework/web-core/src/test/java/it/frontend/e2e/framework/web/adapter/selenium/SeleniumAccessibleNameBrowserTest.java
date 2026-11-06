package it.frontend.e2e.framework.web.adapter.selenium;

import it.frontend.e2e.framework.core.model.selector.XPathSelector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/** Opt in with -Dsuit.accessibility.browser-test=true; only a local data URL is used. */
@EnabledIfSystemProperty(named = "suit.accessibility.browser-test", matches = "true")
class SeleniumAccessibleNameBrowserTest {
    @Test
    void browserComputesTheNameAndDetectsRegressionsIndependentlyOfVisibleText() {
        var options = new ChromeOptions();
        options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage");
        String binary = System.getProperty("suit.accessibility.browser-binary");
        if (binary != null) options.setBinary(binary);
        var driver = new ChromeDriver(options);
        try {
            String html = """
                    <button id="correct" aria-label="Guida, scarica l'allegato">Guida</button>
                    <button id="old" aria-label="Scarica il documento Guida">Guida</button>
                    <button id="wrong" aria-label="Altro, scarica l'allegato">Guida</button>
                    <button id="fallback">Guida</button>
                    <span id="label">Etichetta esterna</span>
                    <button id="labelled" aria-labelledby="label" aria-label="Ignorata">Guida</button>
                    """;
            driver.get("data:text/html;charset=utf-8," + URLEncoder.encode(html, StandardCharsets.UTF_8));
            var adapter = new SeleniumApiAdapter(driver, 1);
            assertEquals("Guida, scarica l'allegato", name(adapter, "correct"));
            assertNotEquals(name(adapter, "correct"), name(adapter, "old"));
            assertNotEquals(name(adapter, "correct"), name(adapter, "wrong"));
            assertEquals("Guida", name(adapter, "fallback"));
            assertEquals("Etichetta esterna", name(adapter, "labelled"));
        } finally {
            driver.quit();
        }
    }

    private String name(SeleniumApiAdapter adapter, String id) {
        return adapter.getAccessibleName(XPathSelector.of("//button[@id='" + id + "']"));
    }
}
