package it.pagopa.send.web.notification_details.infrastructure.page.section;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.send.web.notification_details.infrastructure.suit.component.NotificationStatusDrawer;
import it.pagopa.send.web.notification_details.infrastructure.suit.section.NotificationSummarySection;
import org.assertj.core.api.Assertions;

/**
 * Riepilogo della notifica nel dettaglio della PA: tag "Notifica a valore legale", IUN come titolo, dati principali
 * (numero di protocollo, oggetto, mittente, destinatario) e "Vai al dettaglio", che apre il pannello con tutti i dati.
 */
@XPath("//div[@id=\"page-header-container\"]/parent::div")
public interface MittenteNotificationSummarySection extends NotificationSummarySection {
    //        @XPath("header iun")
    @XPath("//h1[@data-testid=\"titleBox\" and @role=\"heading\"]")
    Readable<String> iunHeader();

    //        @XPath("header protocol number")
    @XPath(".//p[normalize-space()='Numero protocollo']")
    Readable<String> protocolNumberHeader();

    //        @XPath("protocol number value")
    @XPath(".//p[normalize-space()='Numero protocollo']/following-sibling::div[1]")
    Readable<String> protocolNumberValue();

    //        @XPath("header sender")
    @XPath(".//p[normalize-space()='Mittente']")
    Readable<String> senderHeader();

    //        @XPath("sender value")
    @XPath(".//p[normalize-space()='Mittente']/following-sibling::div[1]")
    Readable<String> senderValue();

    //        @XPath("header recipient")
    @XPath(".//p[normalize-space()='Persona destinataria']")
    Readable<String> recipientHeader();

    //        @XPath("recipient value")
    @XPath(".//p[normalize-space()='Persona destinataria']/following-sibling::div[1]")
    Readable<String> recipientValue();

    //        @XPath("open details sidebar")
    @XPath(".//div[@id=\"page-header-container\"]/following::button[.//text()[contains(., \"Vai al dettaglio\")]][1]")
    Button openDetailsSidebarButton();

    // riepilogo nel riquadro del titolo: etichetta "Notifica a valore legale", etichette e valori dei dati
    @XPath("//h1[@data-testid=\"titleBox\"]/ancestor::div[.//hr][1]/*[1]")
    Readable<String> legalValueTag();

    @XPath("//h1[@data-testid=\"titleBox\"]/ancestor::div[.//hr][1]//p[contains(@class, 'MuiTypography-body2')]")
    Readable<String> detailLabels();

    @XPath("//h1[@data-testid=\"titleBox\"]/ancestor::div[.//hr][1]//div[contains(@class, 'MuiTypography-body2')]")
    Readable<String> detailValues();

    NotificationStatusDrawer notificationStatusDrawer();

    @Override
    default void assertLoaded() {
        iunHeader().readAndAssert((h) -> {
            Assertions.assertThat(h).isNotNull();
        });
        protocolNumberHeader().readAndAssert((h) -> {
            Assertions.assertThat(h).isNotNull();
        });
        protocolNumberValue().readAndAssert((v) -> {
            Assertions.assertThat(v).isNotNull();
        });
        senderHeader().readAndAssert((h) -> {
            Assertions.assertThat(h).isNotNull();
        });
        senderValue().readAndAssert((v) -> {
            Assertions.assertThat(v).isNotNull();
        });
        recipientHeader().readAndAssert((h) -> {
            Assertions.assertThat(h).isNotNull();
        });
        recipientValue().readAndAssert((v) -> {
            Assertions.assertThat(v).isNotNull();
        });
    }
}
