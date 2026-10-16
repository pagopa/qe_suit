package it.pagopa.send.web.notification_search.infrastructure.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import org.assertj.core.api.Assertions;

/**
 * Tabella delle notifiche ricevute nella pagina "In arrivo" di cittadini e imprese, con la paginazione.
 * Compare solo se il destinatario ha ricevuto almeno una notifica; i pulsanti di pagina solo se le notifiche sono più
 * di quelle mostrate.
 */
public interface NotificationsTable extends Component {
    @XPath("//*[@data-testid=\"notificationsTable\"]//th")
    Readable<String> headers();

    @XPath("//*[@data-testid=\"notificationsTable.body.row\"]/td[1]//p")
    Readable<String> dates();

    @XPath("//*[@data-testid=\"notificationsTable.body.row\"]/td[2]")
    Readable<String> senders();

    @XPath("//*[@data-testid=\"notificationsTable.body.row\"]/td[3]//p")
    Readable<String> subjects();

    @XPath("//*[@data-testid=\"notificationsTable.body.row\"]/td[4]")
    Readable<String> iuns();

    // oggetto di ogni riga con l'eventuale etichetta "Notifica a valore legale"
    @XPath("//*[@data-testid=\"notificationsTable.body.row\"]/td[3]")
    Readable<String> subjectCells();

    @XPath("//*[@data-testid=\"goToNotificationDetail\"]")
    Readable<String> openButtons();

    @XPath("//*[@id=\"rows-per-page\"]")
    Button rowsPerPageButton();

    @XPath("//*[@id=\"previous\"]")
    Button previousPageButton();

    @XPath("//*[@id=\"page1\"]")
    Button firstPageButton();

    @XPath("//*[@id=\"next\"]")
    Button nextPageButton();

    @Override
    default void assertLoaded() {
        headers().readAllAndAssert(h -> Assertions.assertThat(h).isNotEmpty());
    }
}
