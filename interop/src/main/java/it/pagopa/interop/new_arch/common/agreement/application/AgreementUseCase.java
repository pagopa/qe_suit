package it.pagopa.interop.new_arch.common.agreement.application;

import it.pagopa.interop.new_arch.common.agreement.application.request.CreateAgreementRequest;
import it.pagopa.interop.new_arch.common.agreement.domain.Agreement;
import it.pagopa.interop.new_arch.common.agreement.domain.AgreementRef;
import it.pagopa.interop.new_arch.common.kernel.domain.DelegationRef;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class AgreementUseCase {
    private final AgreementGateway agreementGateway;
    private final AgreementRequestFactory requestFactory;

    public Agreement createAgreement(Consumer<CreateAgreementRequest> configurator) {
        CreateAgreementRequest request = requestFactory.creationRequest();
        configurator.accept(request);

        AgreementRef ref = agreementGateway.createAgreement(request.getEService(), request.getEServiceDescriptor(), request.getDelegation());
        return agreementGateway.getAgreement(ref);
    }

    public Agreement getAgreement(Agreement agreement) {
        return agreementGateway.getAgreement(agreement.getRef());
    }

    public Agreement activateAgreement(Agreement agreement, @Nullable DelegationRef delegation) {
        Optional<AgreementRef> maybeRef = agreementGateway.activateAgreement(agreement, delegation);
        return agreementGateway.getAgreement(maybeRef.orElse(agreement.getRef()));
    }

    public Agreement submitAgreement(Agreement agreement) {
        Optional<AgreementRef> maybeRef = agreementGateway.submitAgreement(agreement);
        return agreementGateway.getAgreement(maybeRef.orElse(agreement.getRef()));
    }
}