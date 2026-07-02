package it.pagopa.interop.new_arch.common.eservice.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EServiceMode {
    RECEIVE("RECEIVE"),

    DELIVER("DELIVER");

    private final String value;
}
