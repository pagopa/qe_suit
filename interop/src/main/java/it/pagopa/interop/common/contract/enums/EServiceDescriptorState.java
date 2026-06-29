package it.pagopa.interop.common.contract.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EServiceDescriptorState {
    DRAFT("DRAFT"),

    PUBLISHED("PUBLISHED"),

    DEPRECATED("DEPRECATED"),

    SUSPENDED("SUSPENDED"),

    ARCHIVED("ARCHIVED"),

    WAITING_FOR_APPROVAL("WAITING_FOR_APPROVAL"),

    ARCHIVING("ARCHIVING"),

    ARCHIVING_SUSPENDED("ARCHIVING_SUSPENDED");

    private final String value;
}
