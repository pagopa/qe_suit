package it.pagopa.send.web.mittente.combo.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Chip;
import org.assertj.core.api.SoftAssertions;

@XPath(".//div[contains(@class, 'NotificationStatusBox') or (contains(@class, 'MuiPaper-root') and .//button[contains(., 'cronologia') or contains(., 'timeline')])]")
public interface ComboStatusSection extends Component {

    @XPath(".//h5 | .//h2")
    Readable<String> header();

    @XPath(".//*[contains(@class, 'MuiChip-root')] | .//span[contains(@class, 'status')]")
    Chip statusChip();

    @XPath(".//button[contains(., 'Vai alla cronologia') or contains(., 'timeline') or contains(., 'Cronologia')]")
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

