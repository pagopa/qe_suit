package it.pagopa.interop.domain.services.purpose.impl;

import it.pagopa.interop.domain.context.PurposeContext;
import it.pagopa.interop.domain.model.Client;
import it.pagopa.interop.domain.model.Eservice;
import it.pagopa.interop.domain.model.Purpose;
import it.pagopa.interop.domain.services.purpose.PurposeService;
import it.pagopa.interop.generated.openapi.clients.bff.api.PurposesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeSeed;
import it.pagopa.interop.infrastructure.client.auth.context.user.CurrentUserContext;
import it.pagopa.interop.utils.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PurposeDataPreparationService implements PurposeService {

    private final PurposesApi purposesApi;
    private final PurposeContext context;
    private final CurrentUserContext currentUserContext;

    @Override
    public Purpose createEservicePurpose(Eservice eservice) {
        return createEservicePurpose(eservice, null);
    }

    @Override
    public Purpose createEservicePurpose(Eservice eservice, Consumer<PurposeSeed> overrides) {
        PurposeSeed seed = buildDefaultPurposeSeed(eservice);
        if (overrides != null) {
            overrides.accept(seed);
        }

        CreatedResource created = purposesApi.createPurpose(seed);
        return getPurpose(created.getId());
    }

    @Override
    public Purpose associatePurposeToClient(Purpose purpose, Client client) {
        // Step successivo: bind purpose-client quando allinei endpoint/modello
        return purpose;
    }

    @Override
    public Purpose getPurpose(UUID purposeId) {
        Purpose purpose = PollingUtils.pollUntil(
                () -> new Purpose(purposesApi.getPurpose(purposeId)),
                resp -> resp != null && Objects.equals(purposeId, resp.getId()),
                Duration.ofSeconds(20),
                Duration.ofSeconds(2)
        );

        context.upsert(purpose);
        return purpose;
    }

    private PurposeSeed buildDefaultPurposeSeed(Eservice eservice) {
        UUID consumerId = currentUserContext.getTenant().getOrganizationId();
        String title = "purpose-" + UUID.randomUUID().toString().substring(0, 8);

        return new PurposeSeed()
                .title(title)
                .description("Default purpose description")
                .isFreeOfCharge(true)
                .freeOfChargeReason("free of charge")
                .dailyCalls(1)
                .eserviceId(eservice.getId())
                .consumerId(consumerId);
    }
}