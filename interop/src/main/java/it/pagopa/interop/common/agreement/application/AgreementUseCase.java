package it.pagopa.interop.common.agreement.application;

import it.pagopa.interop.common.agreement.domain.Agreement;
import it.pagopa.interop.common.agreement.domain.AgreementCreationFailureReason;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.kernel.domain.Delegation;
import it.pagopa.interop.common.kernel.domain.Document;
import it.pagopa.utils.async.PollingUtils;
import jakarta.annotation.Nullable;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Service
@RequiredArgsConstructor
public class AgreementUseCase {
    private final AgreementGateway agreementGateway;

    public void viewConsumerAgreementDetails(Agreement agreement) {
        agreementGateway.viewConsumerAgreementDetails(agreement);
    }

    public void viewProducerAgreementDetails(Agreement agreement) {
        agreementGateway.viewProducerAgreementDetails(agreement);
    }

    public void verifyConsumerAgreementDownloadAvailability(Agreement agreement) {
        agreementGateway.verifyConsumerAgreementDownloadAvailability(agreement);
    }

    public void verifyProducerAgreementDownloadAvailability(Agreement agreement) {
        agreementGateway.verifyProducerAgreementDownloadAvailability(agreement);
    }

    public void ensureActiveAgreement(EService eService, EServiceDescriptor descriptor) {
        agreementGateway.ensureActiveAgreement(eService, descriptor);
    }

    public Agreement createAgreement(EService eService, EServiceDescriptor descriptor, @Nullable Delegation delegation) {
        return agreementGateway.createAgreement(eService, descriptor, delegation);
    }

    public Agreement createAgreement(EService eService, EServiceDescriptor descriptor) {
        return createAgreement(eService, descriptor, null);
    }

    public Agreement createAgreement(EService eService, EServiceDescriptor descriptor, AgreementState agreementState, @Nullable Delegation delegation) {
        Agreement agreement = createAgreement(eService, descriptor, delegation);

        return switch (agreementState) {
            case DRAFT -> agreement;
            case PENDING -> submitAgreement(agreement);
            case ACTIVE -> {
                Agreement submitted = submitAgreement(agreement);
                yield PollingUtils.pollUntil(
                        () -> getAgreement(submitted),
                        refreshed -> refreshed.getState() == AgreementState.ACTIVE,
                        "La richiesta di fruizione non ha raggiunto lo stato ACTIVE"
                );
            }
            default -> throw new UnsupportedOperationException("Not implemented yet: " + agreementState);
        };
    }

    public void shouldFailToCreateAgreement(EService eService, EServiceDescriptor descriptor, @Nullable Delegation delegation, AgreementCreationFailureReason reason) {
        agreementGateway.shouldFailToCreateAgreement(eService, descriptor, delegation, reason);
    }

    public void shouldFailToCreateAgreement(EService eService, EServiceDescriptor descriptor, AgreementCreationFailureReason reason) {
        shouldFailToCreateAgreement(eService, descriptor, null, reason);
    }

    public Agreement getAgreement(Agreement agreement) {
        return agreementGateway.getAgreement(agreement.getRef());
    }

    public Agreement activateAgreement(Agreement agreement, @Nullable Delegation delegation) {
        return agreementGateway.activateAgreement(agreement, delegation);
    }

    public Agreement activateAgreement(Agreement agreement) {
        return activateAgreement(agreement, null);
    }

    public void attemptAgreementActivation(Agreement agreement) {
        agreementGateway.attemptAgreementActivation(agreement);
    }

    public Agreement submitAgreement(Agreement agreement) {
        return agreementGateway.submitAgreement(agreement);
    }

    public void attemptAgreementSubmission(Agreement agreement) {
        agreementGateway.attemptAgreementSubmission(agreement);
    }

    public Agreement suspendAgreement(Agreement agreement, @Nullable Delegation delegation) {
        return agreementGateway.suspendAgreement(agreement, delegation);
    }

    public Agreement rejectAgreement(Agreement agreement) {
        return agreementGateway.rejectAgreement(agreement);
    }

    public Agreement archiveAgreement(Agreement agreement) {
        return agreementGateway.archiveAgreement(agreement);
    }

    public Agreement awaitState(Agreement agreement, Set<AgreementState> expectedStates) {
        return agreementGateway.awaitState(agreement, expectedStates);
    }

    public Agreement awaitContractAvailable(Agreement agreement) {
        return agreementGateway.awaitContractAvailable(agreement);
    }

    public void verifySignedContractNotYetAvailable(Agreement agreement) {
        Agreement refreshed = getAgreement(agreement);
        assertThat(refreshed.getIsDocumentReady())
                .as("Il documento firmato della richiesta di fruizione non deve essere ancora disponibile")
                .isNotEqualTo(Boolean.TRUE);
    }

    public Agreement addConsumerDocument(Agreement agreement, @Nullable String fileName) {
        return agreementGateway.addConsumerDocument(agreement, fileName);
    }

    public Agreement removeLastConsumerDocument(Agreement agreement) {
        Agreement current = agreement.getConsumerDocuments() != null && !agreement.getConsumerDocuments().isEmpty()
                ? agreement
                : getAgreement(agreement);
        if (current.getConsumerDocuments() == null || current.getConsumerDocuments().isEmpty()) {
            throw new IllegalStateException("No consumer document found for agreement " + agreement.getId());
        }
        Document document = current.getConsumerDocuments().get(current.getConsumerDocuments().size() - 1);
        return agreementGateway.removeConsumerDocument(current, document);
    }

    public void attemptAgreementSuspension(Agreement agreement) {
        agreementGateway.attemptAgreementSuspension(agreement);
    }

    public void attemptAgreementReactivation(Agreement agreement) {
        agreementGateway.attemptAgreementReactivation(agreement);
    }

    public void attemptAgreementArchiving(Agreement agreement) {
        agreementGateway.attemptAgreementArchiving(agreement);
    }

    public void attemptConsumerDocumentUpload(Agreement agreement) {
        agreementGateway.attemptConsumerDocumentUpload(agreement);
    }

    public void attemptAgreementCreation(EService eService, EServiceDescriptor descriptor) {
        agreementGateway.attemptAgreementCreation(eService, descriptor);
    }

    public void attemptAgreementCloning(Agreement agreement) {
        agreementGateway.attemptAgreementCloning(agreement);
    }

    public void attemptAgreementRejection(Agreement agreement) {
        agreementGateway.attemptAgreementRejection(agreement);
    }

    public void attemptAgreementSignedDocumentDownload(Agreement agreement) {
        agreementGateway.attemptAgreementSignedDocumentDownload(agreement);
    }

    public void attemptInvalidFormatConsumerDocumentUpload(Agreement agreement) {
        agreementGateway.attemptInvalidFormatConsumerDocumentUpload(agreement);
    }

    public void attemptAgreementDocumentDownload(Agreement agreement) {
        agreementGateway.attemptAgreementDocumentDownload(agreement);
    }

    public void attemptAgreementDocumentDeletion(Agreement agreement) {
        agreementGateway.attemptAgreementDocumentDeletion(agreement, null);
    }

    public void attemptAgreementDocumentDeletion(Agreement agreement, UUID deletedDocumentId) {
        agreementGateway.attemptAgreementDocumentDeletion(agreement, deletedDocumentId);
    }
}
