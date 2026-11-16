package it.pagopa.send.web.destinatario_pf.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import org.assertj.core.api.Assertions;

/**
 * Domicilio digitale da attivare: i tre vantaggi e il pulsante "Inizia", che apre il wizard di attivazione.
 */
public interface DigitalDomicileToActivate extends Component {
    @XPath("//*[@data-testid=\"legalContacts\"]//*[@data-testid=\"PnInfoCardBody\"]/button")
    Button startButton();

    // labels

    @XPath("//*[@data-testid=\"legalContacts\"]//*[@data-testid=\"PnInfoCardBody\"]/p[1]")
    Readable<String> whyUseful();

    @XPath("//*[@data-testid=\"legalContacts\"]//*[@data-testid=\"PnInfoCardBody\"]/div//p[1]")
    Readable<String> benefitTitles();

    @XPath("//*[@data-testid=\"legalContacts\"]//*[@data-testid=\"PnInfoCardBody\"]/div//p[2]")
    Readable<String> benefitDescriptions();

    @Override
    default void assertLoaded() {
        whyUseful().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
    }
}
