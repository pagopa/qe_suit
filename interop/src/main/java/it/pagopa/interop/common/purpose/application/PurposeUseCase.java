package it.pagopa.interop.common.purpose.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.domain.DelegationRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeCreationFailureReason;
import it.pagopa.interop.common.purpose.domain.PurposeVersion;
import it.pagopa.interop.common.purpose.domain.RiskAnalysisReviewMode;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@Service
@RequiredArgsConstructor
public class PurposeUseCase {
    private final PurposeGateway purposeGateway;
    private final PurposeCommandFactory purposeCommandFactory;

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

    public void shouldFailToCreatePurposeWithSameTitle(Purpose existingPurpose) {
        PurposeCreateCommand command = purposeCommandFactory.validFullPopulatedCreateCommand(
                EServiceRef.of(existingPurpose.getEserviceId()));
        command.title(existingPurpose.getTitle());
        purposeGateway.shouldFailToCreatePurpose(command, PurposeCreationFailureReason.DUPLICATE_TITLE);
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

    public Purpose clonePurpose(Purpose purpose, EService eService) {
        PurposeCloneCommand command = purposeCommandFactory.cloneCommand(eService.getRef());
        return purposeGateway.clonePurpose(purpose.getRef(), command);
    }

    public Purpose assignRiskAnalysisReviewer(Purpose purpose, RiskAnalysisReviewMode reviewMode, User reviewer) {
        AssignRiskAnalysisReviewerCommand command = purposeCommandFactory
                .assignRiskAnalysisReviewerCommand(reviewMode, List.of(reviewer.getUserId()));
        return purposeGateway.assignRiskAnalysisReviewer(purpose.getRef(), command);
    }

    public Purpose submitRiskAnalysis(Purpose purpose) {
        SubmitRiskAnalysisCommand command = purposeCommandFactory.submitRiskAnalysisCommand();
        return purposeGateway.submitRiskAnalysis(purpose.getRef(), command);
    }

    public Purpose signRiskAnalysis(Purpose purpose) {
        Integer metadataVersion = purpose.getMetadataVersion();
        if (metadataVersion == null) {
            Purpose refreshed = purposeGateway.getPurpose(purpose.getRef());
            metadataVersion = refreshed.getMetadataVersion();
        }
        if (metadataVersion == null) {
            throw new IllegalStateException(
                    "Missing metadataVersion (x-metadata-version header) for purpose " + purpose.getId()
            );
        }
        return signRiskAnalysis(purpose, metadataVersion);
    }

    public Purpose signRiskAnalysis(Purpose purpose, int metadataVersionToSign) {
        SignRiskAnalysisCommand command = purposeCommandFactory
                .signRiskAnalysisCommand(metadataVersionToSign);
        return purposeGateway.signRiskAnalysis(purpose.getRef(), command);
    }

    public Purpose rejectRiskAnalysis(Purpose purpose, String rejectionReason) {
        RejectRiskAnalysisCommand command = purposeCommandFactory
                .rejectRiskAnalysisCommand(rejectionReason);
        return purposeGateway.rejectRiskAnalysis(purpose.getRef(), command);
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
