package it.pagopa.interop.web.pages.eservice_creation;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.web.pages.eservice_creation.component.GeneralInformationStepComponent;

@Url("${interop.web.base-url}/erogazione/e-service/crea/")
public interface EServiceCreationPage extends Page {

    @XPath(".//h1")
    Readable<String> title();

    GeneralInformationStepComponent generalInformationStep();

    @Override
    default void assertLoaded() {
        generalInformationStep().assertLoaded();
        title().readAndAssert("Crea e-service");
    }
}
