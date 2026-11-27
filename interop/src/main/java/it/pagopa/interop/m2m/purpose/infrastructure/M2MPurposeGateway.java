package it.pagopa.interop.m2m.purpose.infrastructure;

import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.response.ApiResponse;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
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
import it.pagopa.interop.common.purpose.domain.PurposeCreationFailureReason;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.PurposeVersionSeed;
import it.pagopa.utils.async.PollingUtils;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import static org.assertj.core.api.Assertions.assertThat;

@Service
@RequiredArgsConstructor
public class M2MPurposeGateway implements PurposeGateway {

    private final M2MPurposeRestClient restClient;
    private final M2MPurposeMapper mapper;
    private final M2MPurposeRequestFactory requestFactory;
    private final LastApiResponseStore lastApiResponseStore;

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
    public Purpose updatePurposeMetadata(
            PurposeRef purposeRef, String name, String description,
            Boolean isFreeOfCharge, String freeOfChargeReason, Integer dailyCalls) {
        return restClient.updatePurposeMetadata(
                        purposeRef.id(), name, description, isFreeOfCharge, freeOfChargeReason, dailyCalls)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(ignored -> getPurpose(purposeRef))
                .updateContext()
                .get();
    }

    @Override
    public Purpose updateReversePurposeMetadata(
            PurposeRef purposeRef, String name, String description,
            Boolean isFreeOfCharge, String freeOfChargeReason, Integer dailyCalls) {
        return restClient.updateReversePurposeMetadata(
                        purposeRef.id(), name, description, isFreeOfCharge, freeOfChargeReason, dailyCalls)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(ignored -> getPurpose(purposeRef))
                .updateContext()
                .get();
    }

    @Override
    public void tryToUpdatePurposeMetadata(
            PurposeRef purposeRef, String name, String description,
            Boolean isFreeOfCharge, String freeOfChargeReason, Integer dailyCalls) {
        restClient.updatePurposeMetadata(
                purposeRef.id(), name, description, isFreeOfCharge, freeOfChargeReason, dailyCalls).withoutPolling();
    }

    @Override
    public void tryToUpdateReversePurposeMetadata(
            PurposeRef purposeRef, String name, String description,
            Boolean isFreeOfCharge, String freeOfChargeReason, Integer dailyCalls) {
        restClient.updateReversePurposeMetadata(
                purposeRef.id(), name, description, isFreeOfCharge, freeOfChargeReason, dailyCalls).withoutPolling();
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
    public Purpose updatePurposeDailyCalls(PurposeRef purposeRef, int dailyCalls) {
        var response = restClient.createPurposeVersion(purposeRef.id(), new PurposeVersionSeed().dailyCalls(dailyCalls))
                .withPolling(PollingStrategy.UNTIL_SUCCESS);
        var createdVersion = response.get();
        return PollingUtils.pollUntil(
                () -> getPurpose(purposeRef),
                purpose -> purpose.findVersionById(createdVersion.getId())
                        .map(version -> Integer.valueOf(dailyCalls).equals(version.getDailyCalls()))
                        .orElse(false)
        );
    }

    @Override
    public Purpose createPurpose(PurposeCreateCommand createCommand) {
        return restClient.createPurpose(mapper.toM2MPurposeSeed(createCommand))
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(createdResource -> getPurpose(PurposeRef.of(createdResource.getId())))
                .updateContext()
                .get();
    }

    @Override
    public void shouldFailToCreatePurpose(PurposeCreateCommand createCommand, PurposeCreationFailureReason reason) {
        throw unsupportedOperation();
    }

    @Override
    public Purpose createReversePurpose(EServiceRef eServiceRef, EServiceRiskAnalysisRef riskAnalysisRef) {
        throw unsupportedOperation();
    }

    @Override
    public Purpose createPurposeFromTemplate(PurposeTemplateRef purposeTemplateRef, EServiceRef eServiceRef) {
        return restClient.createPurposeFromTemplate(
                        purposeTemplateRef.id(),
                        requestFactory.creationFromTemplateRequest(eServiceRef, purposeTemplateRef)
                )
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(createdPurpose -> getPurpose(PurposeRef.of(createdPurpose.getId())))
                .updateContext()
                .get();
    }

    @Override
    public Purpose createPurposeFromTemplate(PurposeTemplateRef purposeTemplateRef, PurposeCreateCommand createCommand) {
        throw unsupportedOperation();
    }

    @Override
    public Purpose clonePurpose(PurposeRef purposeRef, PurposeCloneCommand cloneCommand) {
        throw unsupportedOperation();
    }

    @Override
    public Purpose assignRiskAnalysisReviewer(PurposeRef purposeRef, AssignRiskAnalysisReviewerCommand assignCommand) {
        throw unsupportedOperation();
    }

    @Override
    public Purpose submitRiskAnalysis(PurposeRef purposeRef, SubmitRiskAnalysisCommand submitCommand) {
        throw unsupportedOperation();
    }

    @Override
    public Purpose signRiskAnalysis(PurposeRef purposeRef, SignRiskAnalysisCommand signCommand) {
        throw unsupportedOperation();
    }

    @Override
    public Purpose rejectRiskAnalysis(PurposeRef purposeRef, RejectRiskAnalysisCommand rejectCommand) {
        throw unsupportedOperation();
    }

    @Override
    public Purpose activatePurpose(PurposeRef purposeRef, PurposeVersionRef purposeVersionRef, @Nullable DelegationRef delegationRef) {
        it.pagopa.interop.generated.openapi.clients.m2m.v3.model.DelegationRef delegation =
                new it.pagopa.interop.generated.openapi.clients.m2m.v3.model.DelegationRef();
        delegation.setDelegationId(delegationRef != null ? delegationRef.id() : null);

        restClient.approvePurpose(purposeRef.id(), delegation)
                .withPolling(PollingStrategy.UNTIL_SUCCESS);

        return PollingUtils.pollUntil(
                () -> getPurpose(purposeRef),
                purpose -> purpose.findVersionById(purposeVersionRef.id())
                        .map(version -> version.getPurposeVersionState() == PurposeVersionState.ACTIVE)
                        .orElse(false)
        );
    }

    @Override
    public Purpose rejectPurpose(PurposeRef purposeRef, PurposeVersionRef purposeVersionRef, String rejectionReason) {
        restClient.rejectPurpose(purposeRef.id(), rejectionReason)
                .withPolling(PollingStrategy.UNTIL_SUCCESS);

        return PollingUtils.pollUntil(
                () -> getPurpose(purposeRef),
                purpose -> purpose.findVersionById(purposeVersionRef.id())
                        .map(version -> version.getPurposeVersionState() == PurposeVersionState.REJECTED)
                        .orElse(false)
        );
    }

    @Override
    public Purpose askForApprovalPurpose(PurposeRef purposeRef, PurposeVersionRef purposeVersionRef, @Nullable DelegationRef delegationRef) {
        it.pagopa.interop.generated.openapi.clients.m2m.v3.model.DelegationRef delegation =
                new it.pagopa.interop.generated.openapi.clients.m2m.v3.model.DelegationRef();
        delegation.setDelegationId(delegationRef != null ? delegationRef.id() : null);

        restClient.approvePurpose(purposeRef.id(), delegation)
                .withPolling(PollingStrategy.UNTIL_SUCCESS);

        return PollingUtils.pollUntil(
                () -> getPurpose(purposeRef),
                purpose -> purpose.findVersionById(purposeVersionRef.id())
                        .map(version -> version.getPurposeVersionState() == PurposeVersionState.WAITING_FOR_APPROVAL)
                        .orElse(false)
        );
    }

    @Override
    public Purpose suspendPurpose(PurposeRef purposeRef, PurposeVersionRef purposeVersionRef, @Nullable DelegationRef delegationRef) {
        throw unsupportedOperation();
    }

    @Override
    public Purpose archivePurpose(PurposeRef purposeRef, PurposeVersionRef purposeVersionRef) {
        restClient.archivePurpose(purposeRef.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS);

        return PollingUtils.pollUntil(
                () -> getPurpose(purposeRef),
                purpose -> purpose.findVersionById(purposeVersionRef.id())
                        .map(version -> version.getPurposeVersionState() == PurposeVersionState.ARCHIVED)
                        .orElse(false)
        );
    }

    @Override
    public boolean supports(@NonNull Channel channel) {
        return channel == Channel.M2M_V3;
    }

    private static UnsupportedOperationException unsupportedOperation() {
        return new UnsupportedOperationException("This purpose operation is not supported on M2M v3");
    }

    @Override
    public void attemptNameUpdate(PurposeRef purposeRef, String name) {
        throw unsupportedOperation();
    }

    @Override
    public void attemptPurposeCloning(PurposeRef purposeRef, PurposeCloneCommand cloneCommand) {
        throw unsupportedOperation();
    }

    @Override
    public void attemptRiskAnalysisSigning(PurposeRef purposeRef, SignRiskAnalysisCommand signCommand) {
        throw unsupportedOperation();
    }

    @Override
    public void attemptRiskAnalysisRejection(PurposeRef purposeRef, RejectRiskAnalysisCommand rejectCommand) {
        throw unsupportedOperation();
    }

    @Override
    public void attemptCreatePurpose(PurposeCreateCommand createCommand) {
        throw unsupportedOperation();
    }

    @Override
    public void attemptCreateReversePurpose(
            EServiceRef eServiceRef,
            EServiceRiskAnalysisRef riskAnalysisRef,
            String title
    ) {
        throw unsupportedOperation();
    }

    @Override
    public void attemptCreatePurposeFromTemplate(
            PurposeTemplateRef purposeTemplateRef,
            PurposeCreateCommand createCommand
    ) {
        throw unsupportedOperation();
    }

    @Override
    public void attemptNameUpdateFromTemplate(
            PurposeTemplateRef purposeTemplateRef,
            PurposeRef purposeRef,
            String name
    ) {
        throw unsupportedOperation();
    }

    @Override
    public void attemptRiskAnalysisFormUpdate(PurposeRef purposeRef) {
        throw unsupportedOperation();
    }
}
