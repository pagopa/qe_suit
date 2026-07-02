package it.pagopa.interop.new_arch.common.infrastructure.security;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.security.KeyPair;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class Key {
    KeyPair pair;
}

