package it.pagopa.interop.common.contract.model.agreement;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AgreementState {
    DRAFT("DRAFT"),

    ACTIVE("ACTIVE"),

    ARCHIVED("ARCHIVED"),

    PENDING("PENDING"),

    SUSPENDED("SUSPENDED"),

    MISSING_CERTIFIED_ATTRIBUTES("MISSING_CERTIFIED_ATTRIBUTES"),

    REJECTED("REJECTED");

    private final String value;
}
