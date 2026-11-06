package it.pagopa.send.web.login.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.domain.Component;

/**
 * Uscita dal portale, uguale nel portale ente mittente, nel portale imprese e nel BackOffice PT: il pulsante "Esci"
 * dell'header apre la finestra "Confermi di voler uscire da Area Riservata?", che si conferma con "Esci".
 */
public interface PortalLogout extends Component {

    @XPath("//header//button[normalize-space()=\"Esci\"]")
    Clickable exitButton();

    @XPath("//*[@role=\"dialog\"]//*[@data-testid=\"confirm-button\"]")
    Clickable confirmButton();

    default void logout() {
        exitButton().click();
        confirmButton().click();
    }
}
