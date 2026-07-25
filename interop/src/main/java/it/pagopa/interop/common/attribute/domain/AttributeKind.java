package it.pagopa.interop.new_arch.common.attribute.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AttributeKind {
    CERTIFIED("CERTIFIED"),

    DECLARED("DECLARED"),

    VERIFIED("VERIFIED"),

    CERTIFIED_DISCRETE("CERTIFIED_DISCRETE");

    private final String value;
}
