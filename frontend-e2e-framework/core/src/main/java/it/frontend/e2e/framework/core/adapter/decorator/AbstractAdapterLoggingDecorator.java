package it.frontend.e2e.framework.core.adapter.decorator;

import it.frontend.e2e.framework.core.adapter.IPresentationApiAdapter;
import it.frontend.e2e.framework.core.assertion.AssertionAction;
import it.frontend.e2e.framework.core.logging.ILogger;
import it.frontend.e2e.framework.core.logging.Slf4jLogger;
import it.frontend.e2e.framework.core.model.AbstractPresentationElement;
import it.frontend.e2e.framework.core.model.location.Location;
import it.frontend.e2e.framework.core.model.selector.Selector;

import java.util.List;
import java.util.Optional;

public abstract class AbstractAdapterLoggingDecorator<
            S extends Selector,
            L extends Location,
            E extends AbstractPresentationElement<S, L>
        > implements IPresentationApiAdapter<S, L, E> {

    protected final ILogger logger = new Slf4jLogger();
    private final IPresentationApiAdapter<S, L, E> wrappedAdapter;

    protected AbstractAdapterLoggingDecorator(IPresentationApiAdapter<S, L, E> wrappedAdapter) {
        this.wrappedAdapter = wrappedAdapter;
    }

    @Override
    public void clear(S selector) {
        logger.logAction(selector.toString(), "CLEAR", null);
        wrappedAdapter.clear(selector);
    }

    @Override
    public void clearAndAssert(S selector) {
        logger.logAction(selector.toString(), "CLEAR_AND_ASSERT", null);
        IPresentationApiAdapter.super.clearAndAssert(selector);
    }

    @Override
    public void clearAndAssert(S selector, AssertionAction<E> assertion) {
        logger.logAction(selector.toString(), "CLEAR_AND_ASSERT", null);
        wrappedAdapter.clearAndAssert(selector, assertion);
    }

    @Override
    public void click(S selector) {
        logger.logAction(selector.toString(), "CLICK", null);
        wrappedAdapter.click(selector);
    }

    @Override
    public void clickAndAssert(S selector, AssertionAction<E> assertion) {
        logger.logAction(selector.toString(), "CLICK_AND_ASSERT", null);
        wrappedAdapter.clickAndAssert(selector, assertion);
    }

    @Override
    public Optional<E> findElement(S selector) {
        logger.logDebug("Finding element: " + selector);
        return wrappedAdapter.findElement(selector);
    }

    @Override
    public Optional<E> findElementAndAssert(S selector, AssertionAction<E> assertion) {
        logger.logDebug("Finding element with assertion: " + selector);
        return wrappedAdapter.findElementAndAssert(selector, assertion);
    }

    @Override
    public Optional<List<E>> findElements(S selector) {
        logger.logDebug("Finding elements: " + selector);
        return wrappedAdapter.findElements(selector);
    }

    @Override
    public Optional<List<E>> findElementsAndAssert(S selector, AssertionAction<E> assertion) {
        logger.logDebug("Finding elements with assertion: " + selector);
        return wrappedAdapter.findElementsAndAssert(selector, assertion);
    }

    @Override
    public L getLocation() {
        logger.logDebug("Getting current location");
        return wrappedAdapter.getLocation();
    }

    @Override
    public L getLocationAndAssert(AssertionAction<L> assertion) {
        logger.logDebug("Getting location with assertion");
        return wrappedAdapter.getLocationAndAssert(assertion);
    }

    @Override
    public Optional<String> getText(S selector) {
        logger.logAction(selector.toString(), "GET_TEXT", null);
        return wrappedAdapter.getText(selector);
    }

    @Override
    public Optional<String> getTextAndAssert(S selector, AssertionAction<String> assertion) {
        logger.logAction(selector.toString(), "GET_TEXT_AND_ASSERT", null);
        return wrappedAdapter.getTextAndAssert(selector, assertion);
    }

    @Override
    public boolean isDisplayed(S selector) {
        logger.logDebug("Checking if displayed: " + selector);
        return wrappedAdapter.isDisplayed(selector);
    }

    @Override
    public boolean isEnabled(S selector) {
        logger.logDebug("Checking if enabled: " + selector);
        return wrappedAdapter.isEnabled(selector);
    }

    @Override
    public void navigateTo(L locator) {
        logger.logNavigation(locator.toString());
        wrappedAdapter.navigateTo(locator);
    }

    @Override
    public void navigateToAndAssert(L locator, AssertionAction<L> assertion) {
        logger.logNavigation(locator.toString());
        wrappedAdapter.navigateToAndAssert(locator, assertion);
    }

    @Override
    public void navigateToAndCheck(L locator) {
        logger.logNavigation(locator.toString());
        IPresentationApiAdapter.super.navigateToAndCheck(locator);
    }

    @Override
    public void sendText(S selector, String text) {
        logger.logAction(selector.toString(), "SEND_TEXT", "Text: " + text);
        wrappedAdapter.sendText(selector, text);
    }

    @Override
    public void sendTextAndAssert(S selector, String text) {
        logger.logAction(selector.toString(), "SEND_TEXT_AND_ASSERT", "Text: " + text);
        IPresentationApiAdapter.super.sendTextAndAssert(selector, text);
    }

    @Override
    public void sendTextAndAssert(S selector, String text, AssertionAction<E> assertion) {
        logger.logAction(selector.toString(), "SEND_TEXT_AND_ASSERT", "Text: " + text);
        wrappedAdapter.sendTextAndAssert(selector, text, assertion);
    }

    @Override
    public void waitForElement(S selector, long timeoutSeconds) {
        long startTime = System.currentTimeMillis();
        logger.logDebug("Waiting for element: " + selector + " | Timeout: " + timeoutSeconds + "s");
        try {
            wrappedAdapter.waitForElement(selector, timeoutSeconds);
            long duration = System.currentTimeMillis() - startTime;
            logger.logWait(selector.toString(), duration, true);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.logWait(selector.toString(), duration, false);
            throw e;
        }
    }

    @Override
    public void waitUntilElementDisappears(S selector, long timeoutSeconds) {
        long startTime = System.currentTimeMillis();
        logger.logInfo("Waiting for element to disappear: " + selector + " | Timeout: " + timeoutSeconds + "s");
        try {
            wrappedAdapter.waitUntilElementDisappears(selector, timeoutSeconds);
            long duration = System.currentTimeMillis() - startTime;
            logger.logWait(selector.toString() + " (disappear)", duration, true);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.logWait(selector.toString() + " (disappear)", duration, false);
            throw e;
        }
    }
}
