package it.pagopa.send.web.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;

/**
 * Messaggio d'errore del portale, in basso a destra ({@code snackBarContainer} di pn-commons): compare quando una
 * chiamata al backend fallisce, con il codice dell'errore e il pulsante per chiuderlo. Il messaggio con i dati tecnici
 * dell'errore non si chiude da solo, quindi resta sopra i pulsanti finché non lo si chiude.
 */
@XPath("//*[@data-testid=\"snackBarContainer\"]")
public interface ErrorSnackbar extends Component {

    @XPath("//*[@data-testid=\"snackBarCloseButton\"]")
    Button closeButton();

    /**
     * Chiude il messaggio se è visibile. {@code exists()} legge il DOM al momento della chiamata, senza attendere un
     * messaggio assente.
     */
    default void closeIfShown() {
        if (closeButton().exists()) {
            closeButton().click();
        }
    }

    @Override
    default void assertLoaded() {
    }
}
