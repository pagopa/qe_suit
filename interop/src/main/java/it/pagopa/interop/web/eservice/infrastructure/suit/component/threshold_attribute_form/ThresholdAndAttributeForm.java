package it.pagopa.interop.web.eservice.infrastructure.suit.component.threshold_attribute_form;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;

@XPath(".//form")
public interface ThresholdAndAttributeForm extends Component {

    ApiCallThresholdSection apiCallThresholdSection();

    RequiredAttributeSection requiredAttributeSection();

    @XPath(".//button[contains(., 'Indietro')]")
    Button backButton();

    @XPath(".//button[contains(., 'Salva bozza e prosegui')]")
    Button saveDraftButton();

    @Override
    default void assertLoaded() {
        apiCallThresholdSection().assertLoaded();
        requiredAttributeSection().assertLoaded();
    }
}
