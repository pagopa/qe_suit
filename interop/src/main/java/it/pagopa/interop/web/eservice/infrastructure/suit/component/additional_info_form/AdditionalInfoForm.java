package it.pagopa.interop.web.eservice.infrastructure.suit.component.additional_info_form;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.TextField;

@XPath(".//form[.//button[contains(., 'Vai al riepilogo')]]")
public interface AdditionalInfoForm extends Component {

    @XPath(".//textarea[@name='description' and not(@aria-hidden='true')]")
    TextField descriptionInput();

    @XPath(".//button[contains(., 'Vai al riepilogo')]")
    Button goToSummaryButton();

    @Override
    default void assertLoaded() {
        descriptionInput().get(FindPolicy.PRESENT)
                .orElseThrow(() -> new IllegalStateException("Informazioni aggiuntive non caricate"));
    }
}
