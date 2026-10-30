package it.frontend.e2e.framework.web.adapter.selenium;

import it.frontend.e2e.framework.core.model.selector.XPathSelector;
import it.frontend.e2e.framework.web.adapter.model.WebWaitSettings;
import it.frontend.e2e.framework.web.capability.core.Gettable;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SeleniumApiAdapterExistsTest {
    @Test
    void missingElementReturnsFalseWithoutRetry() {
        WebDriver driver = mock(WebDriver.class);
        when(driver.findElements(any(By.class))).thenReturn(List.of());
        SeleniumApiAdapter adapter = new SeleniumApiAdapter(driver, new WebWaitSettings(10, 60, 0));
        assertFalse(adapter.exists(XPathSelector.of("//missing"), Gettable.FindPolicy.PRESENT));
        verify(driver, times(1)).findElements(any(By.class));
    }

    @Test
    void visibleAndClickableFollowTheirPolicies() {
        WebDriver driver = mock(WebDriver.class);
        WebElement element = mock(WebElement.class);
        when(driver.findElements(any(By.class))).thenReturn(List.of(element));
        when(element.isDisplayed()).thenReturn(true);
        when(element.isEnabled()).thenReturn(false);
        SeleniumApiAdapter adapter = new SeleniumApiAdapter(driver, new WebWaitSettings(10, 60, 0));
        XPathSelector selector = XPathSelector.of("//button");
        assertTrue(adapter.exists(selector, Gettable.FindPolicy.PRESENT));
        assertTrue(adapter.exists(selector, Gettable.FindPolicy.VISIBLE));
        assertFalse(adapter.exists(selector, Gettable.FindPolicy.CLICKABLE));
    }
}
