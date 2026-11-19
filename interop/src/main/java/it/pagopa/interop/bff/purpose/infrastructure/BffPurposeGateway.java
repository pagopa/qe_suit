package it.pagopa.interop.bff.purpose.infrastructure;

import it.pagopa.infrastructure.response.ApiResponse;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.bff.purpose.application.BffAssignRiskAnalysisReviewerCommand;
import it.pagopa.interop.bff.purpose.application.BffPurposeCloneCommand;
import it.pagopa.interop.bff.purpose.application.BffPurposeCreateCommand;
import it.pagopa.interop.bff.purpose.application.BffRejectRiskAnalysisCommand;
import it.pagopa.interop.bff.purpose.application.BffSignRiskAnalysisCommand;
import it.pagopa.interop.bff.purpose.application.BffSubmitRiskAnalysisCommand;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.DelegationRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysisRef;
import it.pagopa.interop.common.kernel.domain.PurposeRef;
import it.pagopa.interop.common.kernel.domain.PurposeTemplateRef;
import it.pagopa.interop.common.kernel.domain.PurposeVersionRef;
import it.pagopa.interop.common.purpose.application.AssignRiskAnalysisReviewerCommand;
import it.pagopa.interop.common.purpose.application.PurposeCloneCommand;
import it.pagopa.interop.common.purpose.application.PurposeCreateCommand;
import it.pagopa.interop.common.purpose.application.PurposeGateway;
import it.pagopa.interop.common.purpose.application.RejectRiskAnalysisCommand;
import it.pagopa.interop.common.purpose.application.SignRiskAnalysisCommand;
import it.pagopa.interop.common.purpose.application.SubmitRiskAnalysisCommand;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import it.pagopa.interop.common.purpose.domain.PurposeCreationFailureReason;
import it.pagopa.interop.common.purpose.domain.RiskAnalysisSigningState;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RejectPurposeVersionPayload;
import it.pagopa.utils.async.PollingUtils;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BffPurposeGateway implements PurposeGateway {
    private static final String METADATA_VERSION_HEADER = "x-metadata-version";

    private final BffPurposeRestClient restClient;
    private final BffPurposeMapper mapper;
    private final BffPurposeCommandFactory commandFactory;

    @Override
    public Purpose getPurpose(PurposeRef purposeRef) {
        var action = restClient.getPurpose(purposeRef.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS);

        // Si accetta un cast esplicito perché, in ambito BFF, la risposta è sempre un ApiResponse
        String metadataVersionHeader = action.getRaw() instanceof ApiResponse apiResponse
                ? apiResponse.getHeader(METADATA_VERSION_HEADER)
                : null;
        Integer metadataVersion = metadataVersionHeader != null && !metadataVersionHeader.isBlank()
                ? Integer.valueOf(metadataVersionHeader.trim())
                : null;
        return action.map(bffPurpose -> {
                    Purpose mapped = mapper.toPurpose(bffPurpose);
                    return metadataVersion != null
                            ? mapped.toBuilder().metadataVersion(metadataVersion).build()
                            : mapped;
                })
                .updateContext()
                .get();
    }

    @Override
    public Purpose updatePurposeDailyCalls(PurposeRef purposeRef, int dailyCalls) {
        var response = restClient.createPurposeVersion(purposeRef.id(), new PurposeVersionSeed().dailyCalls(dailyCalls))
                .withPolling(PollingStrategy.UNTIL_SUCCESS);
        var createdVersion = response.get();
        return PollingUtils.pollUntil(
                () -> getPurpose(purposeRef),
                purpose -> purpose.findVersionById(createdVersion.getVersionId())
                        .map(version -> Integer.valueOf(dailyCalls).equals(version.getDailyCalls()))
                        .orElse(false)
        );
    }

    @Override
    public Purpose createPurpose(PurposeCreateCommand createCommand) {
        if (!(createCommand instanceof BffPurposeCreateCommand bffCommand)) {
            throw new IllegalArgumentException(
                    "Command must be an instance of " + BffPurposeCreateCommand.class.getSimpleName()
            );
        }

        PurposeSeed bffCreationPayload = bffCommand.getBffCreationPayload();
        return restClient.createPurpose(bffCreationPayload)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(createdResource -> getPurpose(PurposeRef.of(createdResource.getId())))
                .updateContext()
                .get();
    }

    @Override
    public void shouldFailToCreatePurpose(PurposeCreateCommand createCommand, PurposeCreationFailureReason reason) {
        if (!(createCommand instanceof BffPurposeCreateCommand bffCommand)) {
            throw new IllegalArgumentException(
                    "Command must be an instance of " + BffPurposeCreateCommand.class.getSimpleName()
            );
        }

        int expectedStatus = switch (reason) {
            case DUPLICATE_TITLE -> 409;
        };
        restClient.createPurpose(bffCommand.getBffCreationPayload())
                .withoutPolling()
                .assertStatusCode(expectedStatus);
    }

    @Override
    public Purpose createReversePurpose(EServiceRef eServiceRef, EServiceRiskAnalysisRef riskAnalysisRef) {
        return restClient.createReversePurpose(commandFactory.reverseCreationRequest(eServiceRef, riskAnalysisRef))
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(createdResource -> getPurpose(PurposeRef.of(createdResource.getId())))
                .updateContext()
                .get();
    }

    @Override
    public Purpose createPurposeFromTemplate(PurposeTemplateRef purposeTemplateRef, EServiceRef eServiceRef) {
        return restClient.createPurposeFromTemplate(
                        purposeTemplateRef.id(),
                        commandFactory.creationFromTemplateRequest(eServiceRef, purposeTemplateRef)
                )
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(createdResource -> getPurpose(PurposeRef.of(createdResource.getId())))
                .updateContext()
                .get();
    }

    @Override
    public Purpose createPurposeFromTemplate(PurposeTemplateRef purposeTemplateRef, PurposeCreateCommand createCommand) {
        return restClient.createPurposeFromTemplate(
                        purposeTemplateRef.id(),
                        commandFactory.creationFromTemplateRequest(createCommand, purposeTemplateRef)
                )
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(createdResource -> getPurpose(PurposeRef.of(createdResource.getId())))
                .updateContext()
                .get();
    }

    @Override
    public Purpose clonePurpose(PurposeRef purposeRef, PurposeCloneCommand cloneCommand) {
        if (!(cloneCommand instanceof BffPurposeCloneCommand bffCommand)) {
            throw new IllegalArgumentException(
                    "Command must be an instance of " + BffPurposeCloneCommand.class.getSimpleName()
            );
        }

        return restClient.clonePurpose(
                        purposeRef.id(),
                        bffCommand.getBffClonePayload()
                )
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(clonedResource -> getPurpose(PurposeRef.of(clonedResource.getPurposeId())))
                .updateContext()
                .get();
    }

    @Override
    public Purpose assignRiskAnalysisReviewer(
            PurposeRef purposeRef,
            AssignRiskAnalysisReviewerCommand assignCommand
    ) {
        if (!(assignCommand instanceof BffAssignRiskAnalysisReviewerCommand bffCommand)) {
            throw new IllegalArgumentException(
                    "Command must be an instance of " + BffAssignRiskAnalysisReviewerCommand.class.getSimpleName()
            );
        }

        restClient.assignRiskAnalysisReviewer(
                        purposeRef.id(),
                        bffCommand.getBffAssignmentPayload()
                )
                .withPolling(PollingStrategy.UNTIL_SUCCESS);

        return PollingUtils.pollUntil(
                () -> getPurpose(purposeRef),
                purpose -> purpose.getRiskAnalysisSigningState() == RiskAnalysisSigningState.ASSIGNED
                        && purpose.getRiskAnalysisReviewMode() == bffCommand.getReviewMode()
        );
    }

    @Override
    public Purpose submitRiskAnalysis(PurposeRef purposeRef, SubmitRiskAnalysisCommand submitCommand) {
        if (!(submitCommand instanceof BffSubmitRiskAnalysisCommand bffCommand)) {
            throw new IllegalArgumentException(
                    "Command must be an instance of " + BffSubmitRiskAnalysisCommand.class.getSimpleName()
            );
        }

        restClient.submitRiskAnalysis(
                        purposeRef.id(),
                        bffCommand.getBffSubmissionPayload()
                )
                .withPolling(PollingStrategy.UNTIL_SUCCESS);

        return PollingUtils.pollUntil(
                () -> getPurpose(purposeRef),
                purpose -> purpose.getRiskAnalysisSigningState() == RiskAnalysisSigningState.SUBMITTED
        );
    }

    @Override
    public Purpose signRiskAnalysis(PurposeRef purposeRef, SignRiskAnalysisCommand signCommand) {
        if (!(signCommand instanceof BffSignRiskAnalysisCommand bffCommand)) {
            throw new IllegalArgumentException(
                    "Command must be an instance of " + BffSignRiskAnalysisCommand.class.getSimpleName()
            );
        }

        restClient.signRiskAnalysis(
                        purposeRef.id(),
                        bffCommand.getBffSignPayload()
                )
                .withPolling(PollingStrategy.UNTIL_SUCCESS);

        return PollingUtils.pollUntil(
                () -> getPurpose(purposeRef),
                purpose -> purpose.getRiskAnalysisSigningState() == RiskAnalysisSigningState.SIGNED
        );
    }

    @Override
    public Purpose rejectRiskAnalysis(PurposeRef purposeRef, RejectRiskAnalysisCommand rejectCommand) {
        if (!(rejectCommand instanceof BffRejectRiskAnalysisCommand bffCommand)) {
            throw new IllegalArgumentException(
                    "Command must be an instance of " + BffRejectRiskAnalysisCommand.class.getSimpleName()
            );
        }

        restClient.rejectRiskAnalysis(
                        purposeRef.id(),
                        bffCommand.getBffRejectionPayload()
                )
                .withPolling(PollingStrategy.UNTIL_SUCCESS);

        return PollingUtils.pollUntil(
                () -> getPurpose(purposeRef),
                purpose -> purpose.getRiskAnalysisSigningState() == RiskAnalysisSigningState.REJECTED
        );
    }

    @Override
    public Purpose activatePurpose(PurposeRef purposeRef, PurposeVersionRef purposeVersionRef, @Nullable DelegationRef delegationRef) {
        it.pagopa.interop.generated.openapi.clients.bff.model.DelegationRef delegation = getDelegation(delegationRef);

        restClient.activatePurposeVersion(purposeRef.id(), purposeVersionRef.id(), delegation)
                .withPolling(PollingStrategy.UNTIL_SUCCESS);

        return PollingUtils.pollUntil(
                () -> getPurpose(purposeRef),
                purpose -> purpose.getLastActiveVersion() != null && purpose.getLastActiveVersion().getRef().equals(purposeVersionRef)
        );
    }

    @Override
    public Purpose rejectPurpose(PurposeRef purposeRef, PurposeVersionRef purposeVersionRef, String rejectionReason) {
        restClient.rejectPurposeVersion(
                purposeRef.id(), purposeVersionRef.id(), new RejectPurposeVersionPayload().rejectionReason(rejectionReason)
                ).withPolling(PollingStrategy.UNTIL_SUCCESS);

        return PollingUtils.pollUntil(
                () -> getPurpose(purposeRef),
                purpose -> purpose.findVersionById(purposeVersionRef.id())
                        .map(version -> version.getPurposeVersionState() == PurposeVersionState.REJECTED)
                        .orElse(false)
        );
    }

    @Override
    public Purpose askForApprovalPurpose(PurposeRef purposeRef, PurposeVersionRef purposeVersionRef, @Nullable DelegationRef delegationRef) {
        it.pagopa.interop.generated.openapi.clients.bff.model.DelegationRef delegation = getDelegation(delegationRef);

        restClient.activatePurposeVersion(purposeRef.id(), purposeVersionRef.id(), delegation)
                .withPolling(PollingStrategy.UNTIL_SUCCESS);

        return PollingUtils.pollUntil(
                () -> getPurpose(purposeRef),
                purpose -> purpose.getLastWaitingForApprovalVersion() != null && purpose.getLastWaitingForApprovalVersion().getRef().equals(purposeVersionRef)
        );
    }

    @Override
    public Purpose suspendPurpose(PurposeRef purposeRef, PurposeVersionRef purposeVersionRef, @Nullable DelegationRef delegationRef) {
        it.pagopa.interop.generated.openapi.clients.bff.model.DelegationRef delegation = getDelegation(delegationRef);

        restClient.suspendPurposeVersion(purposeRef.id(), purposeVersionRef.id(), delegation)
                .withPolling(PollingStrategy.UNTIL_SUCCESS);

        return PollingUtils.pollUntil(
                () -> getPurpose(purposeRef),
                purpose -> purpose.findVersionById(purposeVersionRef.id())
                                .orElseThrow(() -> new IllegalStateException("Purpose version not found"))
                                .getRef().equals(purposeVersionRef)
        );
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }

    private static it.pagopa.interop.generated.openapi.clients.bff.model.@NonNull DelegationRef getDelegation(@org.jspecify.annotations.Nullable DelegationRef delegationRef) {
        it.pagopa.interop.generated.openapi.clients.bff.model.DelegationRef delegation = new it.pagopa.interop.generated.openapi.clients.bff.model.DelegationRef();
        delegation.setDelegationId(delegationRef != null ? delegationRef.id() : null);
        return delegation;
    }

    @Override
    public void attemptNameUpdate(PurposeRef purposeRef, String name) {
        restClient.updatePurpose(purposeRef.id(), commandFactory.updateRequest().title(name)).withoutPolling();
    }

    @Override
    public void attemptPurposeCloning(PurposeRef purposeRef, PurposeCloneCommand cloneCommand) {
        if (!(cloneCommand instanceof BffPurposeCloneCommand bffCommand)) {
            throw new IllegalArgumentException(
                    "Command must be an instance of " + BffPurposeCloneCommand.class.getSimpleName()
            );
        }
        restClient.clonePurpose(purposeRef.id(), bffCommand.getBffClonePayload()).withoutPolling();
    }

    @Override
    public void attemptRiskAnalysisSigning(PurposeRef purposeRef, SignRiskAnalysisCommand signCommand) {
        if (!(signCommand instanceof BffSignRiskAnalysisCommand bffCommand)) {
            throw new IllegalArgumentException(
                    "Command must be an instance of " + BffSignRiskAnalysisCommand.class.getSimpleName()
            );
        }
        restClient.signRiskAnalysis(purposeRef.id(), bffCommand.getBffSignPayload()).withoutPolling();
    }

    @Override
    public void attemptRiskAnalysisRejection(PurposeRef purposeRef, RejectRiskAnalysisCommand rejectCommand) {
        if (!(rejectCommand instanceof BffRejectRiskAnalysisCommand bffCommand)) {
            throw new IllegalArgumentException(
                    "Command must be an instance of " + BffRejectRiskAnalysisCommand.class.getSimpleName()
            );
        }
        restClient.rejectRiskAnalysis(purposeRef.id(), bffCommand.getBffRejectionPayload()).withoutPolling();
    }
}
