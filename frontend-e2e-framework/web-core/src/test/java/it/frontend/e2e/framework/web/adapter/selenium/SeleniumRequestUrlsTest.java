package it.frontend.e2e.framework.web.adapter.selenium;

import it.frontend.e2e.framework.core.model.selector.XPathSelector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SeleniumRequestUrlsTest {
    private final WebDriver driver = mock(WebDriver.class, withSettings().extraInterfaces(JavascriptExecutor.class));
    private final JavascriptExecutor js = (JavascriptExecutor) driver;
    private final WebElement button = mock(WebElement.class);
    private final XPathSelector selector = XPathSelector.of("//button");

    @BeforeEach
    void setUp() {
        when(driver.findElement(any(By.class))).thenReturn(button);
        when(button.isDisplayed()).thenReturn(true);
        when(button.isEnabled()).thenReturn(true);
    }

    @Test
    void returnsFullUrlsAndDuplicatesAfterOneClickAndCleansUp() {
        String url = "https://example.test/assignments?limit=10&signingStates=REJECTED";
        when(js.executeScript(contains("return state.urls.length"), any()))
                .thenReturn(List.of(url, url));

        assertEquals(List.of(url, url), new SeleniumApiAdapter(driver, 1).clickAndGetRequestUrls(selector));

        var order = inOrder(js, button);
        order.verify(js).executeScript(contains("state.observer.observe"), any());
        order.verify(button).click();
        order.verify(js).executeScript(contains("return state.urls.length"), any());
        order.verify(js).executeScript(contains("state.observer.disconnect"), any());
        verify(button, times(1)).click();
    }

    @Test
    void waitsForDelayedEntries() {
        when(js.executeScript(contains("return state.urls.length"), any()))
                .thenReturn(null, List.of("https://example.test/api"));

        assertEquals(List.of("https://example.test/api"),
                new SeleniumApiAdapter(driver, 2).clickAndGetRequestUrls(selector));
        verify(button, times(1)).click();
    }

    @Test
    void timesOutWithoutRequestsOrQuietWindowAndCleansUp() {
        TimeoutException error = assertThrows(TimeoutException.class,
                () -> new SeleniumApiAdapter(driver, 0).clickAndGetRequestUrls(selector));

        String message = error.getMessage();
        assertNotNull(message);
        assertTrue(message.contains("No HTTP API request observed"));
        verify(js).executeScript(contains("state.observer.disconnect"), any());
        verify(button, times(1)).click();
    }

    @Test
    void preservesClickFailureAndCleansUpWithoutRetryingObservation() {
        IllegalStateException error = new IllegalStateException("click failed");
        doThrow(error).when(button).click();

        assertSame(error, assertThrows(IllegalStateException.class,
                () -> new SeleniumApiAdapter(driver, 1).clickAndGetRequestUrls(selector)));
        verify(js).executeScript(contains("state.observer.disconnect"), any());
        verify(js, never()).executeScript(contains("return state.urls.length"), any());
    }

    @Test
    void preservesOriginalFailureIfCleanupAlsoFails() {
        IllegalStateException error = new IllegalStateException("click failed");
        JavascriptException cleanup = new JavascriptException("cleanup failed");
        doThrow(error).when(button).click();
        when(js.executeScript(contains("state.observer.disconnect"), any())).thenThrow(cleanup);

        assertSame(error, assertThrows(IllegalStateException.class,
                () -> new SeleniumApiAdapter(driver, 1).clickAndGetRequestUrls(selector)));
        assertArrayEquals(new Throwable[]{cleanup}, error.getSuppressed());
    }

    @Test
    void rejectsUnsupportedJavascriptBeforeClicking() {
        WebDriver unsupported = mock(WebDriver.class);
        assertThrows(IllegalStateException.class,
                () -> new SeleniumApiAdapter(unsupported).clickAndGetRequestUrls(selector));
        verifyNoInteractions(unsupported);
    }

    @Test
    void failsIfObservationIsLostDuringNavigation() {
        JavascriptException error = new JavascriptException("Request observation lost");
        when(js.executeScript(contains("return state.urls.length"), any())).thenThrow(error);
        assertSame(error, assertThrows(JavascriptException.class,
                () -> new SeleniumApiAdapter(driver, 1).clickAndGetRequestUrls(selector)));
        verify(js).executeScript(contains("state.observer.disconnect"), any());
    }

    @Test
    void rejectsInvalidInputAndInvalidBrowserResults() {
        assertThrows(IllegalArgumentException.class,
                () -> new SeleniumApiAdapter(driver).clickAndGetRequestUrls(null));
        when(js.executeScript(contains("return state.urls.length"), any())).thenReturn(List.of(42));
        assertThrows(IllegalStateException.class,
                () -> new SeleniumApiAdapter(driver, 1).clickAndGetRequestUrls(selector));
    }
}