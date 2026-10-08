package it.pagopa.send.web.login.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;

@XPath("//*[@id=\"onetrust-banner-sdk\"]")
public interface OneTrustBanner extends Component {

    @XPath(".//*[@id=\"onetrust-accept-btn-handler\"]")
    Button acceptButton();

    @XPath(".//*[@id=\"onetrust-reject-all-handler\"]")
    Clickable rejectButton();

    default void accept(){
        acceptButton().click();
    }

    /**
     * Accetta i cookie solo se il banner è mostrato, senza attendere: {@link #accept()} riprova il clic per 60 secondi
     * quando il banner è già stato chiuso, ad esempio dopo un cambio di pagina nella stessa sessione.
     * Il pulsante può restare nel DOM nascosto, e Selenium ne restituisce il testo solo se è visibile.
     */
    default void acceptIfShown(){
        acceptButton().get(FindPolicy.PRESENT)
                .filter(button -> button.getText() != null && !button.getText().isBlank())
                .ifPresent(button -> acceptButton().click());
    }

    default void reject(){
        rejectButton().click();
    }
}
