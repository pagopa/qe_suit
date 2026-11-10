package it.frontend.e2e.framework.web.adapter.selenium;

import it.frontend.e2e.framework.core.model.selector.XPathSelector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Stream;

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

    @ParameterizedTest
    @MethodSource("clickFailures")
    void preservesClickFailureAndCleansUpWithoutRetryingClickOrObservation(RuntimeException error) {
        doThrow(error).doNothing().when(button).click();

        assertSame(error, assertThrows(RuntimeException.class,
                () -> new SeleniumApiAdapter(driver, 1).clickAndGetRequestUrls(selector)));
        verify(button, times(1)).click();
        verify(js).executeScript(contains("state.observer.disconnect"), any());
        verify(js, never()).executeScript(contains("return state.urls.length"), any());
    }

    @ParameterizedTest
    @MethodSource("clickFailures")
    void preservesOriginalFailureIfCleanupAlsoFails(RuntimeException error) {
        JavascriptException cleanup = new JavascriptException("cleanup failed");
        doThrow(error).doNothing().when(button).click();
        when(js.executeScript(contains("state.observer.disconnect"), any())).thenThrow(cleanup);

        assertSame(error, assertThrows(RuntimeException.class,
                () -> new SeleniumApiAdapter(driver, 1).clickAndGetRequestUrls(selector)));
        assertArrayEquals(new Throwable[]{cleanup}, error.getSuppressed());
        verify(button, times(1)).click();
        verify(js).executeScript(contains("state.observer.disconnect"), any());
        verify(js, never()).executeScript(contains("return state.urls.length"), any());
    }

    static Stream<RuntimeException> clickFailures() {
        return Stream.of(
                new IllegalStateException("click failed"),
                new StaleElementReferenceException("element became stale during click"),
                new ElementClickInterceptedException("click intercepted"),
                new TimeoutException("click timed out"));
    }

    @Test
    void doesNotClickIfElementIsNotClickableAndCleansUp() {
        when(button.isEnabled()).thenReturn(false);

        assertThrows(TimeoutException.class,
                () -> new SeleniumApiAdapter(driver, 0).clickAndGetRequestUrls(selector));

        verify(button, never()).click();
        verify(js).executeScript(contains("state.observer.disconnect"), any());
        verify(js, never()).executeScript(contains("return state.urls.length"), any());
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