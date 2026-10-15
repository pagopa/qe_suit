package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.TextField;
import org.openqa.selenium.WebElement;

//TODO MATTEO: XPath andranno verificati sul DOM reale

@Url("${interop.web.base-url}/fruizione/richieste")
public interface ConsumerAgreementsListPage extends Page {

        @XPath(".//h1")
        Readable<String> pageTitle();

        @XPath(".//p")
        Readable<String> pageDescription();

//        @XPath("//input[contains(@name,'eservicesIds')]")
//        WebElement eServiceFilter();
//
//        @XPath("//input[contains(@name,'producersIds')]")
//        WebElement producerFilter();
//
//        @XPath("//input[contains(@name,'states')]")
//        WebElement statusFilter();


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
        WebElement agreementsTable();

        @XPath("//thead")
        WebElement tableHeader();

        @XPath("//th[1]")
        Readable<String> eServiceColumn();

        @XPath("//th[2]")
        Readable<String> providerColumn();

        @XPath("//th[3]")
        Readable<String> statusColumn();

        @XPath("//nav[contains(@class,'pagination')]")
        WebElement pagination();

        @Override
        default void assertLoaded() {
            pageTitle().readAndAssert("Richieste di fruizione inoltrate");
        }
}
