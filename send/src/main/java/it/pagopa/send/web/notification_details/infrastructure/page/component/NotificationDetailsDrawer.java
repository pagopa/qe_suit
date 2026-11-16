package it.pagopa.send.web.notification_details.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import org.assertj.core.api.Assertions;

/**
 * Pannello "Dettaglio della notifica" del portale della PA, aperto con "Vai al dettaglio" del riepilogo: elenco dei dati
 * della notifica, ognuno con etichetta e valore.
 */
public interface NotificationDetailsDrawer extends Component {
    @XPath("//*[@data-testid=\"notificationDetailsDrawer\"]//h2")
    Readable<String> title();

    @XPath("//*[@data-testid=\"notificationDetailsDrawer\"]//hr/parent::*/div/*[1]")
    Readable<String> labels();

    @Override
    default void assertLoaded() {
        title().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
    }
}
