package it.pagopa.interop.domain.services.eservice.impl;

import it.pagopa.interop.domain.context.EserviceContext;
import it.pagopa.interop.domain.model.Eservice;
import it.pagopa.interop.domain.services.eservice.EserviceService;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.utils.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;


@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EserviceDataPreparationService implements EserviceService {

    private final EservicesApi eservicesApi;
    private final EserviceContext context;

    @Override
    public Eservice createEservice(EServiceSeed request) {
        CreatedEServiceDescriptor createdEservice = eservicesApi.createEService(request);
        return getEservice(createdEservice.getId(), createdEservice.getDescriptorId());
    }

    @Override
    public Eservice createEservice() {
        return createEservice(buildDefaultRequest());
    }

    @Override
    public Eservice createEservice(java.util.function.Consumer<EServiceSeed> overrides) {
        EServiceSeed seed = buildDefaultRequest();
        if (overrides != null) {
            // override solo dei campi che servono
            overrides.accept(seed);
        }
        return createEservice(seed);
    }

    @Override
    public Eservice publishEservice(Eservice eservice) {
        UUID eserviceId = eservice.getEserviceId();
        UUID descriptorId = eservice.getLastDraftDescriptorId();
        eservicesApi.publishDescriptor(eserviceId, descriptorId);

        Eservice publishedEservice = PollingUtils.pollUntil(
                () -> new Eservice(eservicesApi.getProducerEServiceDescriptor(eserviceId, descriptorId)),
                resp -> resp != null
                        && Objects.equals(descriptorId, resp.getId())
                        && resp.getState() == EServiceDescriptorState.PUBLISHED,
                Duration.ofSeconds(15),
                Duration.ofSeconds(2)
        );

        context.upsert(publishedEservice);
        return publishedEservice;
    }

    @Override
    public Eservice getEservice(UUID eserviceId, UUID descriptorId) {
        Eservice eservice = PollingUtils.pollUntil(
                () -> new Eservice(eservicesApi.getProducerEServiceDescriptor(eserviceId, descriptorId)),
                resp -> resp != null
                        && Objects.equals(eserviceId, resp.getEservice().getId())
                        && Objects.equals(descriptorId, resp.getId()),
                Duration.ofSeconds(15),
                Duration.ofSeconds(2)
        );

        context.upsert(eservice);
        return eservice;
    }

    private EServiceSeed buildDefaultRequest() {
        return new EServiceSeed()
                .name("Default EService - " + UUID.randomUUID().toString().substring(0, 8))
                .description("Default EService description")
                .technology(EServiceTechnology.REST)
                .mode(EServiceMode.DELIVER)
                .personalData(false)
                .isSignalHubEnabled(false)
                .isConsumerDelegable(false)
                .isClientAccessDelegable(false);
    }
}
