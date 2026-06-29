package it.pagopa.interop.common.contract.model.attribute;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AttributeKind {
    CERTIFIED("CERTIFIED"),

    DECLARED("DECLARED"),

    VERIFIED("VERIFIED"),

    CERTIFIED_DISCRETE("CERTIFIED_DISCRETE");

    private final String value;
}
