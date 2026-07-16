package it.pagopa.send.domain.web.pages.mittente;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.send.web.notification_details.infrastructure.suit.NotificationDetailsPage;
import it.pagopa.send.web.notification_details.infrastructure.suit.component.NotificationStatusDrawer;
import org.assertj.core.api.Assertions;

@Url("${url.notifiche.mittente.dashboard}#selfCareToken=${token.mittente}")
public interface MittenteNotificationDetailsPage extends NotificationDetailsPage {

    @XPath("//*[@id=\"title-of-page\"]")
    Readable<String> breadcrumbs();

    @XPath("//*[@id=\"notificationsTable.body.row\"]/td[7]/button")
    Clickable seeDetailsButton();

    interface NotificationSummarySection extends Component {
        @XPath("header iun")
        Readable<String> iunHeader();

        @XPath("header protocol number")
        Readable<String> protocolNumberHeader();

        @XPath("protocol number value")
        Readable<String> protocolNumberValue();

        @XPath("header sender")
        Readable<String> senderHeader();

        @XPath("sender value")
        Readable<String> senderValue();

        @XPath("header recipient")
        Readable<String> recipientHeader();

        @XPath("recipient value")
        Readable<String> recipientValue();

        @XPath("open details sidebar")
        Clickable openDetailsSidebarButton();

        NotificationStatusDrawer notificationStatusDrawer();

    }

    interface PaymentSection extends Component {
        @XPath("header notice code")
        Readable<String> noticeCodeHeader();

        @XPath("notice code value")
        Readable<String> noticeCodeValue();
    }

    interface AttachmentSection extends Component {
        @XPath("header")
        Readable<String> header();

        @XPath("griglia contenuti")
        Readable<String> attachmentGrid();
    }

    interface NotificationStatusSection extends Component {
        @XPath("header")
        Readable<String> header();

        @XPath("chip stato notifica")
        Readable<String> notificationStatusChip();

        @XPath("dettaglio stato notifica")
        Readable<String> detailsText();

        @XPath("griglia contenuti")
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
