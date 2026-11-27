package it.pagopa.interop.bff.purpose.infrastructure;

import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.response.ApiResponse;
import it.pagopa.infrastructure.template.action.PollingAction;
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
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import it.pagopa.interop.generated.openapi.clients.bff.model.PatchPurposeUpdateContent;
import it.pagopa.interop.generated.openapi.clients.bff.model.PatchReversePurposeUpdateContent;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RejectPurposeVersionPayload;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisReviewMode;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;

import it.pagopa.utils.async.PollingUtils;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Service
@RequiredArgsConstructor
public class BffPurposeGateway implements PurposeGateway {
    private final BffPurposeRestClient restClient;
    private final BffPurposeMapper mapper;
    private final BffPurposeCommandFactory commandFactory;
    private final LastApiResponseStore lastApiResponseStore;

    @Override
    public void shouldFailLinkToReceiveEService() {
        ApiResponse lastResponse = lastApiResponseStore.getLastResponse();
        Assertions.assertThat(lastResponse)
                .as("Risposta al tentativo di creare una finalità con e-service in ricezione")
                .isNotNull();
        Assertions.assertThat(lastResponse.getStatusCode())
                .as("La creazione della finalità con e-service in ricezione deve essere rifiutata con HTTP 409")
                .isEqualTo(400);
    }

    @Override
    public void shouldFailToUpdatePurposeMetadataAsMalformed() {
        assertThat(lastApiResponseStore.getLastResponse())
                .as("Risposta al tentativo di modifica dei metadati della finalita")
                .isNotNull()
                .extracting(ApiResponse::getStatusCode)
                .as("Modifica dei metadati della finalita malformata")
                .isEqualTo(400);
    }

    @Override
    public void shouldFailToUpdatePurposeMetadataAsForbidden() {
        assertThat(lastApiResponseStore.getLastResponse())
                .as("Risposta al tentativo di modifica dei metadati della finalita")
                .isNotNull()
                .extracting(ApiResponse::getStatusCode)
                .as("Modifica dei metadati della finalita vietata")
                .isEqualTo(403);
    }

    @Override
    public void shouldFailToUpdatePurposeMetadataAsConflict() {
        assertThat(lastApiResponseStore.getLastResponse())
                .as("Risposta al tentativo di modifica dei metadati della finalita")
                .isNotNull()
                .extracting(ApiResponse::getStatusCode)
                .as("Modifica dei metadati della finalita in conflitto")
                .isEqualTo(409);
    }

    @Override
    public Purpose getPurpose(PurposeRef purposeRef) {
        return restClient.getPurpose(purposeRef.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(mapper::toPurpose)
                .updateContext()
                .get();
    }

    @Override
    public Purpose updatePurposeMetadata(
            PurposeRef purposeRef, String name, String description,
            @Nullable Boolean isFreeOfCharge, @Nullable String freeOfChargeReason, @Nullable Integer dailyCalls) {
        PatchPurposeUpdateContent payload = new PatchPurposeUpdateContent()
                .title(name)
                .description(description)
                .isFreeOfCharge(isFreeOfCharge)
                .freeOfChargeReason(freeOfChargeReason)
                .dailyCalls(dailyCalls);

        return restClient.patchUpdatePurpose(purposeRef.id(), payload)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(ignored -> getPurpose(purposeRef))
                .updateContext()
                .get();
    }

    @Override
    public Purpose updateReversePurposeMetadata(
            PurposeRef purposeRef, String name, String description,
            @Nullable Boolean isFreeOfCharge, @Nullable String freeOfChargeReason, @Nullable Integer dailyCalls) {
        PatchReversePurposeUpdateContent payload = new PatchReversePurposeUpdateContent()
                .title(name)
                .description(description)
                .isFreeOfCharge(isFreeOfCharge)
                .freeOfChargeReason(freeOfChargeReason)
                .dailyCalls(dailyCalls);

        return restClient.patchUpdateReversePurpose(purposeRef.id(), payload)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(ignored -> getPurpose(purposeRef))
                .updateContext()
                .get();
    }

    @Override
    public void tryToUpdatePurposeMetadata(
            PurposeRef purposeRef, String name, String description,
            @Nullable Boolean isFreeOfCharge, @Nullable String freeOfChargeReason, @Nullable Integer dailyCalls) {
        PatchPurposeUpdateContent payload = new PatchPurposeUpdateContent()
                .title(name)
                .description(description)
                .isFreeOfCharge(isFreeOfCharge)
                .freeOfChargeReason(freeOfChargeReason)
                .dailyCalls(dailyCalls);
        restClient.patchUpdatePurpose(purposeRef.id(), payload).withoutPolling();
    }

    @Override
    public void tryToUpdateReversePurposeMetadata(
            PurposeRef purposeRef, String name, String description,
            @Nullable Boolean isFreeOfCharge, @Nullable String freeOfChargeReason, @Nullable Integer dailyCalls) {
        PatchReversePurposeUpdateContent payload = new PatchReversePurposeUpdateContent()
                .title(name)
                .description(description)
                .isFreeOfCharge(isFreeOfCharge)
                .freeOfChargeReason(freeOfChargeReason)
                .dailyCalls(dailyCalls);
        restClient.patchUpdateReversePurpose(purposeRef.id(), payload).withoutPolling();
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
        restClient.archivePurposeVersion(purposeRef.id(), purposeVersionRef.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS);

        return PollingUtils.pollUntil(
                () -> getPurpose(purposeRef),
                purpose -> purpose.findVersionById(purposeVersionRef.id())
                        .map(version -> version.getPurposeVersionState() == PurposeVersionState.ARCHIVED)
                        .orElse(false)
        );
    }

    @Override
    public boolean supports(@NonNull Channel delimiter) {
        return delimiter == Channel.BFF;
    }

    @Override
    public Purpose tryCreatePurpose(PurposeCreateCommand createCommand) {
        if (!(createCommand instanceof BffPurposeCreateCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of BffPurposeCreateCommand");

        PurposeSeed bffCreationPayload = bffCommand.getBffCreationPayload();
        var response = restClient.createPurpose(bffCreationPayload)
                .withoutPolling();

        return getPurpose(response);
    }

    @Override
    public Purpose tryCreatePurposeFromTemplate(PurposeTemplateRef purposeTemplateRef, EServiceRef eServiceRef) {
        var response = restClient.createPurposeFromTemplate(purposeTemplateRef.id(), commandFactory.creationFromTemplateRequest(eServiceRef, purposeTemplateRef))
                .withoutPolling();

        return getPurpose(response);
    }

    private Purpose getPurpose(
            PollingAction<CreatedResource> response) {
        if (!response.getRaw().isSuccess()) {
            return null;
        }

        return response
                .map(createdResource -> getPurpose(PurposeRef.of(createdResource.getId())))
                .updateContext()
                .get();
    }

    private static it.pagopa.interop.generated.openapi.clients.bff.model.@NonNull DelegationRef getDelegation(@org.jspecify.annotations.Nullable DelegationRef delegationRef) {
        it.pagopa.interop.generated.openapi.clients.bff.model.DelegationRef delegation = new it.pagopa.interop.generated.openapi.clients.bff.model.DelegationRef();
        delegation.setDelegationId(delegationRef != null ? delegationRef.id() : null);
        return delegation;
    }

    @Override
    public void assignRiskAnalysisReviewers(PurposeRef purposeRef, RiskAnalysisReviewMode reviewMode, List<UUID> reviewerIds) {
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
                        return false;
                    }
                    List<UUID> actualIds = workflow.getReviewers().stream().map(Reviewer::getUserId).sorted().toList();
                    return actualIds.equals(expectedIds) && assignment.getReviewMode() == purpose.getRiskAnalysisReviewMode();
                },
                "Reviewer assignment did not become visible for purpose " + purposeRef.id()
                        + ", reviewMode " + reviewMode + "; expected reviewer IDs: " + expectedIds
        );
    }

    @Override
    public void submitRiskAnalysis(PurposeRef purposeRef) {
        submitRiskAnalysis(purposeRef, getRiskAnalysisForm(purposeRef));
    }

    @Override
    public void submitRiskAnalysis(PurposeRef purposeRef, RiskAnalysisForm form) {
        Objects.requireNonNull(form, "Risk-analysis form must not be null");
        RiskAnalysisSubmissionSeed submission = new RiskAnalysisSubmissionSeed()
                .riskAnalysisForm(toRiskAnalysisFormSeed(form));
        restClient.submitRiskAnalysis(purposeRef.id(), submission)
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
        if (form == null || form.getVersion() == null || form.getVersion().isBlank() || form.getAnswers() == null) {
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
        var payload = toRiskAnalysisFormSeed(form);
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

    private static RiskAnalysisFormSeed toRiskAnalysisFormSeed(RiskAnalysisForm form) {
        return new RiskAnalysisFormSeed().version(form.getVersion())
                .answers(form.getAnswers().entrySet().stream().collect(
                        Collectors.toMap(Map.Entry::getKey, entry -> List.copyOf(entry.getValue()))
                ));
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

        var signingResponse = restClient.signRiskAnalysis(purposeRef.id(), signRequest)
                .withoutPolling().getRaw();
        if (!(signingResponse instanceof ApiResponse apiResponse)) {
            throw new IllegalStateException("Cannot read signing response for purpose " + purposeRef.id()
                    + ", metadataVersionToSign " + metadataVersionToSign);
        }
        if (!apiResponse.isSuccess()) {
            throw new IllegalStateException("Risk-analysis signing rejected for purpose " + purposeRef.id()
                    + ", metadataVersionToSign " + metadataVersionToSign
                    + " (HTTP status " + apiResponse.getStatusCode() + ")");
        }

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

        var rejectionResponse = restClient.rejectRiskAnalysis(purposeRef.id(), rejectionRequest)
                .withoutPolling().getRaw();
        if (!(rejectionResponse instanceof ApiResponse apiResponse)) {
            throw new IllegalStateException("Cannot read rejection response for purpose " + purposeRef.id());
        }
        if (!apiResponse.isSuccess()) {
            throw new IllegalStateException("Risk-analysis rejection rejected for purpose " + purposeRef.id()
                    + " (HTTP status " + apiResponse.getStatusCode() + ")");
        }
        PollingUtils.pollUntil(
                () -> {
                    var response = restClient.getPurpose(purposeRef.id()).withoutPolling();
                    if (!response.getRaw().isSuccess()) {
                        throw new IllegalStateException("Risk-analysis rejection read failed for purpose " + purposeRef.id());
                    }
                    return response.get();
                },
                purpose -> purpose.getReviewerWorkflow() != null
                        && purpose.getReviewerWorkflow().getSigningState() == RiskAnalysisSigningState.REJECTED,
                "Risk analysis did not become REJECTED for purpose " + purposeRef.id()
        );
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
}
