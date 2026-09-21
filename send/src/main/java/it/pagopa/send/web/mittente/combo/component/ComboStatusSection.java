package it.pagopa.send.web.mittente.combo.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Chip;
import org.assertj.core.api.SoftAssertions;

// TODO: Confirm locator against FE implementation
@XPath(".//*[@data-testid='combo-status-section']")
public interface ComboStatusSection extends Component {

    @XPath(".//h2[contains(text(), 'Stato')]")
    Readable<String> header();

    @XPath(".//*[contains(@class, 'MuiChip-root')] | .//*[@data-testid='combo-status-chip']")
    Chip statusChip();

    @XPath(".//a[contains(., 'Vai alla timeline')] | .//button[contains(., 'Vai alla timeline')]")
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

