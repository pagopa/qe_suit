package it.pagopa.interop.web.agreement.application;

import it.pagopa.interop.common.agreement.domain.Agreement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebAgreementUseCase {

    private final WebAgreementSecurityGateway webAgreementSecurityGateway;

    public void blockUnauthorizedAccessToErogazioneSection(Agreement agreement) {
        webAgreementSecurityGateway.blockUnauthorizedAccessToErogazioneSection(agreement);
    }
}

