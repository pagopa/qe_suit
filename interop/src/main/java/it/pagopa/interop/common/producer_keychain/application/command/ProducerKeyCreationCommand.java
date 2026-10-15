package it.pagopa.interop.common.producer_keychain.application.command;

import it.pagopa.interop.common.kernel.domain.KeyUse;
import it.pagopa.kernel.security.Key;
import it.pagopa.kernel.security.KeyAlgorithm;
import it.pagopa.utils.RandomUtils;
import it.pagopa.utils.jwt.JwtUtils;

/**
 * Identico a ClientKeyCreationCommand solo per coincidenza: struttura e processo
 * di creazione possono evolvere indipendentemente tra client e producerKeychain.
 */
public interface ProducerKeyCreationCommand {
    ProducerKeyCreationCommand name(String name);
    ProducerKeyCreationCommand key(Key key);
    ProducerKeyCreationCommand use(KeyUse use);
    ProducerKeyCreationCommand alg(String alg);

    default ProducerKeyCreationCommand randomProducerKey() {
        KeyAlgorithm algorithm = KeyAlgorithm.RSA;
        return name(RandomUtils.randomAlphanumericName("producer-key"))
                .key(Key.generate(algorithm))
                .alg(JwtUtils.resolveAlgorithm(algorithm))
                .use(KeyUse.SIG);
    }
}
