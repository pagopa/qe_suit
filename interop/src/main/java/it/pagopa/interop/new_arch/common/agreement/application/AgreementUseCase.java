package it.pagopa.interop.new_arch.common.agreement.application;

import it.pagopa.interop.new_arch.common.agreement.application.request.ActivateAgreementRequest;
import it.pagopa.interop.new_arch.common.agreement.application.request.CreateAgreementRequest;
import it.pagopa.interop.new_arch.common.agreement.application.request.SubmitAgreementRequest;
import it.pagopa.interop.new_arch.common.agreement.domain.Agreement;
import it.pagopa.interop.new_arch.common.agreement.domain.AgreementRef;
import it.pagopa.interop.new_arch.common.eservice.domain.EService;
import it.pagopa.interop.new_arch.common.eservice.domain.EServiceDescriptor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AgreementUseCase {
    private final AgreementGateway agreementGateway;
    private final AgreementRequestFactory requestFactory;

    public Agreement createAgreement(EService eService, EServiceDescriptor eServiceDescriptor) {
        CreateAgreementRequest request = requestFactory.creationRequest()
                .eService(eService)
                .eServiceDescriptor(eServiceDescriptor);

        AgreementRef ref = agreementGateway.createAgreement(request);
        return agreementGateway.getAgreement(ref);
    }

    public Agreement getAgreement(Agreement agreement) {
        return agreementGateway.getAgreement(agreement.getRef());
    }

    public Agreement activateAgreement(Agreement agreement) {
        ActivateAgreementRequest request = requestFactory.activateRequest()
                .agreement(agreement);

        Optional<AgreementRef> maybeRef = agreementGateway.activateAgreement(request);
        return agreementGateway.getAgreement(maybeRef.orElse(agreement.getRef()));
    }

    public Agreement submitAgreement(Agreement agreement) {
        SubmitAgreementRequest request = requestFactory.submitRequest()
                .agreement(agreement);

        Optional<AgreementRef> maybeRef = agreementGateway.submitAgreement(request);
        return agreementGateway.getAgreement(maybeRef.orElse(agreement.getRef()));
    }
}