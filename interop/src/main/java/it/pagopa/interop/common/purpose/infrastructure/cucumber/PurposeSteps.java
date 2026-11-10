package it.pagopa.interop.common.purpose.infrastructure.cucumber;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.application.context.EntityStore;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.response.ApiResponse;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.purpose.application.PurposeUseCase;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

@RequiredArgsConstructor
public class PurposeSteps {

    private final PurposeUseCase purposeUseCase;
    private final CurrentUserSession currentUserSession;
    private final EntityStore entityStore;
    private final LastApiResponseStore lastApiResponseStore;

    @When("{tenant} tenta di creare una finalità a partire dal proprio template associando l'e-service in modalità di ricezione")
    public void tryCreatePurposeFromTemplateWithReceiveEService(Tenant tenant) {
        currentUserSession.set(User.getTenantAdmin(tenant), tenant);
        PurposeTemplate purposeTemplate = entityStore.getLastOrThrow(PurposeTemplate.class);
        EService eService = entityStore.getLastOrThrow(EService.class);
        purposeUseCase.tryCreatePurposeFromTemplate(purposeTemplate, eService);
    }

    @When("{tenant} tenta di creare una finalità associando l'e-service in modalità di ricezione")
    public void tryCreatePurposeWithReceiveEService(Tenant tenant) {
        currentUserSession.set(User.getTenantAdmin(tenant), tenant);
        EService eService = entityStore.getLastOrThrow(EService.class);
        purposeUseCase.tryCreatePurpose(eService);
    }

    @Then("la creazione della finalità è negata")
    public void assertPurposeCreationDenied() {
        ApiResponse lastResponse = lastApiResponseStore.getLastResponse();
        Assertions.assertThat(lastResponse).isNotNull();
        Assertions.assertThat(lastResponse.isSuccess())
                .as("La creazione della finalità con e-service in modalità di ricezione doveva essere rifiutata")
                .isFalse();
    }
}

