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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Questa pagina rappresenta la pagina dei dettagli di una notifica per il cittadino, accessibile dal portale delle notifiche.
 * La pagina mostra informazioni dettagliate sulla notifica selezionata.
 */
@Url("www.google.com")
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

        @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div[2]/div/div[1]/div[1]/div[3]/div[1]/div[2]/span")
        Readable<String> sender();

        @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div[2]/div/div[1]/div[1]/div[3]/div[1]/div[2]/p")
        Readable<String> date();

        @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div[2]/div/div[1]/div[1]/div[3]/div[2]/div/p[2]")
        Readable<String> iun();

        @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div[2]/div/div[1]/div[1]/p")
        Readable<String> description();

        @Override
        default void assertLoaded() {
            header().readAndAssert((h) -> {
                assertThat(h).isNotNull();
                assertThat(h).isIn("Configure SEND", "Configura SEND");
            });
        }
    }

    interface AttachmentDocumentSection extends Component {
        @XPath("//*[@id=\"notification-detail-document-attached\"]")
        Readable<String> header();

        @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div[2]/div/div[2]/div[1]/div/div[1]/div/p")
        Readable<String> message();

        @XPath("//*[@id=\"document-button\"]")
        Readable<String> file();

        @XPath("//*[@id=\"item\"]")
        Chip raddMessage();
    }

    interface PaymentSection extends Component {
        @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div[2]/div/div[2]/div[2]/div/h2")
        Readable<String> header();

        @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div[2]/div/div[2]/div[2]/div/div[1]/div[2]/div/div")
        Readable<String> costMessage();

        @XPath("//*[@id=\"paymentPagoPa-302040124464100004\"]/div[1]/div[1]/span[2]")
        Readable<String> noticeCode();

        @XPath("//*[@id=\"paymentPagoPa-302040124464100004\"]/div[1]/div[2]/span[2]")
        Readable<String> expiredDate();

        @XPath("//*[@id=\"paymentPagoPa-302040124464100004\"]/div[2]/div/p")
        Readable<String> amount();

        @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div[2]/div/div[2]/div[2]/div/button")
        Clickable payButton();

        @XPath("//*[@id=\"item\"]")
        Clickable downloadButton();
    }

    interface NotificationStatusSection extends Component {
        @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div[2]/aside/div[1]/div/h2")
        Readable<String> header();

        @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div[2]/aside/div[1]/div/div")
        Chip status();

        @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div[2]/aside/div[1]/div/p")
        Readable<String> message();

        @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div[2]/aside/div[1]/div/button")
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
