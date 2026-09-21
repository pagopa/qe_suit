package it.pagopa.send.web.mittente.combo.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import org.assertj.core.api.SoftAssertions;

import java.util.List;

// TODO: Confirm locator against FE implementation
@XPath(".//*[@data-testid='combo-channels-section']")
public interface ComboChannelsSection extends Component {

    @XPath(".//h2[contains(text(), 'Canali Abilitati')]")
    Readable<String> header();

    @XPath(".//div[@data-testid='channel-item']")
    List<ChannelItemComponent> channelItems();

    // TODO: Confirm locator against FE implementation
    @XPath(".//div[@data-testid='channel-item']")
    interface ChannelItemComponent extends Component {

        @XPath(".//span[@data-testid='channel-name']")
        Readable<String> name();

        @XPath(".//*[self::span or self::div][contains(text(), 'IN ATTESA') or contains(text(), 'DEPOSITATA') or contains(text(), 'INVIATA') or contains(text(), 'NON DISPONIBILE') or contains(text(), 'CONSEGNATA') or contains(text(), 'LETTA')]")
        Readable<String> statusBadge();

        @Override
        default void assertLoaded() {
            SoftAssertions softly = new SoftAssertions();
            name().readAndAssert(n -> softly.assertThat(n).as("Nome Canale").isNotNull());
            statusBadge().readAndAssert(s -> softly.assertThat(s).as("Badge Stato Canale").isNotNull());
            softly.assertAll();
        }
    }

    @Override
    default void assertLoaded() {
        SoftAssertions softly = new SoftAssertions();
        header().readAndAssert(h -> softly.assertThat(h).as("Header Sezione Canali").isNotNull());
        softly.assertThat(channelItems()).as("Lista Canali").isNotEmpty();
        softly.assertAll();
    }
}
