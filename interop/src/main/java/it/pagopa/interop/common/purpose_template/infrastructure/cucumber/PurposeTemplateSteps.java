package it.pagopa.interop.common.purpose_template.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantKind;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose_template.application.PurposeTemplateUseCase;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplateState;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PurposeTemplateSteps {

    private final EntityStore entityStore;
    private final InteropJourney journey;
    private final PurposeTemplateUseCase purposeTemplateUseCase;
    private PurposeTemplate purposeTemplateReference;

    @Given("i dati del template finalità allo stato attuale appuntati come riferimento")
    public void savePurposeTemplateReference() {
        PurposeTemplate purposeTemplate = entityStore.getLastOrThrow(PurposeTemplate.class);
        purposeTemplate = purposeTemplateUseCase.getPurposeTemplate(purposeTemplate);
        purposeTemplateReference = purposeTemplate.toBuilder().build();
    }

    @Given("un template finalità creato da/dal {tenant} e pubblicato")
    public void createPurposeTemplate(Tenant creator) {
        createPurposeTemplate(creator, PurposeTemplateState.PUBLISHED);
    }

    @Given("un template finalità creato da/dal {tenant} in stato {purposeTemplateState}")
    public void createPurposeTemplate(Tenant creator, PurposeTemplateState state) {
        journey.withProducer(creator, UserRole.ADMIN)
                .createPurposeTemplate(Tenant.fromTenantType(TenantKind.PA), state);
    }
}
