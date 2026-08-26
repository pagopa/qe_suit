package it.pagopa.send.domain.web.pages.mittente;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.send.web.infrastructure.suit.component.Chip;
import it.pagopa.send.web.notification_details.infrastructure.suit.NotificationDetailsPage;
import it.pagopa.send.web.notification_details.infrastructure.suit.component.NotificationStatusDrawer;
import org.assertj.core.api.Assertions;

@Url("${url.notifiche.mittente.dashboard}#selfCareToken=${token.mittente}")
public interface MittenteNotificationDetailsPage extends NotificationDetailsPage {

    @XPath("//*[@id=\"title-of-page\"]")
    Readable<String> breadcrumbs();

    @XPath("//*[@id=\"notificationsTable.body.row\"]/td[7]/button")
    Readable<String> alertMessage();

    @XPath("//div[contains(concat(' ', normalize-space(@class), ' '), ' MuiButton-root ')]"+
    "[contains(concat(' ', normalize-space(@class), ' '), ' MuiButton-root ')]")
    interface NotificationSummarySection extends Component {
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

    }

    interface PaymentSection extends Component {
//        @XPath("header notice code")
        @XPath("//*[@id=\"title-of-page\"]")
        Readable<String> noticeCodeHeader();

//        @XPath("notice code value")
        @XPath("//*[@id=\"title-of-page\"]")
        Readable<String> noticeCodeValue();
    }

    interface AttachmentSection extends Component {
//        @XPath("header")
        @XPath("//*[@id=\"title-of-page\"]")
        Readable<String> header();

//        @XPath("griglia contenuti")
        @XPath("//*[@id=\"title-of-page\"]")
        Readable<String> attachmentGrid();
    }

    @XPath(".//div[@data-testid='NotificationDetailTimeline']")
    interface NotificationStatusSection extends Component {
//        @XPath("header")
        @XPath("//*[@id=\"title-of-page\"]")
        Readable<String> header();

        //"chip stato notifica"
        Chip notificationStatusChip();

//        @XPath("dettaglio stato notifica")
        @XPath("//*[@id=\"title-of-page\"]")
        Readable<String> detailsText();

//        @XPath("griglia contenuti")
        @XPath("//*[@id=\"title-of-page\"]")
        Clickable details();
    }

    @Override
    NotificationSummarySection notificationSummarySection();

    @Override
    PaymentSection paymentSection();

    @Override
    AttachmentSection attachmentSection();

    @Override
    NotificationStatusSection notificationStatusSection();

    @Override
    default void assertLoaded() {
//        seeDetailsButton().click();
        breadcrumbs().readAndAssert((h) -> {
            Assertions.assertThat(h).isNotNull();
            Assertions.assertThat(h).isIn("Dettaglio notifica", "Notification details");
        });
    }


}
