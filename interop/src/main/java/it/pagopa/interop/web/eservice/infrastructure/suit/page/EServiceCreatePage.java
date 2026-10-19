package it.pagopa.interop.web.eservice.infrastructure.suit.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form.GeneralDataForm;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.threshold_attribute_form.ThresholdAndAttributeForm;

@Url("${interop.web.base-url}/erogazione/e-service/crea/")
public interface EServiceCreatePage extends Page {

    @XPath(".//h1")
    Readable<String> title();

    GeneralDataForm generalDataForm();

    ThresholdAndAttributeForm thresholdAndAttributeForm();

    /**
     * Il componente viene commentato visto il restyling previsto nell'ambiente di dev e non compatibile con l'ambiente di QA
     * @return the stepper component for creating or updating an e-service
     */
    //Stepper upsertStepper();

    @Override
    default void assertLoaded() {
        title().readAndAssert("Crea e-service");
        //Assertions.assertThat(upsertStepper().get()).as("The stepper must be presents").isPresent();
    }
}
