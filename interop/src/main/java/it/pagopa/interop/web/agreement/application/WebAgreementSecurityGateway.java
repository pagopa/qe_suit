package it.pagopa.interop.web.agreement.application;

import it.pagopa.interop.common.agreement.domain.Agreement;

public interface WebAgreementSecurityGateway {

    void blockUnauthorizedAccessToErogazioneSection(Agreement agreement);
}

