package it.pagopa.interop.common.contract.model.eservice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EServiceTechnology {

    REST("REST"),

    SOAP("SOAP");

    private final String value;
}
