package it.pagopa.interop.common.infrastructure.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Menu;

@XPath("./li[contains(@class, 'MuiBox-root')]")
public interface CertifiedAttribute extends Component, Clickable {

    @XPath("./button[contains(@class, 'MuiIconButton-sizeMedium') and starts-with(@aria-label, 'Rimuovi attributo')]")
    Button deleteButton();

    Menu menu();

    Readable<String> name();

    default void details() {
        menu().find("Dettagli attributo").ifPresent(Clickable::click);
    }

    default void remove() {
        deleteButton().click();
    }

    Readable<String> thresholdInfo();

    Button customizeThesholdButton();

    default void removeThresholdValue() {
        menu().find("Rimuovi soglia").ifPresent(Clickable::click);
    }
}
