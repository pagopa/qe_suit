package it.pagopa.send.web.mittente.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import org.assertj.core.api.SoftAssertions;

@XPath("//div[@id='page-header-container']")
public interface ComboOverviewSection extends Component {

    @XPath(".//h1[@data-testid='titleBox' or self::h1]")
    Readable<String> title();

    @XPath(".//p[normalize-space()='IUN']/following-sibling::div[1]")
    Readable<String> iun();

    @XPath(".//p[normalize-space()='Persona destinataria' or normalize-space()='Destinatario']/following-sibling::div[1]")
    Readable<String> recipientName();

    // il pulsante sta sotto l'header, non dentro
    @XPath("//button[@aria-label='Vai al dettaglio della comunicazione']")
    Button openSidebarButton();

    @Override
    default void assertLoaded() {
        SoftAssertions softly = new SoftAssertions();
        title().readAndAssert(t -> softly.assertThat(t).as("Titolo comunicazione").isNotNull());
        softly.assertThatCode(() -> openSidebarButton().assertLoaded())
                .as("Pulsante sidebar dettaglio messaggio").doesNotThrowAnyException();
        softly.assertAll();
    }
}
