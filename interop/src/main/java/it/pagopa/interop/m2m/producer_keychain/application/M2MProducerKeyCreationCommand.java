package it.pagopa.interop.m2m.producer_keychain.application;

import it.pagopa.interop.common.kernel.domain.KeyUse;
import it.pagopa.interop.common.producer_keychain.application.command.ProducerKeyCreationCommand;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.KeySeed;
import it.pagopa.kernel.security.Key;
import it.pagopa.utils.jwt.JwtUtils;
import lombok.Getter;

@Getter
public class M2MProducerKeyCreationCommand implements ProducerKeyCreationCommand {
    private final KeySeed keySeed = new KeySeed();
    private Key key;

    @Override
    public M2MProducerKeyCreationCommand name(String name) {
        keySeed.setName(name);
        return this;
    }

    @Override
    public M2MProducerKeyCreationCommand key(Key key) {
        this.key = key;
        keySeed.setKey(JwtUtils.encodeDelimitedPublicKeyBase64(key.pair().getPublic()));
        return this;
    }

    @Override
    public M2MProducerKeyCreationCommand use(KeyUse use) {
        keySeed.setUse(it.pagopa.interop.generated.openapi.clients.m2m.v3.model.KeyUse.fromValue(use.name()));
        return this;
    }

    @Override
    public M2MProducerKeyCreationCommand alg(String alg) {
        keySeed.setAlg(alg);
        return this;
    }
}
