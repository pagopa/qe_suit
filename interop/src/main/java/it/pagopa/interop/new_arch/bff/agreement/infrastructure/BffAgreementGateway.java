package it.pagopa.interop.new_arch.bff.agreement.infrastructure;

import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementPayload;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementSubmissionPayload;
import it.pagopa.interop.new_arch.common.agreement.application.AgreementGateway;
import it.pagopa.interop.new_arch.common.agreement.domain.Agreement;
import it.pagopa.interop.new_arch.common.agreement.domain.AgreementCreationFailureReason;
import it.pagopa.interop.new_arch.common.agreement.domain.AgreementRef;
import it.pagopa.interop.new_arch.common.agreement.domain.AgreementState;
import it.pagopa.interop.new_arch.common.eservice.domain.EService;
import it.pagopa.interop.new_arch.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.new_arch.common.infrastructure.template.action.strategy.AssertionStrategy;
import it.pagopa.interop.new_arch.common.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.new_arch.common.kernel.domain.Channel;
import it.pagopa.interop.new_arch.common.kernel.domain.DelegationRef;
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
    public Agreement createAgreement(EService eService, EServiceDescriptor descriptor, @Nullable DelegationRef delegation) {
        AgreementPayload payload = agreementRequestFactory.creationRequest(eService, descriptor, delegation);

        return restClient.create(payload)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .saveToContext(createdResource -> getAgreement(new AgreementRef(createdResource.getId())))
                .getModel();
    }

    @Override
    public void shouldFailToCreateAgreement(EService eService, EServiceDescriptor descriptor, @Nullable DelegationRef delegation, AgreementCreationFailureReason reason) {
        AgreementPayload payload = agreementRequestFactory.creationRequest(eService, descriptor, delegation);

        AssertionStrategy expectedStatus = switch (reason) {
            case ESERVICE_INVALID_STATE -> AssertionStrategy.STATUS_400;
        };

        restClient.create(payload)
                .withPolling(PollingStrategy.UNTIL_ERROR);
                //.andAssertThat(expectedStatus);
    }

    @Override
    public Agreement getAgreement(AgreementRef ref) {
        return restClient.read(ref.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .saveToContext(mapper::toAgreement)
                .getModel();
    }


    @Override
    public Agreement submitAgreement(Agreement agreement) {
        return restClient.submit(agreement.getId(), new AgreementSubmissionPayload().consumerNotes("consumerNotes"))
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .saveToContext(mapper::toAgreement)
                .getModel();
    }

    @Override
    public Agreement activateAgreement(Agreement agreement, @Nullable DelegationRef delegation) {
        return restClient.activate(
                        agreement.getId(), Optional.ofNullable(delegation).map(DelegationRef::getId).orElse(null)
                )
                .withPolling(PollingStrategy.UNTIL_SUCCESS_WHERE(a -> a.getState().getValue().equals(AgreementState.ACTIVE.getValue())))
                .saveToContext(mapper::toAgreement)
                .getModel();
    }


    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}