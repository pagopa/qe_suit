package it.pagopa.send.web.mittente.combo.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Chip;
import org.assertj.core.api.SoftAssertions;

@XPath("//div[@data-testid='NotificationDetailTimeline']")
public interface ComboStatusSection extends Component {

    @XPath(".//h2 | .//h6 | .//h5")
    Readable<String> header();

    @XPath(".//*[contains(@class, 'MuiChip-root')]")
    Chip statusChip();

    @XPath(".//button[@aria-label='Vai al dettaglio dello stato della comunicazione' or contains(., 'Vai al dettaglio')]")
    Button openTimelineButton();

    @Override
    default void assertLoaded() {
        SoftAssertions softly = new SoftAssertions();
        header().readAndAssert(h -> softly.assertThat(h).as("Header Sezione Stato").isNotNull());
        statusChip().text().readAndAssert(s -> softly.assertThat(s).as("Chip Stato Comunicazione").isNotNull());
        softly.assertThat(openTimelineButton()).as("Pulsante Vai alla Timeline").isNotNull();
        softly.assertAll();
    }
}


