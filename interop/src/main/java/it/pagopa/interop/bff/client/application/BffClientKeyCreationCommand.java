package it.pagopa.interop.bff.client.application;

import it.pagopa.interop.common.client.application.command.ClientKeyCreationCommand;
import it.pagopa.interop.common.infrastructure.utils.jwt.JwtUtils;
import it.pagopa.interop.common.kernel.domain.Key;
import it.pagopa.interop.common.kernel.domain.KeyUse;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeySeed;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class BffClientKeyCreationCommand implements ClientKeyCreationCommand {

    private final KeySeed keySeed = new KeySeed();
    private Key key;

    @Override
    public ClientKeyCreationCommand name(String name) {
        this.keySeed.setName(name);
        return this;
    }

    @Override
    public ClientKeyCreationCommand key(Key key) {
        this.keySeed.setKey(JwtUtils.encodeDelimitedPublicKeyBase64(key.pair().getPublic()));
        this.key = key;
        return this;
    }

    @Override
    public ClientKeyCreationCommand use(KeyUse use) {
        this.keySeed.setUse(it.pagopa.interop.generated.openapi.clients.bff.model.KeyUse.fromValue(use.name()));
        return this;
    }

    @Override
    public ClientKeyCreationCommand alg(String alg) {
        this.keySeed.setAlg(alg);
        return this;
    }
}
