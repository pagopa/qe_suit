package it.pagopa.interop.common.client.application.command;

import it.pagopa.utils.RandomUtils;
import it.pagopa.utils.jwt.JwtUtils;
import it.pagopa.interop.common.kernel.domain.Key;
import it.pagopa.interop.common.kernel.domain.KeyUse;
import it.pagopa.kernel.security.KeyAlgorithm;

public interface ClientKeyCreationCommand {
    ClientKeyCreationCommand name(String name);
    ClientKeyCreationCommand key(Key key);
    ClientKeyCreationCommand use(KeyUse use);
    ClientKeyCreationCommand alg(String alg);

    default ClientKeyCreationCommand randomClientConsumerKey(){
        KeyAlgorithm algorithm = KeyAlgorithm.RSA;
        Key key = Key.generate(algorithm);

        return this
                .name(RandomUtils.randomAlphanumericName("key"))
                .key(key)
                .alg(JwtUtils.resolveAlgorithm(algorithm))
                .use(KeyUse.SIG);
    }
}
