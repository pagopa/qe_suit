package it.pagopa.send.web.destinatario_pg.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import org.assertj.core.api.Assertions;

@Url("${url.notifiche.persona-giuridica.deleghe-a-carico}")
public interface OrganizationDelegationsPage extends Page {

    // titolo della sezione: un h2 con lo stile h6, dentro il blocco con data-testid stabile
    @XPath("//*[@data-testid=\"delegationsOfTheCompany\"]/h2")
    Readable<String> breadcrumbs();

    @Override
    default void assertLoaded() {
        breadcrumbs().readAndAssert((h) -> {
            Assertions.assertThat(h).isNotNull();
            Assertions.assertThat(h).isIn("Authorities held by the company",
                    "Deleghe a carico dell'impresa");
        });
    }
}
