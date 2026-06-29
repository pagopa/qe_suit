package it.pagopa.interop.common.contract.model.shared;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.security.KeyPair;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class Key {
    KeyPair pair;
}

