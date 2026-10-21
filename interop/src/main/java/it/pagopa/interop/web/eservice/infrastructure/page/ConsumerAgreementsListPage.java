package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Pagination;
import it.pagopa.infrastructure.suit.component.Table;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import org.assertj.core.api.Assertions;

@Url("${interop.web.base-url}/fruizione/richieste")
public interface ConsumerAgreementsListPage extends Page {

    Breadcrumbs breadcrumbs();

    @XPath(".//h1")
    Readable<String> pageTitle();

    @XPath(".//h1/following-sibling::p[1]")
    Label pageDescription();

    @XPath("(//div[contains(@class,'MuiFormControl-root')])[1]//label")
    Label eServiceFilterLabel();

    @XPath("(//div[contains(@class,'MuiFormControl-root')])[1]//input")
    TextField eServiceFilterInput();

    @XPath("(//div[contains(@class,'MuiFormControl-root')])[2]//label")
    Label providerFilterLabel();

    @XPath("(//div[contains(@class,'MuiFormControl-root')])[2]//input")
    TextField providerFilterInput();

    @XPath("(//div[contains(@class,'MuiFormControl-root')])[3]//label")
    Label statusFilterLabel();

    @XPath("(//div[contains(@class,'MuiFormControl-root')])[3]//input")
    TextField statusFilterInput();

    @XPath("//table")
    Table agreementsTable();

    @XPath("//nav[contains(@aria-label,'pagination')]")
    Pagination pagination();

    @Override
    default void assertLoaded() {
        Assertions.assertThat(breadcrumbs().items().stream().map(Readable::read).toList())
                .as("Breadcrumbs")
                .containsExactly("Fruizione", "Richieste inoltrate");
        pageTitle().readAndAssert("Richieste di fruizione inoltrate");
    }
}
