package it.pagopa.interop.service.purpose.impl;

import it.pagopa.interop.domain.context.PurposeContext;
import it.pagopa.interop.domain.model.Eservice;
import it.pagopa.interop.domain.model.Purpose;
import it.pagopa.interop.domain.model.RiskAnalysis;
import it.pagopa.interop.service.purpose.PurposeService;
import it.pagopa.interop.service.risk_analysis.impl.RiskAnalysisDataPreparationService;
import it.pagopa.interop.generated.openapi.clients.bff.api.PurposesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionState;
import it.pagopa.interop.domain.context.CurrentUserContext;
import it.pagopa.interop.utils.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PurposeDataPreparationService implements PurposeService {

    private final PurposesApi purposesApi;
    private final PurposeContext context;
    private final RiskAnalysisDataPreparationService riskAnalysisService;
    private final CurrentUserContext currentUserContext;

    @Override
    public Purpose createEservicePurpose(Eservice eservice) {
        return createEservicePurpose(eservice, null);
    }

    @Override
    public Purpose createEservicePurpose(Eservice eservice, Consumer<PurposeSeed> overrides) {
        PurposeSeed seed = buildDefaultPurposeSeed(eservice);
        if (overrides != null) {
            overrides.accept(seed);
        }

        CreatedResource created = poll(
                () -> purposesApi.createPurpose(seed),
                resp -> resp != null && resp.getId() != null,
                Duration.ofSeconds(20)
        );

        return getPurpose(created.getId());
    }

    @Override
    public Purpose createEservicePurposeWithState(Eservice eservice, PurposeVersionState targetState) {
        return createEservicePurposeWithState(eservice, targetState, null);
    }

    @Override
    public Purpose createEservicePurposeWithState(Eservice eservice, PurposeVersionState targetState, Consumer<PurposeSeed> overrides) {
        Purpose purpose = createEservicePurpose(eservice, overrides);
        UUID purposeId = purpose.getId();

        UUID currentVersionId = waitCurrentVersion(purposeId);
        if (targetState == PurposeVersionState.DRAFT) return purpose;

        activateVersion(purposeId, currentVersionId);

        if (targetState == PurposeVersionState.WAITING_FOR_APPROVAL) {
            waitVersionInState(purposeId, PurposeVersionState.WAITING_FOR_APPROVAL);
            return getPurpose(purposeId);
        }

        waitVersionInState(purposeId, PurposeVersionState.ACTIVE);

        if (targetState == PurposeVersionState.SUSPENDED) {
            suspendVersion(purposeId, currentVersionId);
            waitVersionInState(purposeId, PurposeVersionState.SUSPENDED);
        }

        if (targetState == PurposeVersionState.ARCHIVED) {
            archiveVersion(purposeId, currentVersionId);
            waitVersionInState(purposeId, PurposeVersionState.ARCHIVED);
        }

        return getPurpose(purposeId);
    }

    @Override
    public Purpose getPurpose(UUID purposeId) {
        Purpose purpose = poll(
                () -> new Purpose(purposesApi.getPurpose(purposeId)),
                resp -> resp != null && Objects.equals(purposeId, resp.getId()),
                Duration.ofSeconds(20)
        );

        context.upsert(purpose);
        return purpose;
    }

    private PurposeSeed buildDefaultPurposeSeed(Eservice eservice) {
        RiskAnalysis riskAnalysis = riskAnalysisService.createRiskAnalysis();
        UUID consumerId = currentUserContext.getTenant().getOrganizationId();
        String title = "purpose-" + UUID.randomUUID().toString().substring(0, 8);

        return new PurposeSeed()
                .title(title)
                .description("Default purpose description")
                .isFreeOfCharge(true)
                .freeOfChargeReason("free of charge")
                .dailyCalls(1)
                .eserviceId(eservice.getEserviceId())
                .consumerId(consumerId)
                .riskAnalysisForm(riskAnalysis.getForm());
    }

    private UUID waitCurrentVersion(UUID purposeId) {
        it.pagopa.interop.generated.openapi.clients.bff.model.Purpose bffPurpose =
                poll(
                        () -> purposesApi.getPurpose(purposeId),
                        resp -> resp != null && resp.getCurrentVersion() != null,
                        Duration.ofSeconds(20)
                );
        return bffPurpose.getCurrentVersion().getId();
    }

    private void waitVersionInState(UUID purposeId, PurposeVersionState expectedState) {
        poll(
                () -> purposesApi.getPurpose(purposeId),
                resp -> expectedState == Optional.ofNullable(resp.getCurrentVersion())
                        .map(PurposeVersion::getState)
                        .orElse(null)
                        || expectedState == Optional.ofNullable(resp.getWaitingForApprovalVersion())
                        .map(PurposeVersion::getState)
                        .orElse(null),
                Duration.ofSeconds(20)
        );
    }

    private void activateVersion(UUID purposeId, UUID versionId) {
        purposesApi.activatePurposeVersion(purposeId, versionId, null);
    }

    private void suspendVersion(UUID purposeId, UUID versionId) {
        purposesApi.suspendPurposeVersion(purposeId, versionId, null);
    }

    private void archiveVersion(UUID purposeId, UUID versionId) {
        purposesApi.archivePurposeVersion(purposeId, versionId);
    }

    private <T> T poll(Supplier<T> supplier, Predicate<T> predicate, Duration timeout) {
        return PollingUtils.pollUntil(
                supplier,
                predicate,
                timeout,
                Duration.ofSeconds(2)
        );
    }
}