package it.pagopa.send.web.mittente.combo.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import org.assertj.core.api.SoftAssertions;

import java.util.List;

@XPath("//div[contains(@class, 'MuiPaper-root')][.//h2[contains(., 'Canali') or contains(., 'Dettaglio invio per canale')] or .//h5[contains(., 'Canali') or contains(., 'Dettaglio invio per canale')]] | //div[@data-testid='channels-section']")
public interface ComboChannelsSection extends Component {

    @XPath(".//h2 | .//h5 | //h2[contains(., 'Canali')] | //h5[contains(., 'Canali')]")
    Readable<String> header();

    @XPath(".//li[contains(@class, 'MuiListItem-root')] | .//ul/li | .//ol/li | .//div[@component='li']")
    List<ChannelItemComponent> channelItems();

    @XPath(".//li | .//div[contains(@class, 'channel-item')]")
    interface ChannelItemComponent extends Component {

        @XPath(".//span[contains(@class, 'channel-name')] | .//p | .//span")
        Readable<String> name();

        @XPath(".//*[contains(@class, 'MuiChip-root')] | .//*[self::span or self::div or self::p][contains(text(), 'IN ATTESA') or contains(text(), 'DEPOSITATA') or contains(text(), 'INVIATA') or contains(text(), 'NON DISPONIBILE') or contains(text(), 'CONSEGNATA') or contains(text(), 'LETTA') or contains(text(), 'NON CONSEGNATA') or contains(text(), 'ARCHIVIATA') or contains(text(), 'In attesa') or contains(text(), 'Depositata') or contains(text(), 'Inviata') or contains(text(), 'Consegnata') or contains(text(), 'Letta') or contains(text(), 'Non disponibile') or contains(text(), 'Archiviata')]")
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
