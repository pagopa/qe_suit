package it.pagopa.send.web.notification_details.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import it.pagopa.send.web.notification_details.infrastructure.page.component.NotificationDetailsDrawer;
import it.pagopa.send.web.notification_details.infrastructure.suit.NotificationDetailsPage;
import it.pagopa.send.web.notification_details.infrastructure.page.section.MittenteNotificationStatusSection;
import it.pagopa.send.web.notification_details.infrastructure.page.section.MittenteAttachmentSection;
import it.pagopa.send.web.notification_details.infrastructure.page.section.MittentePaymentSection;
import it.pagopa.send.web.notification_details.infrastructure.page.section.MittenteNotificationSummarySection;

import org.assertj.core.api.Assertions;

import java.util.Optional;

/**
 * {@code {baseUrl}/dashboard/:iun/dettaglio}
 * Pagina di dettaglio di una notifica inviata dalla PA: riepilogo con l'IUN come titolo, documenti allegati, pulsante
 * "Annulla notifica" (solo per gli amministratori), stato della notifica e sezione "Dettaglio della notifica".
 * "Vai al dettaglio" del riepilogo apre il pannello con tutti i dati della notifica.
 * Testi e stato iniziale sono verificati da {@code WebNotificationDetailPaContractTest}.
 */
@Url("${url.notifiche.mittente.dashboard}/${iun}/dettaglio")
public interface MittenteNotificationDetailsPage extends NotificationDetailsPage {

    @XPath("//*[@id=\"title-of-page\"]")
    Readable<String> breadcrumbs();

    @XPath("//*[@id=\"notificationsTable.body.row\"]/td[7]/button")
    Readable<String> alertMessage();

    @XPath("//*[@data-testid=\"breadcrumb-root-button\"]")
    Readable<String> notificationsBreadcrumb();

    @XPath("//*[@data-testid=\"breadcrumb-root-button\"]/ancestor::ol/li[last()]")
    Readable<String> currentBreadcrumb();

    @XPath("//*[@data-testid=\"cancelNotificationBtn\"]")
    Button cancelNotificationButton();

    // titolo della sezione "Dettaglio della notifica" con l'avviso di avvenuta ricezione
    @XPath("//*[@data-testid=\"aarDownload\"]//h2")
    Readable<String> notificationDetailSectionTitle();

    @XPath("//*[@data-testid=\"notificationDetailsDrawer\"]")
    NotificationDetailsDrawer notificationDetailsDrawer();

    @Override
    MittenteNotificationSummarySection notificationSummarySection();

    @Override
    MittentePaymentSection paymentSection();

    @Override
    MittenteAttachmentSection attachmentSection();

    @Override
    MittenteNotificationStatusSection notificationStatusSection();

    Optional<OneTrustBanner> oneTrustBanner();

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::acceptIfShown);
        notificationSummarySection().iunHeader().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
    }

}