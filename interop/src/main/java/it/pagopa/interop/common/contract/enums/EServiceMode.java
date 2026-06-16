package it.pagopa.interop.common.contract.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EServiceMode {
    RECEIVE("RECEIVE"),

    DELIVER("DELIVER");

    private final String value;
}
