package it.pagopa.interop.web.agreement.application;

import it.pagopa.interop.common.agreement.domain.Agreement;
import it.pagopa.interop.web.agreement.infrastructure.WebAgreementGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebAgreementUseCase {

    private final WebAgreementGateway webAgreementGateway;

    public void blockUnauthorizedAccessToErogazioneSection(Agreement agreement) {
        webAgreementGateway.blockUnauthorizedAccessToErogazioneSection(agreement);
    }
}

