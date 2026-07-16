package it.pagopa.send.web.notification_details.infrastructure.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.pagopa.send.web.infrastructure.suit.component.Drawer;
import org.assertj.core.api.SoftAssertions;

@XPath("//div[contains(@class, 'MuiDrawer-root')][1]")
public interface NotificationStatusDrawer extends Drawer {
    @XPath(".//h6")
    Readable<String> title();

    @XPath("codice iun")
    Readable<String> iunHeader();

    @XPath("iun value")
    Readable<String> iunValue();

    @XPath("numero protocollo header")
    Readable<String> protocolNumberHeader();

    @XPath("numero protocollo value")
    Readable<String> protocolNumberValue();

    @XPath("oggetto della notifica header")
    Readable<String> notificationObjectHeader();

    @XPath("oggetto della notifica value")
    Readable<String> notificationObjectValue();

    @XPath("mittente header")
    Readable<String> senderHeader();

    @XPath("mittente value")
    Readable<String> senderValue();

    @XPath("persona destinataria header")
    Readable<String> recipientHeader();

    @XPath("persona destinatario value")
    Readable<String> recipientValue();

    @XPath("testo della notifica header")
    Readable<String> notificationTextHeader();

    @XPath("testo della notifica value")
    Readable<String> notificationTextValue();

    @Override
    default void assertLoaded() {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(title().read()).isNotBlank();
            softly.assertThat(iunValue().read()).isNotBlank();
            softly.assertThat(protocolNumberValue().read()).isNotBlank();
            softly.assertThat(notificationObjectValue().read()).isNotBlank();
            softly.assertThat(senderValue().read()).isNotBlank();
            softly.assertThat(recipientValue().read()).isNotBlank();
            softly.assertThat(notificationTextValue().read()).isNotBlank();
        });
    }
}
