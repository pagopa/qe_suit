package it.frontend.e2e.framework.web.adapter.selenium;

import it.frontend.e2e.framework.core.assertion.AssertionAction;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.adapter.model.BrowserSettings;
import it.frontend.e2e.framework.web.model.location.Url;
import it.frontend.e2e.framework.web.model.WebPresentationElement;
import it.frontend.e2e.framework.core.model.selector.XPathSelector;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class SeleniumApiAdapter implements IWebPresentationApiAdapter {

    private static long DEFAULT_WAIT_TIMEOUT_SECONDS = 10;
    private final WebDriver driver;

    public SeleniumApiAdapter(BrowserSettings settings) {
        BrowserSettings safeSettings = settings == null ? BrowserSettings.defaults() : settings;
        this.driver = WebDriverFactory.create(safeSettings.browser(), safeSettings);
    }

    SeleniumApiAdapter(WebDriver driver, long defaultWaitTimeoutSeconds) {
        if (driver == null) {
            throw new IllegalArgumentException("driver cannot be null");
        }
        this.driver = driver;
        DEFAULT_WAIT_TIMEOUT_SECONDS = defaultWaitTimeoutSeconds;
    }

    SeleniumApiAdapter(WebDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("driver cannot be null");
        }
        this.driver = driver;
    }

    @Override
    public Optional<WebPresentationElement> findElement(XPathSelector selector) {
        try {
            WebElement webElement = findWebElement(selector);
            return Optional.of(toPresentationElement(selector, webElement));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<WebPresentationElement> findElementAndAssert(XPathSelector selector, AssertionAction<WebPresentationElement> assertion) {
        Optional<WebPresentationElement> element = findElement(selector);
        element.ifPresent(found -> applyAssertion(found, assertion));
        return element;
    }

    @Override
    public Optional<List<WebPresentationElement>> findElements(XPathSelector selector) {
        try {
            List<WebElement> webElements = findWebElements(selector);
            List<WebPresentationElement> elements = webElements.stream()
                    .map(webElement -> toPresentationElement(selector, webElement))
                    .toList();
            return Optional.of(elements);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<List<WebPresentationElement>> findElementsAndAssert(XPathSelector selector, AssertionAction<WebPresentationElement> assertion) {
        Optional<List<WebPresentationElement>> elements = findElements(selector);
        elements.ifPresent(foundElements -> foundElements.forEach(found -> applyAssertion(found, assertion)));
        return elements;
    }

    @Override
    public void click(XPathSelector selector) {
        findWebElement(selector).click();
    }

    @Override
    public void clickAndAssert(XPathSelector selector, AssertionAction<WebPresentationElement> assertion) {
        click(selector);
        findElement(selector).ifPresent(found -> applyAssertion(found, assertion));
    }

    @Override
    public void sendText(XPathSelector selector, String text) {
        WebElement element = findWebElement(selector);
        element.sendKeys(text);
    }

    @Override
    public void sendTextAndAssert(XPathSelector selector, String text, AssertionAction<WebPresentationElement> assertion) {
        sendText(selector, text);
        findElement(selector).ifPresent(found -> applyAssertion(found, assertion));
    }

    @Override
    public void clear(XPathSelector selector) {
        findWebElement(selector).clear();
    }

    @Override
    public void clearAndAssert(XPathSelector selector, AssertionAction<WebPresentationElement> assertion) {
        clear(selector);
        findElement(selector).ifPresent(found -> applyAssertion(found, assertion));
    }

    @Override
    public boolean isDisplayed(XPathSelector selector) {
        try {
            return findWebElement(selector).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isEnabled(XPathSelector selector) {
        try {
            return findWebElement(selector).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Optional<String> getText(XPathSelector selector) {
        try {
            WebElement element = findWebElement(selector);
            return Optional.of(resolveElementText(element));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> getTextAndAssert(XPathSelector selector, AssertionAction<String> assertion) {
        Optional<String> text = getText(selector);
        text.ifPresent(found -> applyAssertion(found, assertion));
        return text;
    }

    @Override
    public void waitForElement(XPathSelector selector, long timeoutSeconds) {
        findWebElement(selector, timeoutSeconds);
    }

    @Override
    public void waitUntilElementDisappears(XPathSelector selector, long timeoutSeconds) {
        new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.invisibilityOfElementLocated(toBy(selector)));
    }

    @Override
    public Url getLocation() {
        return Url.of(driver.getCurrentUrl());
    }

    @Override
    public Url getLocationAndAssert(AssertionAction<Url> assertion) {
        Url location = getLocation();
        applyAssertion(location, assertion);
        return location;
    }

    @Override
    public void navigateTo(Url locator) {
        driver.get(locator.getUrl());
    }

    @Override
    public void navigateToAndAssert(Url locator, AssertionAction<Url> assertion) {
        navigateTo(locator);
        Url current = getLocation();
        applyAssertion(current, assertion);
    }

    private WebElement findWebElement(XPathSelector selector, long timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.presenceOfElementLocated(toBy(selector)));
    }

    private List<WebElement> findWebElements(XPathSelector selector, long timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.presenceOfAllElementsLocatedBy(toBy(selector)));
    }

    private WebElement findWebElement(XPathSelector selector) {
        return findWebElement(selector, DEFAULT_WAIT_TIMEOUT_SECONDS);
    }

    private List<WebElement> findWebElements(XPathSelector selector) {
        return findWebElements(selector, DEFAULT_WAIT_TIMEOUT_SECONDS);
    }

    private By toBy(XPathSelector selector) {
        return new By.ByXPath(selector.getSelector());
    }

    private WebPresentationElement toPresentationElement(XPathSelector selector, WebElement webElement) {
        WebPresentationElement element = new WebPresentationElement(selector, getLocation());
        element.setText(resolveElementText(webElement));
        element.setTag(webElement.getTagName());
        element.setAttributes(readAttributes(webElement));
        return element;
    }

    private String resolveElementText(WebElement element) {
        String tagName = element.getTagName();
        if (tagName != null) {
            String normalizedTag = tagName.toLowerCase();
            if ("input".equals(normalizedTag) || "textarea".equals(normalizedTag)) {
                String value = element.getAttribute("value");
                return value == null ? "" : value;
            }
        }

        String text = element.getText();
        return text == null ? "" : text;
    }

    private <T> void applyAssertion(T value, AssertionAction<T> assertion) {
        if (assertion != null) {
            assertion.assertOn(value);
        }
    }

    private Map<String, String> readAttributes(WebElement element) {
        if (!(driver instanceof JavascriptExecutor js)) {
            return Map.of();
        }

        Object raw = js.executeScript(
                "const attrs = arguments[0].attributes;" +
                        "const out = {};" +
                        "for (let i =0; i < attrs.length; i++) {" +
                        " out[attrs[i].name] = attrs[i].value;" +
                        "}" +
                        "return out;",
                element );

        if (raw instanceof Map<?, ?> map) {
            Map<String, String> attributes = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                attributes.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
            }
            return attributes;
        }

        return Map.of();
    }
}
