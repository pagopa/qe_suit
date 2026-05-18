package it.pagopa.send.domain.web.pages.destinatario.pg;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import org.assertj.core.api.Assertions;

@Url("${url.notifiche.persona-giuridica.deleghe-a-carico}")
public interface OrganizationDelegationsPage extends Page {

    @XPath("//*[@id=\"simple-tabpanel--1\"]/div/div/h6")
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
