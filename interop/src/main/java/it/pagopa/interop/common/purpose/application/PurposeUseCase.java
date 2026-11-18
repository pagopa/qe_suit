package it.pagopa.interop.common.purpose.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.domain.DelegationRef;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeVersion;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.utils.async.PollingUtils;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class PurposeUseCase {
    private final PurposeGateway purposeGateway;
    private final PurposeCommandFactory purposeCommandFactory;

    public Purpose getPurpose(Purpose purpose) {
        return purposeGateway.getPurpose(purpose.getRef());
    }

    public Purpose updatePurposeMetadata(Purpose purpose, String name, String description) {
        purposeGateway.updatePurposeMetadata(purpose.getRef(), name, description);

        return pollForUpdatedMetadata(purpose, name, description);
    }

    public Purpose updateReversePurposeMetadata(Purpose purpose, String name, String description) {
        purposeGateway.updateReversePurposeMetadata(purpose.getRef(), name, description);

        return pollForUpdatedMetadata(purpose, name, description);
    }

    private Purpose pollForUpdatedMetadata(Purpose purpose, String name, String description) {
        return PollingUtils.pollUntil(
                () -> getPurpose(purpose),
                refreshed -> Objects.equals(name, refreshed.getTitle())
                        && Objects.equals(description, refreshed.getDescription()),
                "Purpose " + purpose.getId() + " metadata did not reach the requested name and description"
        );
    }

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
        return purposeGateway.updatePurposeDailyCalls(purpose.getRef(), dailyCalls);
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

    public Purpose archivePurpose(Purpose purpose, PurposeVersion purposeVersion) {
        return purposeGateway.archivePurpose(purpose.getRef(), purposeVersion.getRef());
    }
}
