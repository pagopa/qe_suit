package it.pagopa.interop.common.contract.model.agreement;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AgreementApprovalPolicy {
    AUTOMATIC("AUTOMATIC"),

    MANUAL("MANUAL");

    private final String value;
}
