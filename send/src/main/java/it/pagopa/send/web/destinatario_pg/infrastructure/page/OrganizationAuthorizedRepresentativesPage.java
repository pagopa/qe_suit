package it.pagopa.send.web.destinatario_pg.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import org.assertj.core.api.Assertions;

@Url("${url.notifiche.persona-giuridica.delegati}")
public interface OrganizationAuthorizedRepresentativesPage extends Page {

    // titolo della sezione: il primo h2 (con lo stile h6) del pannello del tab
    @XPath("(//*[@role=\"tabpanel\"]//h2)[1]")
    Readable<String> breadcrumbs();

    @Override
    default void assertLoaded() {
        breadcrumbs().readAndAssert((h) -> {
            Assertions.assertThat(h).isNotNull();
            Assertions.assertThat(h).isIn("Authorised representatives of the company",
                    "Delegati dall'impresa");
        });
    }
}
