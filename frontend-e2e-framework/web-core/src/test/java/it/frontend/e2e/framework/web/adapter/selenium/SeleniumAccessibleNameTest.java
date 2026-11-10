package it.frontend.e2e.framework.web.adapter.selenium;

import it.frontend.e2e.framework.core.model.selector.XPathSelector;
import it.frontend.e2e.framework.web.adapter.model.WebWaitSettings;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SeleniumAccessibleNameTest {
    private final WebDriver driver = mock(WebDriver.class);
    private final WebElement element = mock(WebElement.class);
    private final XPathSelector selector = XPathSelector.of("//button[@id='document']");

    @Test
    void readsComputedNameWithoutSubstitutingVisibleTextOrAriaLabel() {
        when(driver.findElement(any(By.class))).thenReturn(element);
        when(element.getAccessibleName()).thenReturn("Guida, scarica l'allegato");
        assertEquals("Guida, scarica l'allegato", adapter().getAccessibleName(selector));
        verify(element, never()).getText();
        verify(element, never()).getAttribute(any());
    }

    @Test
    void preservesAnEmptyNameSoTheContractCanRejectIt() {
        when(driver.findElement(any(By.class))).thenReturn(element);
        when(element.getAccessibleName()).thenReturn("");
        assertEquals("", adapter().getAccessibleName(selector));
    }

    @Test
    void propagatesDriverFailuresWithSelectorAndCause() {
        when(driver.findElement(any(By.class))).thenReturn(element);
        var failure = new WebDriverException("unsupported command");
        when(element.getAccessibleName()).thenThrow(failure);
        var error = assertThrows(IllegalStateException.class,
                () -> adapter().getAccessibleName(selector));
        assertSame(failure, error.getCause());
        assertTrue(error.getMessage().contains("document"));
    }

    @Test
    void retriesStaleReferencesUsingExistingRetryMechanism() {
        when(driver.findElement(any(By.class))).thenReturn(element);
        when(element.getAccessibleName()).thenThrow(new StaleElementReferenceException("stale"))
                .thenReturn("Guida, scarica l'allegato");
        assertEquals("Guida, scarica l'allegato", adapter().getAccessibleName(selector));
        verify(element, times(2)).getAccessibleName();
    }

    @Test
    void missingElementFailsInsteadOfBecomingAnEmptyName() {
        when(driver.findElement(any(By.class))).thenThrow(new NoSuchElementException("missing"));
        assertThrows(IllegalStateException.class, () -> adapter().getAccessibleName(selector));
    }

    private SeleniumApiAdapter adapter() {
        return new SeleniumApiAdapter(driver, new WebWaitSettings(0, 1, 0));
    }
}
