package it.pagopa.interop.bff.infrastructure.security;

import it.pagopa.interop.common.kernel.domain.KeyRegistrationMaterial;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeySeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeyUse;
import it.pagopa.kernel.security.Key;
import it.pagopa.kernel.security.KeyAlgorithm;
import it.pagopa.kernel.security.KeyPairUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;

@Component
public class BffKeyRequestFactory {
    public KeySeed registrationRequest(KeyRegistrationMaterial material, String name, Collection<Key> registeredKeys) {
        String pem = switch (material) {
            case PRIVATE_KEY -> KeyPairUtils.toPem(Key.generate(KeyAlgorithm.RSA).pair().getPrivate());
            case CERTIFICATE -> certificate();
            case MULTIPLE_PUBLIC_KEYS -> publicKeyPem(KeyAlgorithm.RSA) + "\n" + publicKeyPem(KeyAlgorithm.RSA);
            case INCOMPLETE_PEM -> publicKeyPem(KeyAlgorithm.RSA).substring(0, 64);
            case EC_PUBLIC_KEY -> publicKeyPem(KeyAlgorithm.EC);
            case RSA_1024_PUBLIC_KEY -> KeyPairUtils.toPem(Key.generate(KeyAlgorithm.RSA, 1024).pair().getPublic());
            case REGISTERED_PUBLIC_KEY -> registeredPublicKey(registeredKeys);
            case VALID_PUBLIC_KEY -> publicKeyPem(KeyAlgorithm.RSA);
        };
        return new KeySeed().name(name).use(KeyUse.SIG)
                .alg(material == KeyRegistrationMaterial.EC_PUBLIC_KEY ? "ES256" : "RS256")
                .key(Base64.getEncoder().encodeToString(pem.getBytes(StandardCharsets.UTF_8)));
    }

    private String publicKeyPem(KeyAlgorithm algorithm) {
        return KeyPairUtils.toPem(Key.generate(algorithm).pair().getPublic());
    }

    private String registeredPublicKey(Collection<Key> registeredKeys) {
        if (registeredKeys == null || registeredKeys.size() != 1) {
            throw new IllegalStateException("Duplicate key registration requires exactly one known registered key");
        }
        return KeyPairUtils.toPem(registeredKeys.iterator().next().pair().getPublic());
    }

    private String certificate() {
        try {
            return new ClassPathResource("assets/authorization/test-certificate.pem")
                    .getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new UncheckedIOException("Cannot load the public test certificate", exception);
        }
    }
}
