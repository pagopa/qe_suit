package it.pagopa.interop.new_arch.common.kernel.domain;

import it.pagopa.interop.new_arch.common.infrastructure.security.KeyPairUtils;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.security.KeyPair;

@Builder(toBuilder = true)
@Jacksonized
public record Key(KeyPair pair) {
    public static Key generate(KeyAlgorithm algorithm, int keySize) {
        return new Key(KeyPairUtils.generate(algorithm, keySize));
    }

    public static Key generate(KeyAlgorithm algorithm) {
        return new Key(KeyPairUtils.generate(algorithm));
    }
}