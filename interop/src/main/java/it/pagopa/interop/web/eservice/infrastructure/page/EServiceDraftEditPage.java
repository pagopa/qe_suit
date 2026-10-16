package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.pagopa.interop.web.eservice.infrastructure.suit.page.EServiceCreatePage;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import org.assertj.core.api.Assertions;

@Url("${interop.web.producer-e-service-list}/${eserviceId}/${descriptorId}/modifica")
public interface EServiceDraftEditPage extends EServiceCreatePage {

    @XPath(".//nav[contains(@class, 'MuiBreadcrumbs-root') and .//li[normalize-space(.)='Modifica e-service']]")
    Breadcrumbs breadcrumbs();

    @Override
    default void assertLoaded() {
        Assertions.assertThat(breadcrumbs().get(FindPolicy.VISIBLE))
                .as("E-service edit page breadcrumbs are visible")
                .isPresent();
        Assertions.assertThat(breadcrumbs().items().stream().map(Readable::read).toList())
                .as("Breadcrumbs")
                .containsExactly("Erogazione", "I miei e-service", "Modifica e-service");
        title().readAndAssert(name -> Assertions.assertThat(name).isNotBlank());
        Assertions.assertThat(generalDataForm().aboutEServiceSection().eServiceNameInput().get(FindPolicy.CLICKABLE))
                .as("Draft e-service name is editable")
                .isPresent();
        Assertions.assertThat(generalDataForm().saveDraftButton().get(FindPolicy.CLICKABLE))
                .as("Draft e-service can be saved")
                .isPresent();
    }
}
