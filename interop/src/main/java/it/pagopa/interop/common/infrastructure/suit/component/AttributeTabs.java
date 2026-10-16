package it.pagopa.interop.common.infrastructure.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.domain.Component;

/**
 * Rappresenta l’intera sezione che contiene il testo descrittivo e le schede degli attributi.
 */
@XPath("//section[contains(@class, 'MuiPaper-root')]//h2[contains(text(), 'Requisiti di accesso')]/ancestor::section[1]")
public interface AttributeTabs extends Component, Clickable {

    CertifiedAttributeTab certifiedAttributesTab();

    DeclaredAttributeTab declaredAttributesTab();

    VerifiedAttributeTab verifiedAttributesTab();

    @Override
    default void assertLoaded() {
        certifiedAttributesTab().assertLoaded();
        declaredAttributesTab().assertLoaded();
        verifiedAttributesTab().assertLoaded();
    }
}
