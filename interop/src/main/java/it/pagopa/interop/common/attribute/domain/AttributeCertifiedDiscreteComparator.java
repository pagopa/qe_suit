package it.pagopa.interop.common.attribute.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AttributeCertifiedDiscreteComparator {
    GT("GT"),

    LT("LT"),

    EQ("EQ"),

    GTE("GTE"),

    LTE("LTE"),

    NE("NE");

    private final String value;
}
