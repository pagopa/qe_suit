package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.component.NotificationDetailsSection;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.component.PfNotificationStatusSection;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.component.PfPaymentSection;
import it.pagopa.send.web.login.infrastructure.page.DowntimeItem;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Chip;
import it.pagopa.send.web.notification_details.infrastructure.suit.NotificationDetailsPage;
import it.pagopa.send.web.notification_details.infrastructure.suit.section.AttachmentSection;
import org.assertj.core.api.Assertions;

import java.util.Optional;

/**
 * {@code {baseUrl}/notifiche/<IUN>/dettaglio}
 * Pagina di dettaglio di una notifica del cittadino.
 * Si apre dal pulsante "Apri" di una riga della pagina {@code {baseUrl}/notifiche} oppure direttamente dall'URL, con lo
 * IUN della notifica come parametro.
 * Il contenuto cambia con il tipo di notifica: una notifica a valore legale ha documenti, stato, avviso di avvenuta
 * ricezione e disservizi; una comunicazione ({@code {baseUrl}/comunicazione/<IUN>/dettaglio}) ha messaggio, documenti,
 * pagamenti e contatti del mittente. Le sezioni che implementano {@link NotificationDetailsPage} sono condivise con gli
 * step Cucumber.
 * L'assertLoaded verifica che la pagina sia caricata, cioè l'oggetto, il breadcrumb e lo IUN. La pagina esiste solo se
 * l'utente ha delle notifiche, quindi non ha un contract test: va verificata con dei test di business.
 */
@Url("${url.notifiche.cittadino.notifiche-dettaglio}")
public interface NotificationDetailsPFPage extends NotificationDetailsPage {

    @XPath("//*[@data-testid=\"breadcrumb-root-button\"]")
    Button notificationsBreadcrumb();

    @XPath("//*[@data-testid=\"titleBox\"]")
    Readable<String> title();

    @XPath("//*[@data-testid=\"NotificationDetailTimeline\"]//button[normalize-space()=\"Vai al dettaglio\"]")
    Button timelineDetailsButton();

    @XPath("//main")
    NotificationTimelinePFPage notificationTimeline();

    // il banner dei cookie copre "Vai al dettaglio": va accettato prima di cliccarlo
    Optional<OneTrustBanner> oneTrustBanner();

    // labels

    @XPath("//*[@data-testid=\"breadcrumb-root-button\"]/ancestor::ol/li[last()]")
    Readable<String> currentBreadcrumb();

    @XPath("//p[contains(normalize-space(),\"depositata il giorno\")]/preceding-sibling::span[1]")
    Readable<String> sender();

    @XPath("//p[contains(normalize-space(),\"depositata il giorno\")]")
    Readable<String> depositDate();

    @XPath("//p[normalize-space()=\"Codice IUN\"]")
    Readable<String> iunLabel();

    @XPath("//p[normalize-space()=\"Codice IUN\"]/following-sibling::p[1]")
    Readable<String> iun();

    // contenuto della pagina, per sapere quali sezioni ci sono senza attenderle
    @XPath("//main")
    Readable<String> content();

    // documenti allegati

    @XPath("//*[@id=\"notification-detail-document-attached\"]")
    Readable<String> documentsTitle();

    @XPath("//*[@data-testid=\"documentsMessage\"]")
    Readable<String> documentsMessage();

    @XPath("//*[@data-testid=\"notificationDetailDocuments\"]//*[@data-testid=\"documentButton\"]")
    Readable<String> documentButtons();

    // notifica a valore legale: stato, avviso di avvenuta ricezione e disservizi

    @XPath("//*[@data-testid=\"NotificationDetailTimeline\"]//h2")
    Readable<String> statusTitle();

    @XPath("//*[@data-testid=\"NotificationDetailTimeline\"]//h2/following::span[1]")
    Readable<String> currentStatus();

    @XPath("//*[@data-testid=\"aarDownload\"]//h2")
    Readable<String> aarTitle();

    @XPath("//*[@data-testid=\"aarBox\"]//p")
    Readable<String> aarLabel();

    @XPath("//*[@data-testid=\"downtimesBox\"]//h2")
    Readable<String> downtimesTitle();

    // comunicazione: messaggio, pagamenti e contatti del mittente

    @XPath("(//*[@data-testid=\"informalNotificationMessage\"]//p)[1]")
    Readable<String> communicationGreeting();

    @XPath("//p[starts-with(normalize-space(),\"Questa comunicazione potrebbe produrre effetti giuridici\")]")
    Readable<String> legalEffectsNote();

    @XPath("//*[@data-testid=\"notification-payment-recipient-title\"]")
    Readable<String> paymentsTitle();

    // non va premuto: avvia il pagamento
    @XPath("//*[@data-testid=\"pay-button\"]")
    Button payButton();

    @XPath("//h2[normalize-space()=\"Contatta il mittente\"]")
    Readable<String> contactSenderTitle();

    // etichette dei contatti forniti dal mittente ("Numero di telefono dell'ente", "Sito web dell'ente", ...)
    @XPath("//h2[normalize-space()=\"Contatta il mittente\"]/following-sibling::*//p[contains(normalize-space(),\"dell'ente\")]")
    Readable<String> contactSenderLabels();

    @XPath("//*[@id=\"title-of-page\"]")
    Chip fullPecMessage();

    @XPath("//*[@id=\"title-of-page\"]")
    Chip notificationCancelledMessage();

    interface AttachmentDocumentSection extends AttachmentSection {
        @XPath("//*[@id=\"notification-detail-document-attached\"]")
        Readable<String> header();

        @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div[2]/div/div[2]/div[1]/div/div[1]/div/p")
        Readable<String> message();

        @XPath("//*[@id=\"document-button\"]")
        Readable<String> file();

        @XPath("//*[@id=\"item\"]")
        Chip raddMessage();
    }

    interface NotificationAARDetailsSection extends Component {
        @XPath("//*[@id=\"item\"]")
        Readable<String> header();

        @XPath("//*[@id=\"item\"]")
        Readable<String> message();
    }

    interface FacSimileSection extends Component {
        @XPath("//*[@id=\"item\"]")
        Readable<String> header();

        @XPath("//*[@id=\"item\"]")
        Readable<String> message();
    }

    interface DowntimeSection extends Component {
        @XPath("//*[@id=\"item\"]")
        Readable<String> header();

        @XPath("//*[@id=\"item\"]")
        Readable<DowntimeItem> items();

        @Override
        default void assertLoaded() {
            header().readAndAssert(h -> Assertions.assertThat(h).isEqualToIgnoringCase("Disservizi"));
        }
    }

    @Override
    NotificationDetailsSection notificationSummarySection();

    @Override
    PfPaymentSection paymentSection();

    @Override
    AttachmentDocumentSection attachmentSection();

    @Override
    PfNotificationStatusSection notificationStatusSection();

    NotificationAARDetailsSection notificationAARDetailsSection();

    FacSimileSection facsimileSection();

    DowntimeSection downtimeSection();

    @Override
    default void assertLoaded() {
        title().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        notificationsBreadcrumb().assertLoaded();
        iun().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
    }

}
