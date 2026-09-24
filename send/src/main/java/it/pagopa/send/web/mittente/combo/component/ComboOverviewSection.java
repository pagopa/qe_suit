package it.pagopa.send.web.mittente.combo.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import org.assertj.core.api.SoftAssertions;

@XPath(".//div[contains(@class, 'AbstractPaper') or contains(@class, 'MuiPaper-root')][.//button[contains(@aria-label, 'dettagli del messaggio') or contains(., 'dettaglio') or contains(@aria-label, 'Dettaglio')]]")
public interface ComboOverviewSection extends Component {

    @XPath(".//h1 | .//h2 | .//span[@data-testid='iunTitle']")
    Readable<String> title();

    @XPath(".//p[contains(text(), 'IUN')] | .//span[contains(text(), 'IUN')] | .//h1")
    Readable<String> iun();

    @XPath(".//p[normalize-space()='Persona destinataria' or normalize-space()='Destinatario' or normalize-space()='Destinatari']/following-sibling::*")
    Readable<String> recipientName();

    @XPath(".//button[contains(@aria-label, 'dettagli del messaggio') or contains(., 'Vai al dettaglio') or contains(@aria-label, 'Dettaglio')]")
    Button openSidebarButton();

    @Override
    default void assertLoaded() {
        SoftAssertions softly = new SoftAssertions();
        title().readAndAssert(t -> softly.assertThat(t).as("Titolo comunicazione").isNotNull());
        softly.assertThat(openSidebarButton()).as("Pulsante sidebar dettaglio messaggio").isNotNull();
        softly.assertAll();
    }
}

