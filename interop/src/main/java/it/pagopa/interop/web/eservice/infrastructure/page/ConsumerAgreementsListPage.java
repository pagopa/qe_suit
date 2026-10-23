package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.*;

@Url("${interop.web.base-url}/fruizione/richieste")
public interface ConsumerAgreementsListPage extends Page {

    @XPath(".//h1")
    Readable<String> pageTitle();

    @XPath(".//p[.='Le richieste di fruizione inoltrate verranno raccolte in questa area.']")
    Readable<String> pageDescription();

    @XPath(".//div[contains(@class,'MuiFormControl-root')]/label[.='Cerca per e-service']")
    Label eServiceFilterLabel();

    @XPath(".//div[contains(@class,'MuiAutocomplete-root') and @label='Cerca per e-service']//input")
    TextField eServiceFilterInput();

    @XPath(".//div[contains(@class,'MuiFormControl-root')]/label[.='Cerca per erogatore']")
    Label producerFilterLabel();

    @XPath(".//div[contains(@class,'MuiAutocomplete-root') and @label='Cerca per erogatore']//input")
    TextField producerFilterInput();

    @XPath(".//div[contains(@class,'MuiFormControl-root')]/label[.='Stato della richiesta']")
    Label statusFilterLabel();

    @XPath(".//div[contains(@class,'MuiAutocomplete-root') and @label='Stato della richiesta']//input")
    TextField statusFilterInput();

    @XPath("//table")
    Table agreementsTable();

    PageSize rowsPerPageSelector();

    @XPath("//nav[contains(@aria-label,'pagination')]")
    Pagination pagination();

    @Override
    default void assertLoaded() {
        pageTitle().readAndAssert("Richieste di fruizione inoltrate");
    }
}
