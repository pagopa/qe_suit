package it.pagopa.interop.common.purpose.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.domain.DelegationRef;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeVersion;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@Service
@RequiredArgsConstructor
public class PurposeUseCase {
    private final PurposeGateway purposeGateway;
    private final PurposeCommandFactory purposeCommandFactory;

    /**
     * Exceeds the published descriptor's consumer threshold by one, preserving the active version
     * and verifying that the new version is waiting for approval.
     */
    public Purpose updatePurposeDailyCallsEstimateBeyondThreshold(Purpose purpose, EService eService) {
        PurposeVersion activeVersion = purpose.getLastActiveVersion();
        if (activeVersion == null) {
            throw new IllegalStateException("No active version found for purpose " + purpose.getId());
        }
        if (purpose.getLastWaitingForApprovalVersion() != null) {
            throw new IllegalStateException("Purpose already has a version waiting for approval: " + purpose.getId());
        }
        if (!eService.getId().equals(purpose.getEserviceId())) {
            throw new IllegalArgumentException("E-service does not belong to purpose " + purpose.getId());
        }
        Integer threshold = eService.getActiveDescriptor().getDailyCallsPerConsumer();
        int dailyCalls = threshold + 1;
        Purpose updatedPurpose = purposeGateway.updatePurposeDailyCalls(purpose.getRef(), dailyCalls);
        PurposeVersion waitingVersion = updatedPurpose.getLastWaitingForApprovalVersion();
        assertThat(waitingVersion).as("New version waiting for approval for purpose %s", purpose.getId()).isNotNull();
        assertThat(purpose.findVersionById(waitingVersion.getId())).as("The waiting version must be new").isEmpty();
        assertThat(waitingVersion.getDailyCalls()).isEqualTo(dailyCalls);
        PurposeVersion preservedActiveVersion = updatedPurpose.findVersionById(activeVersion.getId())
                .orElseThrow(() -> new IllegalStateException("Previous active purpose version was not returned"));
        assertThat(preservedActiveVersion.getPurposeVersionState()).isEqualTo(PurposeVersionState.ACTIVE);
        assertThat(preservedActiveVersion.getDailyCalls()).isEqualTo(activeVersion.getDailyCalls());
        return updatedPurpose;
    }

    public Purpose createPurpose(EService eService, Consumer<PurposeCreateCommand> commandConfig) {
        PurposeCreateCommand defaultCommand = purposeCommandFactory.validFullPopulatedCreateCommand(eService.getRef());
        commandConfig.accept(defaultCommand);

        return purposeGateway.createPurpose(defaultCommand);
    }

    public Purpose createPurpose(EService eService) {
        PurposeCreateCommand command = purposeCommandFactory.validFullPopulatedCreateCommand(eService.getRef());
        return purposeGateway.createPurpose(command);
    }

    public Purpose createReversePurpose(EService eService, EServiceRiskAnalysis riskAnalysis) {
        return purposeGateway.createReversePurpose(eService.getRef(), riskAnalysis.getRef());
    }

    public Purpose createPurposeFromTemplate(PurposeTemplate purposeTemplate, EService eService, Consumer<PurposeCreateCommand> commandConfig) {
        PurposeCreateCommand defaultCommand = purposeCommandFactory.validFullPopulatedCreateCommand(eService.getRef());
        commandConfig.accept(defaultCommand);

        return purposeGateway.createPurposeFromTemplate(purposeTemplate.getRef(), defaultCommand);
    }

    public Purpose createPurposeFromTemplate(PurposeTemplate purposeTemplate, EService eService) {
        return purposeGateway.createPurposeFromTemplate(purposeTemplate.getRef(), eService.getRef());
    }

    public Purpose activatePurpose(Purpose purpose, PurposeVersion purposeVersion, @Nullable DelegationRef delegationRef) {
        return purposeGateway.activatePurpose(purpose.getRef(), purposeVersion.getRef(), delegationRef);
    }

    public Purpose rejectPurpose(Purpose purpose, PurposeVersion purposeVersion, String rejectionReason) {
        return purposeGateway.rejectPurpose(purpose.getRef(), purposeVersion.getRef(), rejectionReason);
    }

    public Purpose askForApprovalPurpose(Purpose purpose, PurposeVersion purposeVersion, @Nullable DelegationRef delegationRef) {
        return purposeGateway.askForApprovalPurpose(purpose.getRef(), purposeVersion.getRef(), delegationRef);
    }

    public Purpose suspendPurpose(Purpose purpose, PurposeVersion purposeVersion, @Nullable DelegationRef delegationRef) {
        return purposeGateway.suspendPurpose(purpose.getRef(), purposeVersion.getRef(), delegationRef);
    }
}
