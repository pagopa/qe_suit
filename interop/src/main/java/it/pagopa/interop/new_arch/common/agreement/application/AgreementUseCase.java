package it.pagopa.interop.new_arch.common.agreement.application;

import it.pagopa.interop.new_arch.common.agreement.domain.Agreement;
import it.pagopa.interop.new_arch.common.agreement.domain.AgreementRef;
import it.pagopa.interop.new_arch.common.eservice.domain.EService;
import it.pagopa.interop.new_arch.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.new_arch.common.kernel.domain.DelegationRef;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AgreementUseCase {
    private final AgreementGateway agreementGateway;

    public Agreement createAgreement(EService eService, EServiceDescriptor descriptor, @Nullable DelegationRef delegation) {
        AgreementRef ref = agreementGateway.createAgreement(eService, descriptor, delegation);
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