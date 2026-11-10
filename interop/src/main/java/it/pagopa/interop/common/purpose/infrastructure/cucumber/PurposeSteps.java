package it.pagopa.interop.common.purpose.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.delegation.domain.Delegation;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.DelegationRef;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose.application.PurposeUseCase;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeVersion;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import it.pagopa.interop.m2m.kernel.context.CurrentM2MSession;
import it.pagopa.interop.m2m.kernel.domain.M2MRole;
import lombok.RequiredArgsConstructor;

import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class PurposeSteps {

    private final EntityStore entityStore;
    private final InteropJourney interopJourney;
    private final PurposeUseCase purposeUseCase;
    private final CurrentUserSession currentUserSession;
    private final CurrentM2MSession currentM2MSession;

    @Given("l'e-service con una finalità in stato {purposeState} associata da/dal {tenant}")
    public void createEServiceAndLinkAgreementAndPurpose(PurposeVersionState purposeState, Tenant consumer) {
        interopJourney
                .withConsumer(consumer, UserRole.ADMIN)
                .createPurpose(purposeState);
    }

    @When("il fruitore aggiorna la stima di chiamate giornaliere oltre la sua soglia per la finalità")
    public void updatePurposeDailyCallsEstimateBeyondThreshold() {
        Purpose purpose = entityStore.getLastOrThrow(Purpose.class);
        EService eService = entityStore.getById(purpose.getEserviceId(), EService.class)
                .orElseThrow(() -> new IllegalStateException(
                        "E-service not found for purpose " + purpose.getId()
                ));
        Tenant consumer = Tenant.fromOrganizationId(purpose.getConsumerId());
        currentUserSession.set(User.getTenantAdmin(consumer), consumer);
        currentM2MSession.set(consumer, M2MRole.M2M_ADMIN);
        purposeUseCase.updatePurposeDailyCallsEstimateBeyondThreshold(purpose, eService);
    }

    @Then("{tenant} approva la finalità che va in stato {purposeState}")
    public void approvePurpose(Tenant tenant, PurposeVersionState expectedState) {
        processWaitingPurpose(tenant, expectedState,
                (purpose, version) -> purposeUseCase.activatePurpose(
                        purpose, version, null
                )
        );
    }

    @Then("{tenant} delegato all'erogazione approva la finalità che va in stato {purposeState}")
    public void approvePurposeWithDelegation(Tenant tenant, PurposeVersionState expectedState) {
        DelegationRef delegationRef = entityStore.getLastOrThrow(Delegation.class).getRef();
        processWaitingPurpose(tenant, expectedState,
                (purpose, version) -> purposeUseCase.activatePurpose(
                        purpose, version, delegationRef
                )
        );
    }

    @Then("{tenant} rifiuta la finalità che va in stato {purposeState}")
    public void rejectPurpose(Tenant tenant, PurposeVersionState expectedState) {
        processWaitingPurpose(tenant, expectedState,
                (purpose, version) -> purposeUseCase.rejectPurpose(
                        purpose, version, "Rifiutata nell'ambito del test"
                )
        );
    }

    private void processWaitingPurpose(Tenant tenant, PurposeVersionState expectedState, BiFunction<Purpose, PurposeVersion, Purpose> operation) {
        currentUserSession.set(User.getTenantAdmin(tenant), tenant);
        currentM2MSession.set(tenant, M2MRole.M2M_ADMIN);

        Purpose purpose = entityStore.getLastOrThrow(Purpose.class);
        PurposeVersion waitingVersion = purpose.getLastWaitingForApprovalVersion();
        if (waitingVersion == null) {
            throw new IllegalStateException("No purpose version waiting for approval was found for purpose " + purpose.getId());
        }

        Purpose updatedPurpose = operation.apply(purpose, waitingVersion);
        PurposeVersionState actualState = updatedPurpose.findVersionById(waitingVersion.getId())
                .map(PurposeVersion::getPurposeVersionState)
                .orElseThrow(() -> new IllegalStateException(
                        "Processed purpose version was not returned for purpose " + purpose.getId()
                ));

        assertThat(actualState).isEqualTo(expectedState);
    }
}
