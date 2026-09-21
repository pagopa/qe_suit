package it.pagopa.send.web.mittente.combo.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import org.assertj.core.api.SoftAssertions;

// TODO: Confirm locator against FE implementation
@XPath(".//*[@data-testid='combo-overview']")
public interface ComboOverviewSection extends Component {

    @XPath(".//h1[@data-testid='combo-title']")
    Readable<String> title();

    @XPath(".//p[@data-testid='combo-iun']")
    Readable<String> iun();

    @XPath(".//p[@data-testid='combo-recipient-name']")
    Readable<String> recipientName();

    @XPath(".//button[contains(., 'Vai al dettaglio')]")
    Button openSidebarButton();

    @Override
    default void assertLoaded() {
        SoftAssertions softly = new SoftAssertions();
        title().readAndAssert(t -> softly.assertThat(t).as("Titolo comunicazione").isNotNull());
        iun().readAndAssert(i -> softly.assertThat(i).as("IUN comunicazione").isNotNull());
        recipientName().readAndAssert(r -> softly.assertThat(r).as("Nome destinatario").isNotNull());
        softly.assertThat(openSidebarButton()).as("Pulsante sidebar dettaglio").isNotNull();
        softly.assertAll();
    }
}

