package it.pagopa.send.web.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;

/**
 * Messaggio d'errore del portale, in basso a destra ({@code snackBarContainer} di pn-commons): compare quando una
 * chiamata al backend fallisce, con il codice dell'errore e il pulsante per chiuderlo.
 */
@XPath("//*[@data-testid=\"snackBarContainer\"]")
public interface ErrorSnackbar extends Component {

    String CLOSE_BUTTON_TEST_ID = "snackBarCloseButton";
    long SNACKBAR_WAIT_MILLIS = 5000;
    long SNACKBAR_POLL_MILLIS = 250;

    @XPath("//*[@data-testid=\"snackBarCloseButton\"]")
    Button closeButton();

    // il pulsante se c'è, altrimenti il body: la ricerca trova sempre un elemento e non attende un messaggio assente
    @XPath("//*[@data-testid=\"snackBarCloseButton\"] | //body[not(//*[@data-testid=\"snackBarCloseButton\"])]")
    Button closeButtonOrBody();

    /**
     * Chiude il messaggio se compare. Il messaggio arriva dopo la risposta del backend, quindi lo si attende per al
     * massimo {@value #SNACKBAR_WAIT_MILLIS} ms, invece dei 60 secondi della ricerca di un elemento assente.
     */
    default void closeIfShown() {
        long deadline = System.currentTimeMillis() + SNACKBAR_WAIT_MILLIS;
        while (true) {
            boolean shown = closeButtonOrBody().get(FindPolicy.PRESENT)
                    .map(element -> CLOSE_BUTTON_TEST_ID.equals(element.getAttributes().get("data-testid")))
                    .orElse(false);
            if (shown) {
                closeButton().click();
                return;
            }
            if (System.currentTimeMillis() >= deadline) {
                return;
            }
            try {
                Thread.sleep(SNACKBAR_POLL_MILLIS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    @Override
    default void assertLoaded() {
    }
}
