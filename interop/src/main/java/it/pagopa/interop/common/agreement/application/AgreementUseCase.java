package it.pagopa.interop.common.agreement.application;

import it.pagopa.interop.common.agreement.domain.Agreement;
import it.pagopa.interop.common.agreement.domain.AgreementCreationFailureReason;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.kernel.domain.Delegation;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AgreementUseCase {
    private final AgreementGateway agreementGateway;

    public void ensureActiveAgreement(EService eService, EServiceDescriptor descriptor) {
        agreementGateway.ensureActiveAgreement(eService, descriptor);
    }

    public Agreement createAgreement(EService eService, EServiceDescriptor descriptor, @Nullable Delegation delegation) {
        return agreementGateway.createAgreement(eService, descriptor, delegation);
    }

    public Agreement createAgreement(EService eService, EServiceDescriptor descriptor) {
        return createAgreement(eService, descriptor, null);
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

    public Agreement submitAgreement(Agreement agreement) {
        return agreementGateway.submitAgreement(agreement);
    }

    public Agreement suspendAgreement(Agreement agreement, @Nullable Delegation delegation) {
        return agreementGateway.suspendAgreement(agreement, delegation);
    }

    public Agreement archiveAgreement(Agreement agreement) {
        return agreementGateway.archiveAgreement(agreement);
    }

    public Agreement awaitState(Agreement agreement, Set<AgreementState> expectedStates) {
        return agreementGateway.awaitState(agreement, expectedStates);
    }
}