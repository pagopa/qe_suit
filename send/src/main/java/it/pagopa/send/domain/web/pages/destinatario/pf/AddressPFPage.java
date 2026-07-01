package it.pagopa.send.domain.web.pages.destinatario.pf;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.domain.web.pages.destinatario.AddressPage;
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
