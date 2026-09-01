package it.pagopa.send.domain.web.pages.mittente;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.pagopa.send.web.notification_details.infrastructure.suit.NotificationDetailsPage;
import it.pagopa.send.web.notification_details.infrastructure.suit.component.NotificationStatusDrawer;
import it.pagopa.send.web.notification_details.infrastructure.suit.section.AttachmentSection;
import it.pagopa.send.web.notification_details.infrastructure.suit.section.NotificationStatusSection;
import it.pagopa.send.web.notification_details.infrastructure.suit.section.NotificationSummarySection;
import it.pagopa.send.web.notification_details.infrastructure.suit.section.PaymentSection;
import it.pagopa.suit.component.Chip;
import org.assertj.core.api.Assertions;

@Url("${url.notifiche.mittente.dashboard}#selfCareToken=${token.mittente}")
public interface MittenteNotificationDetailsPage extends NotificationDetailsPage {

    @XPath("//*[@id=\"title-of-page\"]")
    Readable<String> breadcrumbs();

    @XPath("//*[@id=\"notificationsTable.body.row\"]/td[7]/button")
    Readable<String> alertMessage();

    @XPath("//div[@id=\"page-header-container\"]/parent::div")
    interface MittenteNotificationSummarySection extends NotificationSummarySection {
//        @XPath("header iun")
        @XPath(".//h1")
        Readable<String> iunHeader();

//        @XPath("header protocol number")
        @XPath("//*[@id=\"title-of-page\"]")
        Readable<String> protocolNumberHeader();

//        @XPath("protocol number value")
        @XPath("//*[@id=\"title-of-page\"]")
        Readable<String> protocolNumberValue();

//        @XPath("header sender")
        @XPath("//*[@id=\"title-of-page\"]")
        Readable<String> senderHeader();

//        @XPath("sender value")
        @XPath("//*[@id=\"title-of-page\"]")
        Readable<String> senderValue();

//        @XPath("header recipient")
        @XPath("//*[@id=\"title-of-page\"]")
        Readable<String> recipientHeader();

//        @XPath("recipient value")
        @XPath("//*[@id=\"title-of-page\"]")
        Readable<String> recipientValue();

//        @XPath("open details sidebar")
        @XPath("//*[@id=\"title-of-page\"]")
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
        @XPath("//*[@id=\"title-of-page\"]")
        Readable<String> noticeCodeHeader();

//        @XPath("notice code value")
        @XPath("//*[@id=\"title-of-page\"]")
        Readable<String> noticeCodeValue();
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
        @XPath("//*[@id=\"title-of-page\"]")
        Readable<String> header();

        @Override
        @XPath("//*[@data-testid='NotificationDetailTimeline']//div[contains(@class, 'MuiChip-root')]")
        Chip statusChip();

//        @XPath("dettaglio stato notifica")
        @Override
        @XPath("//*[@id=\"title-of-page\"]")
        Readable<String> detailsMessage();

//        @XPath("griglia contenuti")
        @Override
        @XPath("//*[@id=\"title-of-page\"]")
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

    @Override
    default void assertLoaded() {
//        seeDetailsButton().click();
        breadcrumbs().readAndAssert((h) -> {
            Assertions.assertThat(h).isNotNull();
            Assertions.assertThat(h).isIn("Dettaglio notifica", "Notification details");
        });
    }


}
