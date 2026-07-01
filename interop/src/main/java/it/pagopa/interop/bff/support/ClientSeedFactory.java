package it.pagopa.interop.bff.support;

import it.pagopa.interop.common.utils.KeyPairUtils;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeySeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeyUse;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeAdditionDetailsSeed;
import org.instancio.Instancio;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.util.List;
import java.util.UUID;

import static org.instancio.Select.field;

@Component
public class ClientSeedFactory {

    public ClientSeed fullCreationRequest(List<UUID> members) {
        return Instancio.of(ClientSeed.class)
                .set(field(ClientSeed::getName), "client-" + UUID.randomUUID())
                .set(field(ClientSeed::getMembers), members == null ? List.of() : members)
                .create();
    }

    public KeySeed fullKeyCreationRequest(KeyPair keyPair){
        return Instancio.of(KeySeed.class)
                .set(field(KeySeed::getName), "key-" + UUID.randomUUID())
                .set(field(KeySeed::getUse), KeyUse.SIG)
                .set(field(KeySeed::getAlg), resolveJwtAlg(keyPair))
                .set(field(KeySeed::getKey), KeyPairUtils.toBase64Pem(keyPair.getPublic()))
                .create();
    }

    public PurposeAdditionDetailsSeed fullPurposeAdditionRequest(UUID purposeId){
        return Instancio.of(PurposeAdditionDetailsSeed.class)
                .set(field(PurposeAdditionDetailsSeed::getPurposeId), purposeId)
                .create();
    }

    private String resolveJwtAlg(KeyPair keyPair) {
        return switch (keyPair.getPublic().getAlgorithm()) {
            case "RSA" -> "RS256";
            case "EC" -> "ES256";
            case "Ed25519", "EdDSA" -> "EdDSA";
            default ->
                    throw new IllegalArgumentException("Unsupported key algorithm: " + keyPair.getPublic().getAlgorithm());
        };
    }
}
