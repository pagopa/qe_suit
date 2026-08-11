package it.pagopa.interop.bff.client.application;

import it.pagopa.interop.common.client.application.command.ClientKeyCreationCommand;
import it.pagopa.interop.common.kernel.domain.KeyUse;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeySeed;
import lombok.Getter;

@Getter
public class BffClientKeyCreationCommand implements ClientKeyCreationCommand {

    private final KeySeed keySeed = new KeySeed();

    @Override
    public ClientKeyCreationCommand name(String name) {
        this.keySeed.setName(name);
        return this;
    }

    @Override
    public ClientKeyCreationCommand key(String key) {
        this.keySeed.setKey(key);
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
