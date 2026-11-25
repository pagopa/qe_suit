package it.pagopa.interop.bff.agreement.infrastructure;

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
import it.pagopa.utils.async.PollingUtils;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementPayload;
import it.pagopa.utils.FileUtils;
import jakarta.annotation.Nullable;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BffAgreementGateway implements AgreementGateway {

    private final BffAgreementRestClient restClient;
    private final BffAgreementMapper mapper;
    private final BffAgreementRequestFactory agreementRequestFactory;
    private final BffAgreementPrerequisiteGateway prerequisiteGateway;

    @Override
    public void ensureActiveAgreement(EService eService, EServiceDescriptor descriptor) {
        prerequisiteGateway.ensureActiveAgreement(eService, descriptor, this);
    }

    @Override
    public void viewConsumerAgreementDetails(Agreement agreement) {
        throw new UnsupportedOperationException("BffAgreementGateway does not support viewConsumerAgreementDetails operation");
    }

    @Override
    public void viewProducerAgreementDetails(Agreement agreement) {
        throw new UnsupportedOperationException("BffAgreementGateway does not support viewProducerAgreementDetails operation");
    }

    @Override
    public void verifyConsumerAgreementDownloadAvailability(Agreement agreement) {
        throw new UnsupportedOperationException("BffAgreementGateway does not support verifyConsumerAgreementDownloadAvailability operation");
    }

    @Override
    public void verifyProducerAgreementDownloadAvailability(Agreement agreement) {
        throw new UnsupportedOperationException("BffAgreementGateway does not support verifyProducerAgreementDownloadAvailability operation");
    }

    @Override
    public Agreement createAgreement(EService eService, EServiceDescriptor descriptor, @Nullable Delegation delegation) {
        AgreementPayload payload = agreementRequestFactory.creationRequest(eService, descriptor, delegation);

        return restClient.create(payload)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(createdResource -> getAgreement(new AgreementRef(createdResource.getId())))
                .get();
    }

    @Override
    public void shouldFailToCreateAgreement(EService eService, EServiceDescriptor descriptor, @Nullable Delegation delegation, AgreementCreationFailureReason reason) {
        AgreementPayload payload = agreementRequestFactory.creationRequest(eService, descriptor, delegation);

        int expectedStatus = switch (reason) {
            case ESERVICE_INVALID_STATE, DEPRECATED_VERSION, ARCHIVED_STATE -> 400;
        };

        restClient.create(payload)
                .withPolling(PollingStrategy.UNTIL_ERROR)
                .assertStatusCode(expectedStatus);
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
        return restClient.submit(agreement.getId(), agreementRequestFactory.submissionRequest())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(mapper::toAgreement)
                .updateContext()
                .get();
    }

    @Override
    public void attemptAgreementSubmission(Agreement agreement) {
        restClient.submit(agreement.getId(), agreementRequestFactory.submissionRequest()).withoutPolling();
    }

    @Override
    public Agreement activateAgreement(Agreement agreement, @Nullable Delegation delegation) {
        return restClient.activate(agreement.getId(), Optional.ofNullable(delegation).map(Delegation::getId).orElse(null))
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .assertThat(a -> a.getState().getValue().equals(AgreementState.ACTIVE.getValue()))
                .map(mapper::toAgreement)
                .updateContext()
                .get();
    }

    @Override
    public void attemptAgreementActivation(Agreement agreement) {
        restClient.activate(agreement.getId(), null).withoutPolling();
    }

    @Override
    public Agreement suspendAgreement(Agreement agreement, @Nullable Delegation delegation) {
        return restClient.suspend(
                        agreement.getId(), Optional.ofNullable(delegation).map(Delegation::getId).orElse(null)
                )
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(mapper::toAgreement)
                .updateContext()
                .get();
    }

    @Override
    public Agreement rejectAgreement(Agreement agreement) {
        return restClient.reject(agreement.getId(), agreementRequestFactory.rejectionRequest())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(mapper::toAgreement)
                .updateContext()
                .get();
    }

    @Override
    public Agreement archiveAgreement(Agreement agreement) {
        restClient.archive(agreement.getId())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .assertStatusCode(204);
        return awaitState(agreement, Set.of(AgreementState.ARCHIVED));
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
        return PollingUtils.pollUntil(
                () -> getAgreement(agreement.getRef()),
                current -> Boolean.TRUE.equals(current.getIsContractPresent())
        );
    }

    @Override
    public Agreement addConsumerDocument(Agreement agreement, @Nullable String fileName) {
        String defaultFileName = "allegato-a.pdf";
        String effectiveFileName = fileName != null && !fileName.isBlank() ? fileName : defaultFileName;
        File documentFile = resolveConsumerDocumentFile(effectiveFileName);

        restClient.addConsumerDocument(
                        agreement.getId(),
                        effectiveFileName,
                        effectiveFileName,
                        documentFile
                )
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();

        return PollingUtils.pollUntil(
                () -> getAgreement(agreement.getRef()),
                current -> current.getConsumerDocuments() != null
                        && current.getConsumerDocuments().stream()
                        .anyMatch(doc -> effectiveFileName.equals(doc.getName())
                                || effectiveFileName.equals(doc.getPrettyName()))
        );
    }

    @Override
    public Agreement removeConsumerDocument(Agreement agreement, Document document) {
        restClient.removeConsumerDocument(agreement.getId(), document.getId())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();

        return PollingUtils.pollUntil(
                () -> getAgreement(agreement.getRef()),
                current -> current.getConsumerDocuments() == null
                        || current.getConsumerDocuments().stream()
                        .noneMatch(doc -> document.getId().equals(doc.getId()))
        );
    }

    private File resolveConsumerDocumentFile(String fileName) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = BffAgreementGateway.class.getClassLoader();
        }
        for (String candidate : java.util.List.of(fileName, "assets/" + fileName, "assets/agreement/" + fileName)) {
            String normalized = candidate.startsWith("/") ? candidate.substring(1) : candidate;
            if (classLoader.getResource(normalized) != null) {
                return FileUtils.loadClasspathResourceAsTempFile(normalized);
            }
        }
        throw new IllegalStateException("Risorsa non trovata nel classpath: " + fileName);
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }

    @Override
    public void attemptAgreementSuspension(Agreement agreement) {
        restClient.suspend(agreement.getId(), null).withoutPolling();
    }

    @Override
    public void attemptAgreementReactivation(Agreement agreement) {
        restClient.unsuspend(agreement.getId(), null).withoutPolling();
    }

    @Override
    public void attemptAgreementArchiving(Agreement agreement) {
        restClient.archive(agreement.getId()).withoutPolling();
    }

    @Override
    public void attemptConsumerDocumentUpload(Agreement agreement) {
        var document = FileUtils.loadClasspathResourceAsTempFile("assets/eservice-template/openapi3-riferimenti.json");
        restClient.addConsumerDocument(agreement.getId(), "openapi3-riferimenti.json", "Allegato di test", document)
                .withoutPolling();
    }

    @Override
    public void attemptAgreementCreation(EService eService, EServiceDescriptor descriptor) {
        restClient.create(agreementRequestFactory.creationRequest(eService, descriptor, null)).withoutPolling();
    }

    @Override
    public void attemptAgreementCloning(Agreement agreement) {
        restClient.cloneAgreement(agreement.getId()).withoutPolling();
    }

    @Override
    public void attemptAgreementRejection(Agreement agreement) {
        restClient.reject(agreement.getId(), agreementRequestFactory.rejectionRequest()).withoutPolling();
    }

    @Override
    public void attemptAgreementSignedDocumentDownload(Agreement agreement) {
        restClient.downloadSignedDocument(agreement.getId()).withoutPolling();
    }

    @Override
    public void attemptInvalidFormatConsumerDocumentUpload(Agreement agreement) {
        var document = FileUtils.loadClasspathResourceAsTempFile("assets/agreement/esempio.csv");
        restClient.addConsumerDocument(agreement.getId(), "openapi3-riferimenti.json", "Allegato di test", document)
                .withoutPolling();
    }

    @Override
    public void attemptAgreementDocumentDownload(Agreement agreement) {
        restClient.getAgreementConsumerDocument(agreement.getId(), agreement.getConsumerDocuments().get(0).getId()).withoutPolling();
    }

    @Override
    public void attemptAgreementDocumentDeletion(Agreement agreement, @Nullable UUID documentId) {
        UUID targetDocumentId = documentId != null ? documentId : agreement.getConsumerDocuments().get(0).getId();
        restClient.deleteAgreementConsumerDocument(agreement.getId(), targetDocumentId).withoutPolling();
    }
}
