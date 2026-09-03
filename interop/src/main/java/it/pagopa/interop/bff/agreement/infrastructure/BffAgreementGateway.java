package it.pagopa.interop.bff.agreement.infrastructure;

import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementPayload;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementSubmissionPayload;
import it.pagopa.interop.common.agreement.application.AgreementGateway;
import it.pagopa.interop.common.agreement.domain.Agreement;
import it.pagopa.interop.common.agreement.domain.AgreementCreationFailureReason;
import it.pagopa.interop.common.agreement.domain.AgreementRef;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Delegation;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BffAgreementGateway implements AgreementGateway {

    private final BffAgreementRestClient restClient;
    private final BffAgreementMapper mapper;
    private final BffAgreementRequestFactory agreementRequestFactory;

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
        return restClient.submit(agreement.getId(), new AgreementSubmissionPayload().consumerNotes("consumerNotes"))
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(mapper::toAgreement)
                .updateContext()
                .get();
    }

    @Override
    public Agreement activateAgreement(Agreement agreement, @Nullable Delegation delegation) {
        return restClient.activate(
                        agreement.getId(), Optional.ofNullable(delegation).map(Delegation::getId).orElse(null)
                )
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .assertThat( a -> a.getState().getValue().equals(AgreementState.ACTIVE.getValue()))
                .map(mapper::toAgreement)
                .updateContext()
                .get();
    }


    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}