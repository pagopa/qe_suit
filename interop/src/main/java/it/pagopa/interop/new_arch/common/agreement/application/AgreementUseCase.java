package it.pagopa.interop.new_arch.common.agreement.application;

import it.pagopa.interop.new_arch.common.agreement.application.validator.AgreementFailureValidator;
import it.pagopa.interop.new_arch.common.agreement.domain.Agreement;
import it.pagopa.interop.new_arch.common.agreement.domain.AgreementCreationFailureReason;
import it.pagopa.interop.new_arch.common.agreement.domain.AgreementRef;
import it.pagopa.interop.new_arch.common.eservice.domain.EService;
import it.pagopa.interop.new_arch.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.new_arch.common.kernel.domain.DelegationRef;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AgreementUseCase {
    private final AgreementGateway agreementGateway;
    private final List<AgreementFailureValidator> failureValidators;

    public Agreement createAgreement(EService eService, EServiceDescriptor descriptor, @Nullable DelegationRef delegation) {
        return agreementGateway.createAgreement(eService, descriptor, delegation);
    }

    public Agreement createAgreement(EService eService, EServiceDescriptor descriptor) {
        return createAgreement(eService, descriptor, null);
    }

    public void shouldFailToCreateAgreement(EService eService, EServiceDescriptor descriptor, @Nullable DelegationRef delegation, AgreementCreationFailureReason reason) {
        failureValidators.stream()
                .filter(validator -> validator.supports(reason))
                .findFirst()
                .ifPresent(validator -> validator.validate(eService, descriptor, delegation));

        agreementGateway.shouldFailToCreateAgreement(eService, descriptor, delegation, reason);
    }

    public void shouldFailToCreateAgreement(EService eService, EServiceDescriptor descriptor, AgreementCreationFailureReason reason) {
        shouldFailToCreateAgreement(eService, descriptor, null, reason);
    }

    public Agreement getAgreement(Agreement agreement) {
        return agreementGateway.getAgreement(agreement.getRef());
    }

    public Agreement activateAgreement(Agreement agreement, @Nullable DelegationRef delegation) {
        return agreementGateway.activateAgreement(agreement, delegation);
    }

    public Agreement activateAgreement(Agreement agreement) {
        return activateAgreement(agreement, null);
    }

    public Agreement submitAgreement(Agreement agreement) {
        return agreementGateway.submitAgreement(agreement);
    }
}