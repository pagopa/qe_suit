package it.pagopa.interop.bff.producer_keychain.application;

import it.pagopa.interop.common.kernel.domain.KeyUse;
import it.pagopa.interop.common.producer_keychain.application.command.ProducerKeyCreationCommand;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeySeed;
import it.pagopa.kernel.security.Key;
import it.pagopa.utils.jwt.JwtUtils;
import lombok.Getter;

@Getter
public class BffProducerKeyCreationCommand implements ProducerKeyCreationCommand {
    private final KeySeed keySeed = new KeySeed();

    @Override
    public BffProducerKeyCreationCommand name(String name) {
        keySeed.setName(name);
        return this;
    }

    @Override
    public BffProducerKeyCreationCommand key(Key key) {
        keySeed.setKey(JwtUtils.encodeDelimitedPublicKeyBase64(key.pair().getPublic()));
        return this;
    }

    @Override
    public BffProducerKeyCreationCommand use(KeyUse use) {
        keySeed.setUse(it.pagopa.interop.generated.openapi.clients.bff.model.KeyUse.fromValue(use.name()));
        return this;
    }

    @Override
    public BffProducerKeyCreationCommand alg(String alg) {
        keySeed.setAlg(alg);
        return this;
    }
}
