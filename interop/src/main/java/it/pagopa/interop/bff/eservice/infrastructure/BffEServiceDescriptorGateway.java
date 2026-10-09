package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.interop.bff.eservice.application.BffUpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.kernel.domain.DocumentKind;
import it.pagopa.interop.generated.openapi.clients.bff.model.AsyncExchangeProperties;
import it.pagopa.utils.FileUtils;
import it.pagopa.utils.RandomUtils;
import it.pagopa.utils.async.DelayUtils;
import it.pagopa.utils.async.PollingUtils;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorGateway;
import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;
import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.generated.openapi.clients.bff.model.GracePeriodDaysSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;

import static it.pagopa.interop.common.eservice.domain.EServiceDescriptorState.PUBLISHED;
import static org.instancio.Select.field;

@Service
@RequiredArgsConstructor
public class BffEServiceDescriptorGateway implements EServiceDescriptorGateway {

    private final BffEServiceRestClient restClient;
    private final EntityStore entityStore;
    private final BffEServiceDescriptorMapper mapper;

    @Override
    public EServiceDescriptor getEServiceDescriptor(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef) {
        return restClient.readDescriptor(eServiceRef.id(), descriptorRef.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(descriptor -> {
                    Optional<EService> maybeEService = entityStore.getById(eServiceRef.id(), EService.class);
                    return mapper.toEServiceWithUpsert(descriptor, maybeEService.orElse(null));
                })
                .updateContext()
                .map(eService -> eService.findDescriptor(descriptorRef.id()))
                .get();
    }

    @Override
    public EServiceDescriptor addDescriptor(EServiceRef eServiceRef) {
        return restClient.addDescriptor(eServiceRef.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(createdResource -> getEServiceDescriptor(eServiceRef, EServiceDescriptorRef.of(createdResource.getId())))
                .get();
    }

    @Override
    public EServiceDescriptor publishDescriptor(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef) {
        restClient.publishDescriptor(eServiceRef.id(), descriptorRef.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(emptyResp -> getEServiceDescriptor(eServiceRef, descriptorRef))
                .get();

        return PollingUtils.pollUntil(
                () -> getEServiceDescriptor(eServiceRef, descriptorRef),
                descriptor -> descriptor.getState() == PUBLISHED
        );
    }

    @Override
    public EServiceDescriptor updateDescriptor(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, UpdateEServiceDescriptorCommand command) {
        if (!(command instanceof BffUpdateEServiceDescriptorCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of BffUpdateEServiceDescriptorCommand");

        // Se è un e-service asincrono e le relative proprietà insieme all'interfaccia di callback
        // non sono stati ancora definiti, vengono soddisfatti con dei valori di default
        if (bffCommand.getBffPayload().getAsyncExchangeProperties() == null) {
            Optional<EService> maybeEService = entityStore.getById(eServiceRef.id(), EService.class);
            Assertions.assertThat(maybeEService.isPresent()).isTrue();

            if (maybeEService.get().getAsyncExchange()) {
                linkOpenApiCallbackInterface(eServiceRef, descriptorRef, "assets/origin-interface.yaml");
                AsyncExchangeProperties properties = new AsyncExchangeProperties();
                properties.setResponseTime(60);
                properties.setResourceAvailableTime(60);
                properties.setMaxResultSet(1);
                properties.setConfirmation(false);
                properties.setBulk(true);
                bffCommand.getBffPayload().setAsyncExchangeProperties(properties);
            }
        }
        restClient.updateDescriptor(eServiceRef.id(), descriptorRef.id(), bffCommand.getBffPayload())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(createdResource -> getEServiceDescriptor(eServiceRef, descriptorRef))
                .get();

        return PollingUtils.pollUntil(
                () -> getEServiceDescriptor(eServiceRef, descriptorRef),
                descriptor -> (
                        descriptor.getDailyCallsPerConsumer().intValue() == bffCommand.getBffPayload().getDailyCallsPerConsumer().intValue()
                        && descriptor.getDailyCallsTotal().intValue() == bffCommand.getBffPayload().getDailyCallsTotal().intValue()
                )
        );
    }

    @Override
    public EServiceDescriptor linkOpenApiInterface(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, String openApiInterfacePath) {
        return linkOpenApiInterface(eServiceRef, descriptorRef, openApiInterfacePath, DocumentKind.INTERFACE);
    }

    @Override
    public EServiceDescriptor linkOpenApiCallbackInterface(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, String openApiInterfacePath) {
        return linkOpenApiInterface(eServiceRef, descriptorRef, openApiInterfacePath, DocumentKind.ASYNC_EXCHANGE_CALLBACK_INTERFACE);
    }

    private EServiceDescriptor linkOpenApiInterface(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, String openApiInterfacePath, DocumentKind documentKind) {
        File openapiFile = FileUtils.loadClasspathResourceAsTempFile(openApiInterfacePath);
        String documentName = RandomUtils.randomAlphanumericName("interface") + ".yaml";
        DelayUtils.waitForSeconds(1); // Wait for a second to avoid potential eventual consistency error

        return restClient.addDocument(
                        eServiceRef.id(),
                        descriptorRef.id(),
                        documentKind.name(),
                        documentName,
                        openapiFile
                )
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(createdResource -> getEServiceDescriptor(eServiceRef, descriptorRef))
                .get();
    }

    @Override
    public void archiveDescriptor(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, GracePeriodDays gracePeriodDays) {
        GracePeriodDaysSeed payload = Instancio.of(GracePeriodDaysSeed.class)
                .set(field(GracePeriodDaysSeed::getGracePeriodDays), it.pagopa.interop.generated.openapi.clients.bff.model.GracePeriodDays.fromValue(gracePeriodDays.getDays()))
                .create();

        restClient.scheduleArchiveEserviceDescriptor(eServiceRef.id(), descriptorRef.id(), payload)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}
