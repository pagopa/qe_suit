package it.pagopa.interop.bff.support;

import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementPayload;
import org.instancio.Instancio;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static org.instancio.Select.field;

@Component
public class AgreementSeedFactory {

    public AgreementPayload fullCreationRequest(UUID eserviceId, UUID descriptionId, Optional<UUID> delegationId) {
        return Instancio.of(AgreementPayload.class)
                .set(field(AgreementPayload::getEserviceId), eserviceId)
                .set(field(AgreementPayload::getDescriptorId), descriptionId)
                .set(field(AgreementPayload::getDelegationId), delegationId.isPresent() ? delegationId.orElse(null) : null)
                .create();
    }
}
