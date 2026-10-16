package it.pagopa.interop.common.infrastructure.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Menu;

@XPath("//*[id='TODO']")
public interface VerifiedAttribute extends Component, Clickable {

    @XPath("//*[id='TODO']")
    Button deleteButton();

    @XPath("//*[id='TODO']")
    Button menuButton();

    @XPath("//*[id='TODO']")
    Menu menu();

    @XPath("//*[id='TODO']")
    Readable<String> name();

    default void details() {
        menu().find("Dettagli attributo").ifPresent(Clickable::click);
    }

    default void remove() {
        deleteButton().click();
    }
}


