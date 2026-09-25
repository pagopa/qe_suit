package it.pagopa.send.web.notification_details.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import it.pagopa.send.web.notification_details.infrastructure.suit.NotificationDetailsPage;
import it.pagopa.send.web.notification_details.infrastructure.suit.component.NotificationStatusDrawer;
import it.pagopa.send.web.notification_details.infrastructure.suit.section.AttachmentSection;
import it.pagopa.send.web.notification_details.infrastructure.suit.section.NotificationStatusSection;
import it.pagopa.send.web.notification_details.infrastructure.suit.section.NotificationSummarySection;
import it.pagopa.send.web.notification_details.infrastructure.suit.section.PaymentSection;
import it.pagopa.infrastructure.suit.component.Chip;
import org.assertj.core.api.Assertions;

import java.util.List;
import java.util.Optional;

@Url("${url.notifiche.mittente.dashboard}/${iun}/dettaglio")
public interface MittenteNotificationDetailsPage extends NotificationDetailsPage {

    @XPath("//*[@id=\"title-of-page\"]")
    Readable<String> breadcrumbs();

    @XPath("//*[@id=\"notificationsTable.body.row\"]/td[7]/button")
    Readable<String> alertMessage();

    @XPath("//div[@id=\"page-header-container\"]/parent::div")
    interface MittenteNotificationSummarySection extends NotificationSummarySection {
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
        Clickable openDetailsSidebarButton();

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

    interface MittentePaymentSection extends PaymentSection {
        //        @XPath("header notice code")
        @XPath("//h2[normalize-space(text())=\"Pagamenti\"]")
        Readable<String> noticeCodeHeader();

        //        @XPath("notice code value")
        @XPath("//div[@data-testid=\"paymentInfoBox\"]/p")
        Readable<String> noticeCodeValue();

        @XPath("//div[@data-testid=\"payment-item\"]")
        List<Button> paymentListButtons();

        @Override
        default void assertLoaded() {
            noticeCodeHeader().readAndAssert((h) -> {
                Assertions.assertThat(h).isNotNull();
            });
            noticeCodeValue().readAndAssert((v) -> {
                Assertions.assertThat(v).isNotNull();
            });

        }
    }

    @XPath("//h2[@id=\"notification-detail-document-attached\"]/ancestor::div[.//button[@data-testid=\"cancelNotificationBtn\"]][1]")
    interface MittenteAttachmentSection extends AttachmentSection {
        //        @XPath("header")
        @XPath("//h2[@id=\"notification-detail-document-attached\"]")
        Readable<String> header();

        //        @XPath("griglia contenuti")
        @XPath("//div[@data-testid=\"notificationDetailDocuments\"]//button[@data-testid=\"documentButton\"]/div/span")
        Readable<String> attachmentGrid();

        @Override
        default void assertLoaded() {
            header().readAndAssert((h) -> {
                Assertions.assertThat(h).isNotNull();
            });
            attachmentGrid().readAndAssert((g) -> {
                Assertions.assertThat(g).isNotNull();
            });
        }
    }

    @XPath(".//div[@data-testid='NotificationDetailTimeline']")
    interface MittenteNotificationStatusSection extends NotificationStatusSection {
        //        @XPath("header")
        @Override
        @XPath("//div[@data-testid=\"NotificationDetailTimeline\"]//h2")
        Readable<String> header();

        @Override
        @XPath("//div[@data-testid='NotificationDetailTimeline']")
        Chip statusChip();

        //        @XPath("dettaglio stato notifica")
        @Override
        @XPath("//div[@data-testid=\"NotificationDetailTimeline\"]//p")
        Readable<String> detailsMessage();

        //        @XPath("griglia contenuti")
        @Override
        @XPath("//button[@aria-label=\"Vai alla timeline della notifica\"]")
        Clickable detailsButton();

        @Override
        default void assertLoaded() {
            header().readAndAssert((h) -> {
                Assertions.assertThat(h).isNotNull();
            });
            statusChip().text().readAndAssert((t) -> {
                Assertions.assertThat(t).isNotNull();
            });
            detailsMessage().readAndAssert((m) -> {
                Assertions.assertThat(m).isNotNull();
            });
        }
    }

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
        oneTrustBanner().ifPresent(OneTrustBanner::accept);
    }




}