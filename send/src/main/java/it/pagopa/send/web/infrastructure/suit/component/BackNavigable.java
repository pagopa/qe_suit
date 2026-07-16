package it.pagopa.send.web.infrastructure.suit.component;

import it.frontend.e2e.framework.core.capability.core.Clickable;

public interface BackNavigable {
    Clickable backButton();

    default void goBack() {
        backButton().click();
    }
}
