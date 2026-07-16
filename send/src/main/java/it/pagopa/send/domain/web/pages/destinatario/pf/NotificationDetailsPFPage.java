package it.pagopa.send.domain.web.pages.destinatario.pf;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.send.domain.web.pages.destinatario.pf.login.DowntimeItem;
import it.pagopa.send.web.infrastructure.suit.component.Chip;
import it.pagopa.send.web.notification_details.infrastructure.suit.NotificationDetailsPage;
import org.assertj.core.api.Assertions;

/**
 * Questa pagina rappresenta la pagina dei dettagli di una notifica per il cittadino, accessibile dal portale delle notifiche.
 * La pagina mostra informazioni dettagliate sulla notifica selezionata.
 */
@Url("da mappare")
public interface NotificationDetailsPFPage extends NotificationDetailsPage {

    @XPath("//*[@id=\"title-of-page\"]")
    Chip fullPecMessage();

    @XPath("//*[@id=\"title-of-page\"]")
    Chip notificationCancelledMessage();

    interface NotificationDetailsSection extends Component {

        @XPath("//*[@id=\"item\"]")
        Chip type();

        @XPath("//*[@id=\"item\"]")
        Readable<String> header();

        @XPath("//*[@id=\"item\"]")
        Readable<String> sender();

        @XPath("//*[@id=\"item\"]")
        Readable<String> date();

        @XPath("//*[@id=\"item\"]")
        Readable<String> iun();

        @XPath("//*[@id=\"item\"]")
        Readable<String> description();
    }

    interface AttachmentDocumentSection extends Component {
        @XPath("//*[@id=\"item\"]")
        Readable<String> header();

        @XPath("//*[@id=\"item\"]")
        Readable<String> message();

        @XPath("//*[@id=\"item\"]")
        Readable<String> file();

        @XPath("//*[@id=\"item\"]")
        Chip raddMessage();
    }

    interface PaymentSection extends Component {
        @XPath("//*[@id=\"item\"]")
        Readable<String> header();

        @XPath("//*[@id=\"item\"]")
        Readable<String> costMessage();

        @XPath("//*[@id=\"item\"]")
        Readable<String> noticeCode();

        @XPath("//*[@id=\"item\"]")
        Readable<String> expiredDate();

        @XPath("//*[@id=\"item\"]")
        Readable<String> amount();

        @XPath("//*[@id=\"item\"]")
        Clickable payButton();

        @XPath("//*[@id=\"item\"]")
        Clickable downloadButton();
    }

    interface NotificationStatusSection extends Component {
        @XPath("//*[@id=\"item\"]")
        Readable<String> header();

        @XPath("//*[@id=\"item\"]")
        Chip status();

        @XPath("//*[@id=\"item\"]")
        Readable<String> message();

        @XPath("//*[@id=\"item\"]")
        Clickable detailsButton();

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
    PaymentSection paymentSection();

    @Override
    AttachmentDocumentSection attachmentSection();

    @Override
    NotificationStatusSection notificationStatusSection();

    NotificationAARDetailsSection notificationAARDetailsSection();

    FacSimileSection facsimileSection();

    DowntimeSection downtimeSection();

}
