package it.pagopa.interop.m2m.purpose.infrastructure;

import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;

import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
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
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisReviewMode;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.PurposeVersionSeed;
import it.pagopa.utils.async.PollingUtils;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class M2MPurposeGateway implements PurposeGateway {
    @Override
    public RiskAnalysisForm getRiskAnalysisForm(PurposeRef purposeRef) {
        throw new UnsupportedOperationException("Risk-analysis form reads are not supported by M2M");
    }

    @Override
    public void saveRiskAnalysisForm(
            PurposeRef purposeRef, RiskAnalysisForm form
    ) {
        throw new UnsupportedOperationException("Risk-analysis form saves are not supported by M2M");
    }


    private final M2MPurposeRestClient restClient;
    private final M2MPurposeMapper mapper;

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
    public Purpose createReversePurpose(EServiceRef eServiceRef, EServiceRiskAnalysisRef riskAnalysisRef) {
        throw unsupportedOperation();
    }

    @Override
    public Purpose createPurposeFromTemplate(PurposeTemplateRef purposeTemplateRef, EServiceRef eServiceRef) {
        throw unsupportedOperation();
    }

    @Override
    public Purpose createPurposeFromTemplate(PurposeTemplateRef purposeTemplateRef, PurposeCreateCommand createCommand) {
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
        // TODO con il rilascio della 2.25 verrà introdotto l'endpoint per rifiutare una finalità in M2M V3
        throw new UnsupportedOperationException("Purpose rejection is not supported on M2M v3");
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
    public void assignRiskAnalysisReviewers(PurposeRef purposeRef, RiskAnalysisReviewMode reviewMode, List<UUID> reviewerIds) {
        throw unsupportedOperation();
    }

    @Override
    public void submitRiskAnalysis(PurposeRef purposeRef) {
        throw unsupportedOperation();
    }

    @Override
    public void signRiskAnalysis(PurposeRef purposeRef, int metadataVersionToSign) {
        throw unsupportedOperation();
    }

    @Override
    public void signRiskAnalysis(PurposeRef purposeRef) {
        throw unsupportedOperation();
    }

    @Override
    public void rejectRiskAnalysis(PurposeRef purposeRef, String rejectionReason) {
        throw unsupportedOperation();
    }

    @Override
    public boolean supports(@NonNull Channel channel) {
        return channel == Channel.M2M_V3;
    }

    private static UnsupportedOperationException unsupportedOperation() {
        return new UnsupportedOperationException("This purpose operation is not supported on M2M v3");
    }
}
