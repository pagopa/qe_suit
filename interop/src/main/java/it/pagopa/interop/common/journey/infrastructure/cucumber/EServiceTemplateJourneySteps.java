package it.pagopa.interop.common.journey.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EServiceTemplateJourneySteps {

    private final InteropJourney interopJourney;

    @Given("un Template-EService creato dal {tenant} con un attributo dichiarato")
    public void creaTemplateEServiceConAttributoDichiarato(Tenant producer) {
        interopJourney
                .withProducer(producer, UserRole.ADMIN)
                .createDeclaredAttribute()
                .createEServiceTemplate(EServiceTemplateVersionState.PUBLISHED);
    }
}

