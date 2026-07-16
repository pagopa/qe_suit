package it.pagopa.send.domain.web.pages.destinatario.pf;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import org.assertj.core.api.Assertions;

/**
 * Questa pagina rappresenta la pagina iniziale del portale delle notifiche per il cittadino
 * in cui vengono visualizzate tutte le notifiche ricevute.
 * La pagina contiene un elenco di notifiche con informazioni come il mittente, la data di ricezione e lo stato della notifica.
 */
@Url("${url.notifiche.cittadino.notifiche}")
public interface NotificationPFPage extends Page {

    @XPath("//*[@id=\"item\"]")
    Readable<String> breadcrumbs();

    @XPath("//*[@id=\"notificationsTable.body.row\"]/td[7]/div/button")
    Clickable notificationDetailsButton();

    default void goToNotificationDetails() {
        notificationDetailsButton().click();
    }

    @Override
    default void assertLoaded() {
        breadcrumbs().readAndAssert((h) -> {
            Assertions.assertThat(h).isNotNull();
            Assertions.assertThat(h).isIn("Your notifications", "Le tue notifiche");
        });
    }
}
