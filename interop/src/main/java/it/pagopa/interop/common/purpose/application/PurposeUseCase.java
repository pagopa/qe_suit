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
import it.pagopa.utils.async.PollingUtils;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class PurposeUseCase {
    private final PurposeGateway purposeGateway;
    private final PurposeCommandFactory purposeCommandFactory;

    public Purpose getPurpose(Purpose purpose) {
        return purposeGateway.getPurpose(purpose.getRef());
    }

    public Purpose updatePurposeMetadata(
            Purpose purpose, String name, String description,
            Boolean isFreeOfCharge, String freeOfChargeReason, Integer dailyCalls) {
        purposeGateway.updatePurposeMetadata(
                purpose.getRef(), name, description, isFreeOfCharge, freeOfChargeReason, dailyCalls);

        return pollForUpdatedMetadata(purpose, name, description, isFreeOfCharge, freeOfChargeReason, dailyCalls);
    }

    public Purpose updateReversePurposeMetadata(
            Purpose purpose, String name, String description,
            Boolean isFreeOfCharge, String freeOfChargeReason, Integer dailyCalls) {
        purposeGateway.updateReversePurposeMetadata(
                purpose.getRef(), name, description, isFreeOfCharge, freeOfChargeReason, dailyCalls);

        return pollForUpdatedMetadata(purpose, name, description, isFreeOfCharge, freeOfChargeReason, dailyCalls);
    }

    public void tryToUpdatePurposeMetadata(
            Purpose purpose, String name, String description,
            Boolean isFreeOfCharge, String freeOfChargeReason, Integer dailyCalls) {
        purposeGateway.tryToUpdatePurposeMetadata(
                purpose.getRef(), name, description, isFreeOfCharge, freeOfChargeReason, dailyCalls);
    }

    public void tryToUpdateReversePurposeMetadata(
            Purpose purpose, String name, String description,
            Boolean isFreeOfCharge, String freeOfChargeReason, Integer dailyCalls) {
        purposeGateway.tryToUpdateReversePurposeMetadata(
                purpose.getRef(), name, description, isFreeOfCharge, freeOfChargeReason, dailyCalls);
    }

    public void shouldFailToUpdatePurposeMetadataAsMalformed() {
        purposeGateway.shouldFailToUpdatePurposeMetadataAsMalformed();
    }

    public void shouldFailToUpdatePurposeMetadataAsForbidden() {
        purposeGateway.shouldFailToUpdatePurposeMetadataAsForbidden();
    }

    public void shouldFailToUpdatePurposeMetadataAsConflict() {
        purposeGateway.shouldFailToUpdatePurposeMetadataAsConflict();
    }

    private Purpose pollForUpdatedMetadata(
            Purpose purpose, String name, String description,
            Boolean isFreeOfCharge, String freeOfChargeReason, Integer dailyCalls) {
        return PollingUtils.pollUntil(
                () -> getPurpose(purpose),
                refreshed -> Objects.equals(name, refreshed.getTitle())
                        && Objects.equals(description, refreshed.getDescription())
                        && (isFreeOfCharge == null
                        || Objects.equals(isFreeOfCharge, refreshed.getIsFreeOfCharge()))
                        && (freeOfChargeReason == null
                        || Objects.equals(freeOfChargeReason, refreshed.getFreeOfChargeReason()))
                        && (dailyCalls == null
                        || refreshed.getLastDraftVersion() != null
                        && Objects.equals(dailyCalls, refreshed.getLastDraftVersion().getDailyCalls())),
                "Purpose " + purpose.getId() + " metadata did not reach the requested values"
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
        return signRiskAnalysis(purpose, resolveMetadataVersion(purpose));
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

    public Purpose archivePurpose(Purpose purpose, PurposeVersion purposeVersion) {
        return purposeGateway.archivePurpose(purpose.getRef(), purposeVersion.getRef());
    }

    public void attemptNameUpdate(Purpose purpose, String name) {
        purposeGateway.attemptNameUpdate(purpose.getRef(), name);
    }

    public void attemptPurposeCloning(Purpose purpose) {
        PurposeCloneCommand command = purposeCommandFactory.cloneCommand(EServiceRef.of(purpose.getEserviceId()));
        purposeGateway.attemptPurposeCloning(purpose.getRef(), command);
    }

    public void attemptRiskAnalysisSigning(Purpose purpose) {
        attemptRiskAnalysisSigning(purpose, resolveMetadataVersion(purpose));
    }

    public void attemptRiskAnalysisSigning(Purpose purpose, int metadataVersionToSign) {
        SignRiskAnalysisCommand command = purposeCommandFactory.signRiskAnalysisCommand(metadataVersionToSign);
        purposeGateway.attemptRiskAnalysisSigning(purpose.getRef(), command);
    }

    public void attemptRiskAnalysisRejection(Purpose purpose, String rejectionReason) {
        RejectRiskAnalysisCommand command = purposeCommandFactory.rejectRiskAnalysisCommand(rejectionReason);
        purposeGateway.attemptRiskAnalysisRejection(purpose.getRef(), command);
    }

    public void attemptCreatePurposeWithTitle(EService eService, String title) {
        PurposeCreateCommand command = purposeCommandFactory.validFullPopulatedCreateCommand(eService.getRef());
        command.title(title);
        purposeGateway.attemptCreatePurpose(command);
    }

    public void attemptCreateReversePurposeWithTitle(
        EService eService,
        EServiceRiskAnalysis riskAnalysis,
        String title
    ) {
        purposeGateway.attemptCreateReversePurpose(eService.getRef(), riskAnalysis.getRef(), title);
    }

    public void attemptCreatePurposeFromTemplateWithTitle(
        PurposeTemplate purposeTemplate,
        EService eService,
        String title
    ) {
        PurposeCreateCommand command = purposeCommandFactory.validFullPopulatedCreateCommand(eService.getRef());
        command.title(title);
        purposeGateway.attemptCreatePurposeFromTemplate(purposeTemplate.getRef(), command);
    }

    public void attemptNameUpdateFromTemplate(
        PurposeTemplate purposeTemplate,
        Purpose purpose,
        String name
    ) {
        purposeGateway.attemptNameUpdateFromTemplate(purposeTemplate.getRef(), purpose.getRef(), name);
    }

    public void attemptRiskAnalysisFormUpdate(Purpose purpose) {
        purposeGateway.attemptRiskAnalysisFormUpdate(purpose.getRef());
    }

    private int resolveMetadataVersion(Purpose purpose) {
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
        return metadataVersion;
    }
}
