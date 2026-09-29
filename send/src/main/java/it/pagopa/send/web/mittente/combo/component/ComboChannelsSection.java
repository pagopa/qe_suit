package it.pagopa.send.web.mittente.combo.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import org.assertj.core.api.SoftAssertions;

import java.util.List;

@XPath("//div[contains(@class, 'MuiPaper-root') and .//h5[contains(., 'canale')]]")
public interface ComboChannelsSection extends Component {

    @XPath(".//h5")
    Readable<String> header();

    @XPath(".//ul[contains(@class, 'MuiList-root')]/li")
    List<ChannelItemComponent> channelItems();

    @XPath(".//li")
    interface ChannelItemComponent extends Component {

        @XPath(".//div[contains(@class, 'MuiListItemIcon-root')]/following-sibling::p")
        Readable<String> name();

        @XPath(".//span")
        Readable<String> statusBadge();

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



