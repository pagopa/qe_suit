package it.pagopa.send.domain.web.pages.destinatario.pg;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.domain.web.pages.destinatario.AddressPage;
import org.assertj.core.api.Assertions;

@Url("${url.notifiche.persona-giuridica.recapiti}")
public interface AddressPGPage extends AddressPage, Page {

    @XPath("//*[@id=\"item\"]")
    Readable<String> breadcrumbs();

    @Override
    default void assertLoaded() {
        breadcrumbs().readAndAssert((h) -> {
            Assertions.assertThat(h).isNotNull();
            Assertions.assertThat(h).isIn("Contacts", "Recapiti");
        });
    }
}
