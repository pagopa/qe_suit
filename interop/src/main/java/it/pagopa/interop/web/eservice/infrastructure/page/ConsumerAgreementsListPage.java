package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.*;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import org.assertj.core.api.Assertions;


@Url("${interop.web.base-url}/fruizione/richieste")
public interface ConsumerAgreementsListPage extends Page {

    Breadcrumbs breadcrumbs();

    @XPath(".//h1")
    Readable<String> pageTitle();

    @XPath(".//h1/following-sibling::p[1]")
    Label pageDescription();

    @XPath(".//div[contains(@class,'MuiFormControl-root')]/label[.='Cerca per e-service']")
    Label eServiceFilterLabel();

    @XPath(".//div[contains(@class,'MuiAutocomplete-root') and @label='Cerca per e-service']//input")
    TextField eServiceFilterInput();

    @XPath("(//div[contains(@class,'MuiFormControl-root')])[2]//label")
    Label providerFilterLabel();

    @XPath("(//div[contains(@class,'MuiFormControl-root')])[2]//input")
    TextField providerFilterInput();

    @XPath(".//div[contains(@class,'MuiFormControl-root')]/label[.='Stato della richiesta']")
    Label statusFilterLabel();

    @XPath(".//div[contains(@class,'MuiAutocomplete-root') and @label='Stato della richiesta']//input")
    TextField statusFilterInput();

    @XPath("//table")
    Table agreementsTable();

    PageSize rowsPerPageSelector();

    @XPath("//nav[contains(@aria-label,'pagination')]")
    Pagination pagination();

    @XPath("//*[contains(@class,'MuiAlert-message') and .='La ricerca corrente non ha prodotto risultati']")
    Alert noResultMessage();

    @Override
    default void assertLoaded() {
        Assertions.assertThat(breadcrumbs().items().stream().map(Readable::read).toList())
                .as("Breadcrumbs")
                .containsExactly("Fruizione", "Richieste inoltrate");
        pageTitle().readAndAssert("Richieste di fruizione inoltrate");
    }
}
