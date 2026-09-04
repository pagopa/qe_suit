package it.frontend.e2e.framework.web.adapter.selenium;

import it.frontend.e2e.framework.core.assertion.AssertionAction;
import it.frontend.e2e.framework.core.model.selector.XPathSelector;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.model.WebPresentationElement;
import it.frontend.e2e.framework.web.model.location.Url;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Interactive;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("SeleniumApiAdapter")
class SeleniumApiAdapterTest {

    @Test
    @DisplayName("findElement mappa testo, tag, attributi HTML e location")
    void shouldMapWebElementToPresentationElement() {
        WebDriver driver = mock(WebDriver.class, withSettings().extraInterfaces(JavascriptExecutor.class));
        WebElement loginButton = mock(WebElement.class);
        JavascriptExecutor js = (JavascriptExecutor) driver;

        when(driver.findElement(any(By.class))).thenReturn(loginButton);
        when(driver.getCurrentUrl()).thenReturn("https://example.test/login");
        when(loginButton.isDisplayed()).thenReturn(true);
        when(loginButton.isEnabled()).thenReturn(true);
        when(loginButton.getText()).thenReturn("Accedi");
        when(loginButton.getTagName()).thenReturn("button");
        when(js.executeScript(anyString(), eq(loginButton))).thenReturn(Map.of("id", "login-btn", "class", "primary"));

        SeleniumApiAdapter adapter = new SeleniumApiAdapter(driver);
        XPathSelector selector = XPathSelector.of("//button[@id='login-btn']");

        Optional<WebPresentationElement> found = adapter.findElement(selector);
        assertTrue(found.isPresent());
        assertEquals("Accedi", found.get().getText());
        assertEquals("button", found.get().getTag());
        assertEquals("login-btn", found.get().getAttributes().get("id"));
        assertEquals("primary", found.get().getAttributes().get("class"));
        assertEquals("https://example.test/login", found.get().getLocation().getUrl());
    }

    @Test
    @DisplayName("click, sendText e clear delegano al WebElement")
    void shouldDelegateInteractionMethodsToWebElement() {
        WebDriver driver = mock(WebDriver.class, withSettings().extraInterfaces(JavascriptExecutor.class, Interactive.class));
        WebElement webElement = mock(WebElement.class);

        when(webElement.isDisplayed()).thenReturn(true);
        when(webElement.isEnabled()).thenReturn(true);

        when(webElement.getAttribute("value")).thenReturn("mario.rossi");

        when(driver.findElement(any(By.class))).thenReturn(webElement);

        SeleniumApiAdapter adapter = new SeleniumApiAdapter(driver, 1);
        XPathSelector selector = XPathSelector.of("//input[@id='username']");

        adapter.click(selector);
        adapter.sendText(selector, "mario.rossi");
        adapter.clear(selector);

        verify(webElement).click();
        verify(webElement).sendKeys("mario.rossi");

        verify(webElement, atLeastOnce()).getAttribute("value");
    }

    @Test
    @DisplayName("getTextAndAssert ritorna il testo e applica l'assertion")
    void shouldApplyAssertionOnText() {
        WebDriver driver = mock(WebDriver.class, withSettings().extraInterfaces(JavascriptExecutor.class));
        WebElement webElement = mock(WebElement.class);
        when(webElement.isDisplayed()).thenReturn(true);
        when(webElement.isEnabled()).thenReturn(true);
        when(driver.findElement(any(By.class))).thenReturn(webElement);
        when(webElement.getText()).thenReturn("Codice OTP");

        SeleniumApiAdapter adapter = new SeleniumApiAdapter(driver);
        XPathSelector selector = XPathSelector.of("//label[@id='otp-label']");
        @SuppressWarnings("unchecked")
        AssertionAction<String> assertion = mock(AssertionAction.class);

        Optional<String> text = adapter.getTextAndAssert(selector, assertion);

        assertTrue(text.isPresent());
        assertEquals("Codice OTP", text.get());
        verify(assertion).assertOn("Codice OTP");
    }

    @Test
    @DisplayName("isDisplayed ritorna false quando l'elemento non esiste")
    void shouldReturnFalseWhenElementIsMissing() {
        WebDriver driver = mock(WebDriver.class, withSettings().extraInterfaces(JavascriptExecutor.class));
        when(driver.findElement(any(By.class))).thenThrow(new NoSuchElementException("not found"));

        SeleniumApiAdapter adapter = new SeleniumApiAdapter(driver, 1);

        assertFalse(adapter.isDisplayed(XPathSelector.of("//*[@id='missingId']")));
    }

    @Test
    @DisplayName("waitUntilElementDisappears completa quando l'elemento non e' visibile")
    void shouldWaitUntilElementDisappears() {
        WebDriver driver = mock(WebDriver.class, withSettings().extraInterfaces(JavascriptExecutor.class));
        WebElement webElement = mock(WebElement.class);
        when(driver.findElement(any(By.class))).thenReturn(webElement);
        when(webElement.isDisplayed()).thenReturn(false);

        SeleniumApiAdapter adapter = new SeleniumApiAdapter(driver);

        adapter.waitUntilElementDisappears(XPathSelector.of("//*[@id='toast-message']"), 1);

        verify(driver).findElement(any(By.class));
    }

    @Test
    @DisplayName("navigateToAndAssert naviga e applica assertion sulla location corrente")
    void shouldNavigateAndAssertLocation() {
        WebDriver driver = mock(WebDriver.class, withSettings().extraInterfaces(JavascriptExecutor.class));
        when(driver.getCurrentUrl()).thenReturn("https://example.test/dashboard");

        SeleniumApiAdapter adapter = new SeleniumApiAdapter(driver);
        Url expected = Url.of("https://example.test/dashboard");
        @SuppressWarnings("unchecked")
        AssertionAction<Url> assertion = mock(AssertionAction.class);

        adapter.navigateToAndAssert(expected, assertion);

        verify(driver).get("https://example.test/dashboard");
        verify(assertion).assertOn(expected);
    }

    @Test
    @DisplayName("findElements ritorna tutti gli elementi mappati")
    void shouldMapMultipleElements() {
        WebDriver driver = mock(WebDriver.class, withSettings().extraInterfaces(JavascriptExecutor.class));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebElement first = mock(WebElement.class);
        WebElement second = mock(WebElement.class);

        when(driver.findElements(any(By.class))).thenReturn(List.of(first, second));
        when(driver.getCurrentUrl()).thenReturn("https://example.test/list");
        when(first.getText()).thenReturn("Uno");
        when(second.getText()).thenReturn("Due");
        when(first.getTagName()).thenReturn("li");
        when(second.getTagName()).thenReturn("li");
        when(js.executeScript(anyString(), eq(first))).thenReturn(Map.of("data-id", "1"));
        when(js.executeScript(anyString(), eq(second))).thenReturn(Map.of("data-id", "2"));

        SeleniumApiAdapter adapter = new SeleniumApiAdapter(driver);
        Optional<List<WebPresentationElement>> elements = adapter.findElements(XPathSelector.of("//li[@class='item']"), FindPolicy.PRESENT);

        assertTrue(elements.isPresent());
        assertEquals(2, elements.get().size());
        assertEquals("Uno", elements.get().get(0).getText());
        assertEquals("2", elements.get().get(1).getAttributes().get("data-id"));
    }
}

