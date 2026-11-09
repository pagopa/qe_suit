package it.pagopa.interop.common.purpose.infrastructure.cucumber;

import io.cucumber.java.en.Then;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.purpose.application.PurposeUseCase;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeVersion;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import it.pagopa.interop.m2m.kernel.context.CurrentM2MSession;
import it.pagopa.interop.m2m.kernel.domain.M2MRole;
import lombok.RequiredArgsConstructor;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class PurposeSteps {

    private final EntityStore entityStore;
    private final PurposeUseCase purposeUseCase;
    private final CurrentUserSession currentUserSession;
    private final CurrentM2MSession currentM2MSession;

    @Then("{tenant} approva la finalità che va in stato {purposeState}")
    public void approvePurpose(Tenant tenant, PurposeVersionState expectedState) {
        currentUserSession.set(User.getTenantAdmin(tenant), tenant);
        currentM2MSession.set(tenant, M2MRole.M2M_ADMIN);

        Purpose purpose = entityStore.getLastOrThrow(Purpose.class);
        PurposeVersion versionToApprove = purpose.getLastWaitingForApprovalVersion();
        if (versionToApprove == null) {
            throw new IllegalStateException("No purpose version waiting for approval was found for purpose " + purpose.getId());
        }

        Purpose updatedPurpose = purposeUseCase.activatePurpose(purpose, versionToApprove, null);
        PurposeVersionState actualState = updatedPurpose.findVersionById(versionToApprove.getId())
                .map(PurposeVersion::getPurposeVersionState)
                .orElseThrow(() -> new IllegalStateException(
                        "Approved purpose version was not returned for purpose " + purpose.getId()
                ));

        assertThat(actualState).isEqualTo(expectedState);
    }
}
