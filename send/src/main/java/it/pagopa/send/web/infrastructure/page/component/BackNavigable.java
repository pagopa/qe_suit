package it.pagopa.send.web.infrastructure.page.component;

import it.frontend.e2e.framework.core.capability.core.Clickable;

public interface BackNavigable {
    Clickable backButton();

    default void goBack() {
        backButton().click();
    }
}
