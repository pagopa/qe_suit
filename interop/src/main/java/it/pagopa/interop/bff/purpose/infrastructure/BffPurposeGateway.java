package it.pagopa.interop.bff.purpose.infrastructure;

import it.pagopa.infrastructure.response.ApiResponse;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.bff.purpose.application.BffPurposeCreateCommand;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.DelegationRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysisRef;
import it.pagopa.interop.common.kernel.domain.PurposeRef;
import it.pagopa.interop.common.kernel.domain.PurposeTemplateRef;
import it.pagopa.interop.common.kernel.domain.PurposeVersionRef;
import it.pagopa.interop.common.purpose.application.PurposeCreateCommand;
import it.pagopa.interop.common.purpose.application.PurposeGateway;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisReviewMode;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;
import it.pagopa.utils.async.PollingUtils;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BffPurposeGateway implements PurposeGateway {
    private final BffPurposeRestClient restClient;
    private final BffPurposeMapper mapper;
    private final BffPurposeCommandFactory commandFactory;

    @Override
    public Purpose getPurpose(PurposeRef purposeRef) {
        return restClient.getPurpose(purposeRef.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(mapper::toPurpose)
                .updateContext()
                .get();
    }

    @Override
    public Purpose updatePurposeDailyCalls(PurposeRef purposeRef, int dailyCalls) {
        var response = restClient.createPurposeVersion(purposeRef.id(), new PurposeVersionSeed().dailyCalls(dailyCalls))
                .withPolling(PollingStrategy.UNTIL_SUCCESS);
        var createdVersion = response.get();
        final var purpose1 = PollingUtils.pollUntil(
                () -> getPurpose(purposeRef),
                purpose -> purpose.findVersionById(Objects.requireNonNull(createdVersion).getVersionId())
                        .map(version -> Integer.valueOf(dailyCalls).equals(version.getDailyCalls()))
                        .orElse(false)
        );
        return purpose1;
    }

    @Override
    public Purpose createPurpose(PurposeCreateCommand createCommand) {
        if (!(createCommand instanceof BffPurposeCreateCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of BffPurposeCreateCommand");

        PurposeSeed bffCreationPayload = bffCommand.getBffCreationPayload();
        return restClient.createPurpose(bffCreationPayload)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(createdResource -> getPurpose(PurposeRef.of(createdResource.getId())))
                .updateContext()
                .get();
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
    public Purpose archivePurpose(PurposeRef purposeRef, PurposeVersionRef purposeVersionRef) {
        var response = restClient.archivePurposeVersion(purposeRef.id(), purposeVersionRef.id()).withoutPolling();
        if (!response.getRaw().isSuccess()) {
            String status = response.getRaw() instanceof ApiResponse apiResponse
                    ? Integer.toString(apiResponse.getStatusCode()) : "unknown";
            throw new IllegalStateException(
                    "Purpose-version archive request was rejected for purpose " + purposeRef.id()
                            + " and version " + purposeVersionRef.id() + " (HTTP status " + status + ")"
            );
        }
        response.get();

        return PollingUtils.pollUntil(
                () -> getPurpose(purposeRef),
                purpose -> purpose.findVersionById(purposeVersionRef.id())
                        .map(version -> version.getPurposeVersionState() == PurposeVersionState.ARCHIVED)
                        .orElse(false),
                "Purpose version " + purposeVersionRef.id() + " did not become ARCHIVED for purpose " + purposeRef.id()
        );
    }

    @Override
    public void assignRiskAnalysisReviewers(
            PurposeRef purposeRef,
            RiskAnalysisReviewMode reviewMode,
            List<UUID> reviewerIds
    ) {
        RiskAnalysisAssignmentSeed assignment = new RiskAnalysisAssignmentSeed()
                .reviewMode(toBffReviewMode(reviewMode))
                .reviewerIds(List.copyOf(reviewerIds));

        restClient.assignRiskAnalysisReviewers(purposeRef.id(), assignment)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();

        List<UUID> expectedIds = Objects.requireNonNull(assignment.getReviewerIds()).stream().sorted().toList();
        PollingUtils.pollUntil(
                () -> {
                    var response = restClient.getPurpose(purposeRef.id()).withoutPolling();
                    if (!response.getRaw().isSuccess()) {
                        throw new IllegalStateException("Reviewer-assignment read failed for purpose " + purposeRef.id());
                    }
                    return response.get();
                },
                purpose -> {
                    ReviewerWorkflow workflow = purpose.getReviewerWorkflow();
                    if (workflow == null || workflow.getReviewers() == null) {
                        log.debug("Reviewer assignment for purpose {}, reviewMode {}: workflow or reviewers absent",
                                purposeRef.id(), reviewMode);
                        return false;
                    }
                    List<UUID> actualIds = workflow.getReviewers().stream()
                            .map(Reviewer::getUserId).sorted().toList();
                    log.debug("Reviewer assignment for purpose {}, reviewMode {}: expected IDs {}, actual IDs {}",
                            purposeRef.id(), reviewMode, expectedIds, actualIds);
                    return actualIds.equals(expectedIds);
                },
                "Reviewer assignment did not become visible for purpose " + purposeRef.id()
                        + ", reviewMode " + reviewMode + "; expected reviewer IDs: " + expectedIds
        );
    }

    @Override
    public void submitRiskAnalysis(PurposeRef purposeRef) {
        restClient.submitRiskAnalysis(purposeRef.id(), commandFactory.riskAnalysisSubmissionRequest())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();

        PollingUtils.pollUntil(
                () -> {
                    var response = restClient.getPurpose(purposeRef.id()).withoutPolling();
                    if (!response.getRaw().isSuccess()) {
                        throw new IllegalStateException("Risk-analysis submission read failed for purpose " + purposeRef.id());
                    }
                    return response.get();
                },
                purpose -> purpose.getReviewerWorkflow() != null
                        && purpose.getReviewerWorkflow().getSigningState() == RiskAnalysisSigningState.SUBMITTED,
                "Risk analysis did not become SUBMITTED for purpose " + purposeRef.id()
        );
    }

    @Override
    public RiskAnalysisForm getRiskAnalysisForm(PurposeRef purposeRef) {
        return readRiskAnalysisForm(purposeRef).orElseThrow(() ->
                new IllegalStateException("Risk-analysis form is incomplete or absent for purpose " + purposeRef.id()));
    }

    private Optional<RiskAnalysisForm> readRiskAnalysisForm(PurposeRef purposeRef) {
        var response = restClient.getPurpose(purposeRef.id()).withoutPolling();
        if (!response.getRaw().isSuccess()) {
            throw new IllegalStateException("Risk-analysis form read failed for purpose " + purposeRef.id());
        }
        var form = Objects.requireNonNull(response.get()).getRiskAnalysisForm();
        if (form == null) {
            return Optional.empty();
        }
        var answers = form.getAnswers().entrySet().stream().collect(
                Collectors.toUnmodifiableMap(
                        Map.Entry::getKey, entry -> List.copyOf(entry.getValue())
                )
        );
        return Optional.of(RiskAnalysisForm.builder()
                .version(form.getVersion()).answers(answers).build());
    }

    @Override
    public void saveRiskAnalysisForm(
            PurposeRef purposeRef, RiskAnalysisForm form
    ) {
        Objects.requireNonNull(form, "Risk-analysis form must not be null");
        var payload = new RiskAnalysisFormSeed().version(form.getVersion())
                .answers(form.getAnswers().entrySet().stream().collect(
                        Collectors.toMap(
                                Map.Entry::getKey, entry -> List.copyOf(entry.getValue())
                        )
                ));
        var expected = RiskAnalysisForm.builder()
                .version(payload.getVersion()).answers(Map.copyOf(payload.getAnswers())).build();
        var response = restClient.saveRiskAnalysisForm(purposeRef.id(), payload).withoutPolling();
        if (!response.getRaw().isSuccess()) {
            throw new IllegalStateException("Risk-analysis form save failed for purpose " + purposeRef.id());
        }
        PollingUtils.pollUntil(
                () -> readRiskAnalysisForm(purposeRef),
                formSnapshot -> formSnapshot.filter(expected::equals).isPresent(),
                "Saved risk-analysis form did not become visible for purpose " + purposeRef.id()
        );
    }

    @Override
    public void signRiskAnalysis(PurposeRef purposeRef) {
        var result = restClient.getPurpose(purposeRef.id()).withoutPolling();
        if (!(result.getRaw() instanceof ApiResponse response) || !response.isSuccess()) {
            throw new IllegalStateException("Cannot read metadata version for purpose " + purposeRef.id());
        }
        String header = response.getHeader("x-metadata-version");
        if (header == null || header.isBlank()) {
            throw new IllegalStateException("Missing x-metadata-version header for purpose " + purposeRef.id());
        }
        int metadataVersion;
        try {
            metadataVersion = Integer.parseInt(header.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalStateException("Invalid x-metadata-version header for purpose " + purposeRef.id(), ex);
        }
        if (metadataVersion < 0) {
            throw new IllegalStateException("Negative x-metadata-version header for purpose " + purposeRef.id());
        }
        signRiskAnalysis(purposeRef, metadataVersion);
    }

    @Override
    public void signRiskAnalysis(PurposeRef purposeRef, int metadataVersionToSign) {
        RiskAnalysisSignSeed signRequest = new RiskAnalysisSignSeed()
                .metadataVersionToSign(metadataVersionToSign);

        restClient.signRiskAnalysis(purposeRef.id(), signRequest)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();

        PollingUtils.pollUntil(
                () -> {
                    var response = restClient.getPurpose(purposeRef.id()).withoutPolling();
                    if (!response.getRaw().isSuccess()) {
                        throw new IllegalStateException("Risk-analysis signing read failed for purpose " + purposeRef.id());
                    }
                    return response.get();
                },
                purpose -> purpose.getReviewerWorkflow() != null
                        && purpose.getReviewerWorkflow().getSigningState() == RiskAnalysisSigningState.SIGNED,
                "Risk analysis did not become SIGNED for purpose " + purposeRef.id()
        );
    }

    @Override
    public void rejectRiskAnalysis(PurposeRef purposeRef, String rejectionReason) {
        RiskAnalysisRejectionSeed rejectionRequest = new RiskAnalysisRejectionSeed()
                .rejectionReason(rejectionReason);

        restClient.rejectRiskAnalysis(purposeRef.id(), rejectionRequest)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();
    }

    private static it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisReviewMode toBffReviewMode(
            RiskAnalysisReviewMode reviewMode
    ) {
        return switch (reviewMode) {
            case ADMIN_WRITES_ADMIN_SIGNS ->
                    it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisReviewMode.ADMIN_WRITES_ADMIN_SIGNS;
            case ADMIN_WRITES_REVIEWER_SIGNS ->
                    it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisReviewMode.ADMIN_WRITES_REVIEWER_SIGNS;
            case REVIEWER_WRITES_REVIEWER_SIGNS ->
                    it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisReviewMode.REVIEWER_WRITES_REVIEWER_SIGNS;
        };
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
}
