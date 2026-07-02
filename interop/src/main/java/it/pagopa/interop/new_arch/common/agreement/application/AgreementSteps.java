package it.pagopa.interop.new_arch.common.agreement.application;

import it.pagopa.interop.new_arch.common.eservice.domain.EService;
import it.pagopa.interop.new_arch.common.eservice.domain.EServiceDescriptor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AgreementSteps {
    private final AgreementUseCase agreementUseCase;

    public void createAgreement(EService eService, EServiceDescriptor eServiceDescriptor) {
        agreementUseCase.createAgreement(eService, eServiceDescriptor);
    }
}
