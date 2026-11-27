package it.pagopa.interop.m2m.agreement.infrastructure;

import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.agreement.application.AgreementGateway;
import it.pagopa.interop.common.agreement.domain.Agreement;
import it.pagopa.interop.common.agreement.domain.AgreementCreationFailureReason;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.kernel.domain.AgreementRef;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Delegation;
import it.pagopa.interop.common.kernel.domain.Document;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.AgreementSeed;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.AgreementSubmission;
import it.pagopa.utils.async.PollingUtils;
import jakarta.annotation.Nullable;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class M2MAgreementGateway implements AgreementGateway {

    private final M2MAgreementRestClient restClient;
    private final M2MAgreementMapper mapper;

    @Override
    public void viewConsumerAgreementDetails(Agreement agreement) {
        throw unsupportedOperation("viewConsumerAgreementDetails");
    }

    @Override
    public void viewProducerAgreementDetails(Agreement agreement) {
        throw unsupportedOperation("viewProducerAgreementDetails");
    }

    @Override
    public void verifyConsumerAgreementDownloadAvailability(Agreement agreement) {
        throw unsupportedOperation("verifyConsumerAgreementDownloadAvailability");
    }

    @Override
    public void verifyProducerAgreementDownloadAvailability(Agreement agreement) {
        throw unsupportedOperation("verifyProducerAgreementDownloadAvailability");
    }

    @Override
    public void ensureActiveAgreement(EService eService, EServiceDescriptor descriptor) {
        throw unsupportedOperation("ensureActiveAgreement");
    }

    @Override
    public Agreement createAgreement(EService eService, EServiceDescriptor descriptor, @Nullable Delegation delegation) {
        AgreementSeed payload = new AgreementSeed()
                .eserviceId(eService.getId())
                .descriptorId(descriptor.getId())
                .delegationId(delegation != null ? delegation.getId() : null);

        return restClient.create(payload)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(mapper::toAgreement)
                .updateContext()
                .get();
    }

    @Override
    public void shouldFailToCreateAgreement(
            EService eService,
            EServiceDescriptor descriptor,
            @Nullable Delegation delegation,
            AgreementCreationFailureReason reason
    ) {
        throw unsupportedOperation("shouldFailToCreateAgreement");
    }

    @Override
    public Agreement getAgreement(AgreementRef ref) {
        return restClient.read(ref.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(mapper::toAgreement)
                .updateContext()
                .get();
    }

    @Override
    public Agreement submitAgreement(Agreement agreement) {
        return restClient.submit(
                        agreement.getId(),
                        new AgreementSubmission().consumerNotes("consumerNotes")
                )
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(mapper::toAgreement)
                .updateContext()
                .get();
    }

    @Override
    public void attemptAgreementSubmission(Agreement agreement) {
        throw unsupportedOperation("attemptAgreementSubmission");
    }

    @Override
    public Agreement activateAgreement(Agreement agreement, @Nullable Delegation delegation) {
        throw unsupportedOperation("activateAgreement");
    }

    @Override
    public void attemptAgreementActivation(Agreement agreement) {
        throw unsupportedOperation("attemptAgreementActivation");
    }

    @Override
    public Agreement suspendAgreement(Agreement agreement, @Nullable Delegation delegation) {
        throw unsupportedOperation("suspendAgreement");
    }

    @Override
    public Agreement rejectAgreement(Agreement agreement) {
        throw unsupportedOperation("rejectAgreement");
    }

    @Override
    public Agreement archiveAgreement(Agreement agreement) {
        throw unsupportedOperation("archiveAgreement");
    }

    @Override
    public Agreement awaitState(Agreement agreement, Set<AgreementState> expectedStates) {
        if (expectedStates.isEmpty()) {
            throw new IllegalArgumentException("At least one expected agreement state is required");
        }
        return PollingUtils.pollUntil(
                () -> getAgreement(agreement.getRef()),
                current -> {
                    AgreementState state = current.getState();
                    if (expectedStates.contains(state)) {
                        return true;
                    }
                    if (state == null || state == AgreementState.REJECTED
                            || state == AgreementState.MISSING_CERTIFIED_ATTRIBUTES
                            || state == AgreementState.ARCHIVED) {
                        throw new IllegalStateException(
                                "Agreement reached state " + state + " while awaiting " + expectedStates
                        );
                    }
                    return false;
                }
        );
    }

    @Override
    public Agreement awaitContractAvailable(Agreement agreement) {
        throw unsupportedOperation("awaitContractAvailable");
    }

    @Override
    public Agreement addConsumerDocument(Agreement agreement, @Nullable String fileName) {
        throw unsupportedOperation("addConsumerDocument");
    }

    @Override
    public Agreement removeConsumerDocument(Agreement agreement, Document document) {
        throw unsupportedOperation("removeConsumerDocument");
    }

    @Override
    public void attemptAgreementSuspension(Agreement agreement) {
        throw unsupportedOperation("attemptAgreementSuspension");
    }

    @Override
    public void attemptAgreementReactivation(Agreement agreement) {
        throw unsupportedOperation("attemptAgreementReactivation");
    }

    @Override
    public void attemptAgreementArchiving(Agreement agreement) {
        throw unsupportedOperation("attemptAgreementArchiving");
    }

    @Override
    public void attemptConsumerDocumentUpload(Agreement agreement) {
        throw unsupportedOperation("attemptConsumerDocumentUpload");
    }

    @Override
    public void attemptAgreementCreation(EService eService, EServiceDescriptor descriptor) {
        throw unsupportedOperation("attemptAgreementCreation");
    }

    @Override
    public void attemptAgreementCloning(Agreement agreement) {
        throw unsupportedOperation("attemptAgreementCloning");
    }

    @Override
    public void attemptAgreementRejection(Agreement agreement) {
        throw unsupportedOperation("attemptAgreementRejection");
    }

    @Override
    public void attemptAgreementSignedDocumentDownload(Agreement agreement) {
        throw unsupportedOperation("attemptAgreementSignedDocumentDownload");
    }

    @Override
    public void attemptInvalidFormatConsumerDocumentUpload(Agreement agreement) {
        throw unsupportedOperation("attemptInvalidFormatConsumerDocumentUpload");
    }

    @Override
    public void attemptAgreementDocumentDownload(Agreement agreement, @Nullable UUID documentId) {
        throw unsupportedOperation("attemptAgreementDocumentDownload");
    }

    @Override
    public void attemptAgreementDocumentDeletion(Agreement agreement, @Nullable UUID documentId) {
        throw unsupportedOperation("attemptAgreementDocumentDeletion");
    }

    @Override
    public boolean supports(@NonNull Channel channel) {
        return channel == Channel.M2M_V3;
    }

    private static UnsupportedOperationException unsupportedOperation(String operation) {
        return new UnsupportedOperationException(
                "M2MAgreementGateway does not support " + operation + " operation"
        );
    }
}
