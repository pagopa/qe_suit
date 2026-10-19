package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Pagination;
import it.pagopa.infrastructure.suit.component.Table;
import it.pagopa.infrastructure.suit.component.TextField;

@Url("${interop.web.base-url}/fruizione/richieste")
public interface ConsumerAgreementsListPage extends Page {

    @XPath(".//h1")
    Readable<String> pageTitle();

    @XPath(".//p")
    Readable<String> pageDescription();

    @XPath("(//div[contains(@class,'MuiFormControl-root')])[1]//label")
    Label eServiceFilterLabel();

    @XPath("(//div[contains(@class,'MuiFormControl-root')])[1]//input")
    TextField eServiceFilterInput();

    @XPath("(//div[contains(@class,'MuiFormControl-root')])[2]//label")
    Label producerFilterLabel();

    @XPath("(//div[contains(@class,'MuiFormControl-root')])[2]//input")
    TextField producerFilterInput();

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
        pageTitle().readAndAssert("Richieste di fruizione inoltrate");
    }
}
