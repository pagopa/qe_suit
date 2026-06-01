package it.pagopa.interop.common.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.security.KeyPair;

@Getter
@RequiredArgsConstructor
public class DPoPProof extends AbstractModel {

    private final String jwt;
    private final KeyPair keyPair;

    @Override
    public String getUniqueIdentifier() {
        return jwt;
    }
}