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
        UUID eserviceId = eservice.getId();
        UUID descriptorId = eservice.getLastDescriptorId();
        eservicesApi.publishDescriptor(eserviceId, descriptorId);

        Eservice publishedEservice = PollingUtils.pollUntil(
                () -> new Eservice(eservicesApi.getCatalogEServiceDescriptor(eserviceId, descriptorId)),
                resp -> resp.getEservice().getDescriptors().stream()
                                    .filter(d -> Objects.equals(d.getId(), descriptorId))
                                    .findFirst()
                                    .map(CompactDescriptor::getState)
                                    .orElse(null) == EServiceDescriptorState.PUBLISHED,
                Duration.ofSeconds(15),
                Duration.ofSeconds(2)
        );

        context.upsert(publishedEservice);
        return publishedEservice;
    }

    @Override
    public Eservice getEservice(UUID eserviceId, UUID descriptorId) {
        Eservice eservice = PollingUtils.pollUntil(
                () -> new Eservice(eservicesApi.getCatalogEServiceDescriptor(eserviceId, descriptorId)),
                resp -> {
                    if (resp == null || !Objects.equals(eserviceId, resp.getId())) return false;

                    return resp.getEservice().getDescriptors().stream()
                            .map(CompactDescriptor::getId)
                            .anyMatch(id -> Objects.equals(descriptorId, id));
                },
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
