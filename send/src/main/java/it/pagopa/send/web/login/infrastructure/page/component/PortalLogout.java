package it.pagopa.send.web.login.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.domain.Component;

import java.util.Optional;

/**
 * Uscita dal portale, uguale nel portale ente mittente, nel portale imprese e nel BackOffice PT: il pulsante "Esci"
 * dell'header apre la finestra "Confermi di voler uscire da Area Riservata?", che si conferma con "Esci".
 */
public interface PortalLogout extends Component {

    @XPath("//header//button[normalize-space()=\"Esci\"]")
    Clickable exitButton();

    @XPath("//*[@role=\"dialog\"]//*[@data-testid=\"confirm-button\"]")
    Clickable confirmButton();

    // il banner dei cookie può comparire dopo la pagina e coprire "Esci": va accettato prima di cliccarlo
    Optional<OneTrustBanner> oneTrustBanner();

    default void logout() {
        oneTrustBanner().ifPresent(OneTrustBanner::acceptIfShown);
        exitButton().click();
        confirmButton().click();
    }
}
