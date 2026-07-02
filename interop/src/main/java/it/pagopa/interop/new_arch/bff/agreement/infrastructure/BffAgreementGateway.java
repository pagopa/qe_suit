package it.pagopa.interop.new_arch.bff.agreement.infrastructure;

import it.pagopa.interop.common.contract.template.action.strategy.PollingStrategy;
import it.pagopa.interop.new_arch.common.agreement.application.AgreementGateway;
import it.pagopa.interop.new_arch.common.agreement.application.request.ActivateAgreementRequest;
import it.pagopa.interop.new_arch.common.agreement.application.request.CreateAgreementRequest;
import it.pagopa.interop.new_arch.common.agreement.application.request.SubmitAgreementRequest;
import it.pagopa.interop.new_arch.common.agreement.domain.Agreement;
import it.pagopa.interop.new_arch.common.agreement.domain.AgreementRef;
import it.pagopa.interop.new_arch.common.kernel.domain.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BffAgreementGateway implements AgreementGateway {

    private final BffAgreementRestClient restClient;

    @Override
    public AgreementRef createAgreement(CreateAgreementRequest request) {
        return restClient.create(request.getEService().getId(), request.getEServiceDescriptor().getId(), request.getDelegation().getId())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .andUpdateContext()
                .getModel()
                .getRef();
    }

    @Override
    public Agreement getAgreement(AgreementRef ref) {
        return restClient.read(ref.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .andUpdateContext()
                .getModel();
    }

    @Override
    public Optional<AgreementRef> submitAgreement(SubmitAgreementRequest request) {
        AgreementRef ref = restClient.submit(request.getAgreement().getId())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .andUpdateContext()
                .getModel()
                .getRef();

        return Optional.of(ref);
    }

    @Override
    public Optional<AgreementRef> activateAgreement(ActivateAgreementRequest request) {
        AgreementRef ref = restClient.activate(request.getAgreement().getId(), request.getDelegation().getId())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .andUpdateContext()
                .getModel()
                .getRef();

        return Optional.of(ref);
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}