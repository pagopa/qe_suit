package it.pagopa.send.steps.pg.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import org.assertj.core.api.Assertions;

@Url("${url.notifiche.persona-giuridica.delegati}")
public interface OrganizationAuthorizedRepresentativesPage extends Page {

    @XPath("//*[@id=\"simple-tabpanel--1\"]/div/div/div[1]/h6")
    Readable<String> breadcrumbs();

    @Override
    default void assertLoaded() {
        breadcrumbs().readAndAssert((h) -> {
            Assertions.assertThat(h).isNotNull();
            Assertions.assertThat(h.getText()).isIn("Authorised representatives of the company");
        });
    }
}
