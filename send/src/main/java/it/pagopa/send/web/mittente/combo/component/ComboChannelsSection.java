package it.pagopa.send.web.mittente.combo.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import org.assertj.core.api.SoftAssertions;

import java.util.List;

@XPath("//div[contains(@class, 'MuiPaper-root')][.//h2[contains(., 'Canali') or contains(., 'Dettaglio invio per canale')] or .//h5[contains(., 'Canali') or contains(., 'Dettaglio invio per canale')]]")
public interface ComboChannelsSection extends Component {

    @XPath(".//*[self::h2 or self::h5]")
    Readable<String> header();

    @XPath(".//li[contains(@class, 'MuiListItem-root')]")
    List<ChannelItemComponent> channelItems();

    @XPath(".")
    interface ChannelItemComponent extends Component {

        @XPath(".//p[contains(@class, 'MuiTypography-body2') or contains(@class, 'css-nxuris') or contains(@class, 'channel-name')]")
        Readable<String> name();

        @XPath(".//div[contains(@class, '1vn2o2m') or contains(@class, 'MuiChip-root')]//span[contains(@class, 'css-0') or contains(@class, 'MuiChip-label') or not(*)]")
        Readable<String> statusBadge();

        @XPath(".//span[contains(@class, 'MuiTypography-caption') or contains(@class, 'css-1qg6hcb')]")
        Readable<String> description();

        @Override
        default void assertLoaded() {
            SoftAssertions softly = new SoftAssertions();
            name().readAndAssert(n -> softly.assertThat(n).as("Nome Canale").isNotNull());
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

