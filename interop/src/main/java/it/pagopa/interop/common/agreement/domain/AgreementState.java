package it.pagopa.interop.common.agreement.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
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
