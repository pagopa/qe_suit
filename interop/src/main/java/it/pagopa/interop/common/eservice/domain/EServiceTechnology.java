package it.pagopa.interop.new_arch.common.eservice.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EServiceTechnology {

    REST("REST"),

    SOAP("SOAP");

    private final String value;
}
