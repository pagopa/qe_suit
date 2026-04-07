package it.frontend.e2e.framework.web.adapter.model;

import it.frontend.e2e.framework.web.adapter.enums.Browser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DisplayName("BrowserSettings")
class BrowserSettingsTest {

    @Test
    @DisplayName("defaults dovrebbe impostare Chrome, non headless e argomenti vuoti")
    void shouldReturnExpectedDefaults() {
        BrowserSettings settings = BrowserSettings.defaults();

        assertEquals(Browser.CHROME, settings.browser());
        assertFalse(settings.headless());
        assertEquals(List.of(), settings.arguments());
    }
}

