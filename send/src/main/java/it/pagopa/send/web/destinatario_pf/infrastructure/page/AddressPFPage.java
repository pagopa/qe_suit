package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.web.infrastructure.page.AddressPage;
import org.assertj.core.api.Assertions;

@Url("${url.notifiche.cittadino.recapiti}")
public interface AddressPFPage extends AddressPage, Page {

    default void assertLoaded() {
        breadcrumbs().readAndAssert((h) -> {
            Assertions.assertThat(h).isNotNull();
            Assertions.assertThat(h).isIn("Addresses", "I tuoi recapiti");
        });
    }
}
