package it.pagopa.send.web.mittente.combo.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import org.assertj.core.api.SoftAssertions;

@XPath("//div[@id='page-header-container']/parent::div | //div[@id='page-header-container']")
public interface ComboOverviewSection extends Component {

    @XPath("//h1[@data-testid='titleBox' and @role='heading'] | //h1[@data-testid='titleBox'] | //h1")
    Readable<String> title();

    @XPath(".//p[normalize-space()='IUN']/following-sibling::div[1] | //h1[@data-testid='titleBox'] | .//h1")
    Readable<String> iun();

    @XPath(".//p[normalize-space()='Persona destinataria' or normalize-space()='Destinatario']/following-sibling::div[1]")
    Readable<String> recipientName();

    @XPath(".//button[contains(., 'Vai al dettaglio') or contains(@aria-label, 'dettagli') or contains(@aria-label, 'Dettaglio')]")
    Button openSidebarButton();

    @Override
    default void assertLoaded() {
        SoftAssertions softly = new SoftAssertions();
        title().readAndAssert(t -> softly.assertThat(t).as("Titolo comunicazione").isNotNull());
        softly.assertThat(openSidebarButton()).as("Pulsante sidebar dettaglio messaggio").isNotNull();
        softly.assertAll();
    }
}

