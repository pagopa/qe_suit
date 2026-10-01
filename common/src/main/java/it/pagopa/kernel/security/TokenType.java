package it.pagopa.kernel.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Getter
@RequiredArgsConstructor
public enum TokenType implements Serializable {
    BEARER,

    DPOP;
}
