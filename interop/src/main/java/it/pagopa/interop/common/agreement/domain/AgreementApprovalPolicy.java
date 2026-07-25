package it.pagopa.interop.new_arch.common.agreement.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AgreementApprovalPolicy {
    AUTOMATIC("AUTOMATIC"),

    MANUAL("MANUAL");

    private final String value;
}
