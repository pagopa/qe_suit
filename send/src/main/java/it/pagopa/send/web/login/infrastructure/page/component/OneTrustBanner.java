package it.pagopa.send.web.login.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;

@XPath("//*[@id=\"onetrust-banner-sdk\"]")
public interface OneTrustBanner extends Component {

    String ACCEPT_BUTTON_ID = "onetrust-accept-btn-handler";
    long BANNER_WAIT_MILLIS = 5000;
    long BANNER_POLL_MILLIS = 250;

    @XPath(".//*[@id=\"onetrust-accept-btn-handler\"]")
    Button acceptButton();

    @XPath(".//*[@id=\"onetrust-reject-all-handler\"]")
    Clickable rejectButton();

    default void accept(){
        acceptButton().click();
    }

    // il pulsante se c'è, altrimenti il body: la ricerca trova sempre un elemento e non attende un banner assente
    @XPath("//*[@id=\"onetrust-banner-sdk\"]//*[@id=\"onetrust-accept-btn-handler\"]"
            + " | //body[not(//*[@id=\"onetrust-banner-sdk\"]//*[@id=\"onetrust-accept-btn-handler\"])]")
    Button acceptButtonOrBody();

    /**
     * Accetta i cookie se il pulsante è visibile. Il banner si carica dopo la pagina, quindi lo si attende per al
     * massimo {@value #BANNER_WAIT_MILLIS} ms, invece dei 60 secondi della ricerca di un elemento assente.
     * Il body mantiene risolvibile la ricerca quando il banner non c'è; il pulsante può restare nel DOM nascosto, e
     * Selenium ne restituisce il testo solo se è visibile.
     */
    default void acceptIfShown(){
        long deadline = System.currentTimeMillis() + BANNER_WAIT_MILLIS;
        while (true) {
            var button = acceptButtonOrBody().get(FindPolicy.PRESENT)
                    .filter(element -> ACCEPT_BUTTON_ID.equals(element.getAttributes().get("id")));
            if (button.isPresent()) {
                String text = button.get().getText();
                if (text != null && !text.isBlank()) {
                    acceptButton().click();
                }
                return;
            }
            if (System.currentTimeMillis() >= deadline) {
                return;
            }
            try {
                Thread.sleep(BANNER_POLL_MILLIS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    default void reject(){
        rejectButton().click();
    }
}
