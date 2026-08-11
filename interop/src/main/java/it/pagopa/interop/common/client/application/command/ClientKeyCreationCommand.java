package it.pagopa.interop.common.client.application.command;

import it.pagopa.interop.common.infrastructure.utils.RandomUtils;
import it.pagopa.interop.common.infrastructure.utils.jwt.JwtUtils;
import it.pagopa.interop.common.kernel.domain.Key;
import it.pagopa.interop.common.kernel.domain.KeyAlgorithm;
import it.pagopa.interop.common.kernel.domain.KeyUse;

public interface ClientKeyCreationCommand {
    ClientKeyCreationCommand name(String name);
    ClientKeyCreationCommand key(String key);
    ClientKeyCreationCommand use(KeyUse use);
    ClientKeyCreationCommand alg(String alg);

    default ClientKeyCreationCommand randomClientConsumerKey(){
        KeyAlgorithm algorithm = KeyAlgorithm.RSA;
        Key key = Key.generate(algorithm);

        return this
                .name(RandomUtils.randomAlphanumericName("key"))
                .key(JwtUtils.encodeDelimitedPublicKeyBase64(key.pair().getPublic()))
                .alg(JwtUtils.resolveAlgorithm(algorithm))
                .use(KeyUse.SIG);
    }
}
