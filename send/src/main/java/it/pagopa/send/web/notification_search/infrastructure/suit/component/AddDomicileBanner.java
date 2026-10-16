package it.pagopa.send.web.notification_search.infrastructure.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import org.assertj.core.api.Assertions;

/**
 * Banner "Niente più documenti cartacei" della pagina "In arrivo", mostrato solo a chi non ha un domicilio digitale.
 * "Attiva domicilio digitale" apre il wizard di attivazione, "Chiudi" lo nasconde.
 */
public interface AddDomicileBanner extends Component {
    @XPath("//*[@data-testid=\"addDomicileBanner\"]//h6")
    Readable<String> title();

    @XPath("//*[@data-testid=\"addDomicileBanner\"]//p")
    Readable<String> description();

    @XPath("//*[@data-testid=\"addDomicileBanner\"]//button[normalize-space()=\"Attiva domicilio digitale\"]")
    Button activateButton();

    @XPath("//*[@data-testid=\"addDomicileBanner\"]//button[@aria-label=\"Chiudi\"]")
    Button closeButton();

    @Override
    default void assertLoaded() {
        title().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
    }
}
