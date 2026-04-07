package it.frontend.e2e.framework.core.adapter;

import it.frontend.e2e.framework.core.assertion.AssertionAction;
import it.frontend.e2e.framework.core.model.AbstractPresentationElement;
import it.frontend.e2e.framework.core.model.location.Location;
import it.frontend.e2e.framework.core.model.selector.Selector;

import java.util.List;
import java.util.Optional;

public interface IPresentationApiAdapter<
        S extends Selector,
        L extends Location,
        E extends AbstractPresentationElement<S, L>
        >  {

    // Operazioni di ricerca
    Optional<E> findElement(S selector);
    Optional<E> findElementAndAssert(S selector, AssertionAction<E> assertion);
    Optional<List<E>> findElements(S selector);
    Optional<List<E>> findElementsAndAssert(S selector, AssertionAction<E> assertion);

    // Operazioni di interazione
    void click(S selector);
    void clickAndAssert(S selector, AssertionAction<E> assertion);
    void sendText(S selector, String text);
    void sendTextAndAssert(S selector, String text, AssertionAction<E> assertion);
    void clear(S selector);
    void clearAndAssert(S selector, AssertionAction<E> assertion);

    default void sendTextAndAssert(S selector, String text) {
        sendText(selector, text);
        getTextAndAssert(selector, actualText -> {
            if (!actualText.equals(text)) {
                throw new AssertionError("Expected text: " + text + ", but got: " + actualText);
            }
        });
    }

    default void clearAndAssert(S selector) {
        clear(selector);
        getTextAndAssert(selector, actualText -> {
            if (!actualText.isEmpty()) {
                throw new AssertionError("Expected text to be empty, but got: " + actualText);
            }
        });
    }

    // Operazioni di verifica
    boolean isDisplayed(S selector);
    boolean isEnabled(S selector);
    Optional<String> getText(S selector);
    Optional<String> getTextAndAssert(S selector, AssertionAction<String> assertion);

    // Operazioni di attesa
    void waitForElement(S selector, long timeoutSeconds);
    void waitUntilElementDisappears(S selector, long timeoutSeconds);

    // Operazioni di navigazione
    L getLocation();
    L getLocationAndAssert(AssertionAction<L> assertion);
    void navigateTo(L locator);
    void navigateToAndAssert(L locator, AssertionAction<L> assertion);

    default void navigateToAndCheck(L locator) {
        navigateToAndAssert(locator, actualLocator -> {
            if (!actualLocator.equals(locator)) {
                throw new AssertionError("Expected location: " + locator + ", but got: " + actualLocator);
            }
        });
    }
}
