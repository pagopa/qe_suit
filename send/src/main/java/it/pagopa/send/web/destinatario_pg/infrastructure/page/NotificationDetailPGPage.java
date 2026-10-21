package it.pagopa.send.web.destinatario_pg.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import org.assertj.core.api.Assertions;

/**
 * {@code {baseUrl}/notifiche/<IUN>/dettaglio}
 * Pagina di dettaglio di una notifica a valore legale della persona giuridica.
 * Si apre dal pulsante "Apri" di una riga della pagina "In arrivo per &lt;impresa&gt;" ({@link NotificationPage}) oppure
 * direttamente dall'URL, con lo IUN della notifica come parametro.
 * L'assertLoaded verifica che la pagina sia caricata, cioè il titolo (l'oggetto della notifica), il breadcrumb e lo IUN.
 * Testi e sezioni presenti in ogni notifica sono verificati da {@code WebNotificationDetailPGContractTest}.
 */
@Url("${url.notifiche.persona-giuridica.notifiche-dettaglio}")
public interface NotificationDetailPGPage extends Page {

    // breadcrumb: "In arrivo per <impresa>" > oggetto della notifica

    @XPath("//*[@data-testid=\"breadcrumb-root-button\"]")
    Readable<String> notificationsBreadcrumb();

    @XPath("//*[@data-testid=\"breadcrumb-root-button\"]/ancestor::ol/li[last()]")
    Readable<String> currentBreadcrumb();

    // riepilogo della notifica

    // etichetta prima del titolo, per le notifiche a valore legale
    @XPath("//*[@id=\"page-header-container\"]/preceding-sibling::div[1]")
    Readable<String> legalValueChip();

    @XPath("//*[@data-testid=\"titleBox\"]")
    Readable<String> title();

    // logo, nome del mittente e data di deposito
    @XPath("//p[contains(normalize-space(),\"depositata il giorno\")]/../preceding-sibling::div[1]")
    Component senderLogo();

    @XPath("//p[contains(normalize-space(),\"depositata il giorno\")]/preceding-sibling::span[1]")
    Readable<String> sender();

    @XPath("//p[contains(normalize-space(),\"depositata il giorno\")]")
    Readable<String> depositDate();

    @XPath("//p[normalize-space()=\"Codice IUN\"]")
    Readable<String> iunLabel();

    @XPath("//p[normalize-space()=\"Codice IUN\"]/following-sibling::p[1]")
    Readable<String> iun();

    // ultima riga del riepilogo, sotto la descrizione della notifica
    @XPath("(//*[@data-testid=\"titleBox\"]/ancestor::div[contains(@class,\"MuiPaper-root\")][1]//p)[last()]")
    Readable<String> legalDisclaimer();

    // sezioni

    @XPath("//*[@id=\"notification-detail-document-attached\"]")
    Readable<String> documentsTitle();

    @XPath("//*[@data-testid=\"NotificationDetailTimeline\"]//h2")
    Readable<String> statusTitle();

    // stato più recente della notifica, es. "Avvenuto accesso"
    @XPath("//*[@data-testid=\"NotificationDetailTimeline\"]//h2/following::span[1]")
    Readable<String> currentStatus();

    // apre la pagina "Stato della notifica"
    @XPath("//*[@data-testid=\"NotificationDetailTimeline\"]//button")
    Button timelineDetailsButton();

    @XPath("//*[@data-testid=\"aarDownload\"]//h2")
    Readable<String> notificationDetailsTitle();

    // pagina "Stato della notifica" aperta con "Vai al dettaglio"
    @XPath("//main")
    NotificationTimelinePGPage notificationTimeline();

    @Override
    default void assertLoaded() {
        title().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        notificationsBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        iun().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
    }
}
