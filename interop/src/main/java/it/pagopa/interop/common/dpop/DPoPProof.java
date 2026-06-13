package it.pagopa.interop.common.dpop;

import it.pagopa.interop.common.template.TestModel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.security.KeyPair;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class DPoPProof implements TestModel {

    private final String jwt;
    private final KeyPair keyPair;
    private final UUID id = UUID.randomUUID();

    @Override
    public UUID getId() {
        return id;
    }
}