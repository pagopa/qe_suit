package it.pagopa.interop.common.infrastructure.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Dialog;
import it.pagopa.infrastructure.suit.component.Menu;

@XPath(".//li[contains(@class, 'MuiBox-root')]")
public interface CertifiedAttribute extends Component, Clickable {

    String EMPTY_THRESHOLD_LABEL = "Personalizza soglia";

    @XPath(".//button[contains(@class, 'MuiIconButton-root') and contains(@aria-label, 'Rimuovi attributo')]")
    Button deleteButton();

    @XPath(".//button[contains(@class, 'MuiIconButton-root') and contains(@aria-label, 'Apri menù delle azioni')]")
    Button menuButton();

    @XPath("//ul[contains(@class, 'MuiMenu-list') and contains(@role, 'menu')]")
    Menu menu();

    @XPath(".//p")
    Readable<String> name();

    @XPath(".//p/following-sibling::*[contains(@class, 'MuiStack-root')]")
    Readable<String> thresholdInfo();

    // Button customizeThesholdButton();

    Dialog confirmThresholdValueDeletionDialog();

    default boolean isThresholdNotSet() {
        return thresholdInfo().read().equals(EMPTY_THRESHOLD_LABEL);
    }

    default void details() {
        // menu().find("Dettagli attributo").ifPresent(Clickable::click);
    }

    default void remove() {
        deleteButton().click();
    }

    default CertifiedAttribute removeThresholdValue() {
        menuButton().click();
        menu().find("Rimuovi soglia").ifPresent(Clickable::click);
        return this;
    }

    default CertifiedAttribute confirmThesholdValueDeletion() {
        confirmThresholdValueDeletionDialog().confirmBtn().click();
        return this;
    }}
