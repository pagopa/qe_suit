package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.bff.eservice.application.BffUpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorGateway;
import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.*;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.DocumentKind;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static it.pagopa.interop.common.eservice.domain.EServiceDescriptorState.PUBLISHED;

@Service
@RequiredArgsConstructor
public class BffEServiceDescriptorGateway implements EServiceDescriptorGateway {
    private final BffEServiceRestClient restClient;
    private final EntityStore entityStore;
    private final BffEServiceDescriptorMapper mapper;
    private final BffEServiceDescriptorInterfaceGateway interfaceGateway;
    private final BffEServiceDescriptorArchiveGateway archiveGateway;

    @Override
    public EServiceDescriptor getEServiceDescriptor(EServiceRef serviceRef, EServiceDescriptorRef descriptorRef) {
        return restClient.readDescriptor(serviceRef.id(), descriptorRef.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(descriptor -> {
                    Optional<EService> existing = entityStore.getById(serviceRef.id(), EService.class);
                    return mapper.toEServiceWithUpsert(descriptor, existing.orElse(null));
                })
                .updateContext()
                .map(service -> service.findDescriptor(descriptorRef.id()))
                .get();
    }

    @Override
    public EServiceDescriptor addDescriptor(EServiceRef serviceRef) {
        return restClient.addDescriptor(serviceRef.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(created -> getEServiceDescriptor(serviceRef, EServiceDescriptorRef.of(created.getId())))
                .get();
    }

    @Override
    public EServiceDescriptor publishDescriptor(EServiceRef serviceRef, EServiceDescriptorRef descriptorRef) {
        restClient.publishDescriptor(serviceRef.id(), descriptorRef.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(ignored -> getEServiceDescriptor(serviceRef, descriptorRef))
                .get();
        return PollingUtils.pollUntil(
                () -> getEServiceDescriptor(serviceRef, descriptorRef),
                descriptor -> descriptor.getState() == PUBLISHED
        );
    }

    @Override
    public EServiceDescriptor updateDescriptor(
            EServiceRef serviceRef,
            EServiceDescriptorRef descriptorRef,
            UpdateEServiceDescriptorCommand command
    ) {
        if (!(command instanceof BffUpdateEServiceDescriptorCommand bffCommand)) {
            throw new IllegalArgumentException("Command must be an instance of BffUpdateEServiceDescriptorCommand");
        }
        setDefaultAsyncPropertiesIfNeeded(serviceRef, descriptorRef, bffCommand);
        restClient.updateDescriptor(serviceRef.id(), descriptorRef.id(), bffCommand.getBffPayload())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(ignored -> getEServiceDescriptor(serviceRef, descriptorRef))
                .get();
        return PollingUtils.pollUntil(
                () -> getEServiceDescriptor(serviceRef, descriptorRef),
                descriptor -> descriptor.getDailyCallsPerConsumer().intValue()
                        == bffCommand.getBffPayload().getDailyCallsPerConsumer().intValue()
                        && descriptor.getDailyCallsTotal().intValue()
                        == bffCommand.getBffPayload().getDailyCallsTotal().intValue()
        );
    }

    @Override
    public EServiceDescriptor linkOpenApiInterface(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, String openApiInterfacePath, String name) {
        return linkDocument(eServiceRef, descriptorRef, openApiInterfacePath, DocumentKind.INTERFACE, name);
    }

    private void setDefaultAsyncPropertiesIfNeeded(
            EServiceRef serviceRef,
            EServiceDescriptorRef descriptorRef,
            BffUpdateEServiceDescriptorCommand command
    ) {
        if (command.getBffPayload().getAsyncExchangeProperties() != null) return;
        EService service = entityStore.getById(serviceRef.id(), EService.class).orElseThrow();
        if (!service.getAsyncExchange()) return;
        linkOpenApiCallbackInterface(serviceRef, descriptorRef, "assets/origin-interface.yaml", null);
        it.pagopa.interop.generated.openapi.clients.bff.model.AsyncExchangeProperties properties =
                new it.pagopa.interop.generated.openapi.clients.bff.model.AsyncExchangeProperties()
                        .responseTime(60)
                        .resourceAvailableTime(60)
                        .maxResultSet(1)
                        .confirmation(false)
                        .bulk(true);
        command.getBffPayload().setAsyncExchangeProperties(properties);
    }


    @Override
    public EServiceDescriptor linkOpenApiCallbackInterface(
            EServiceRef serviceRef,
            EServiceDescriptorRef descriptorRef,
            String path,
            String name
    ) {
        return linkDocument(serviceRef, descriptorRef, path, DocumentKind.ASYNC_EXCHANGE_CALLBACK_INTERFACE, name);
    }

    private EServiceDescriptor linkDocument(
            EServiceRef serviceRef,
            EServiceDescriptorRef descriptorRef,
            String path,
            DocumentKind kind,
            String name
    ) {
        return interfaceGateway.linkDocument(serviceRef, descriptorRef, path, kind, name);
    }

    @Override
    public void archiveDescriptor(EServiceRef serviceRef, EServiceDescriptorRef descriptorRef,
                                  GracePeriodDays gracePeriodDays) {
        archiveGateway.archive(serviceRef, descriptorRef, gracePeriodDays);
    }

    @Override
    public EServiceDescriptor configureTemplateInstanceSOAPInterface(
            EServiceRef serviceRef,
            EServiceDescriptorRef descriptorRef,
            List<SoapAddressConfiguration> addresses
    ) {
        return interfaceGateway.configureSOAP(serviceRef, descriptorRef, addresses);
    }

    @Override
    public void shouldFailToConfigureSOAPInterface(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, List<SoapAddressConfiguration> addresses, EServiceTemplateSOAPInterfaceFailureReason reason) {
        interfaceGateway.shouldFailToConfigureSOAP(eServiceRef, descriptorRef, addresses, reason);
    }

    @Override
    public EServiceDescriptor configureTemplateInstanceRESTInterface(
            EServiceRef serviceRef, EServiceDescriptorRef descriptorRef,
            RestInterfaceConfiguration configuration
    ) {
        return interfaceGateway.configureREST(serviceRef, descriptorRef, configuration);
    }

    @Override
    public EServiceDescriptor downloadInterface(EServiceRef serviceRef, EServiceDescriptorRef descriptorRef) {
        return interfaceGateway.downloadInterface(serviceRef, descriptorRef);
    }

    @Override
    public Optional<EServiceDescriptorInterface> readInterfaceSnapshot(
            EServiceRef serviceRef,
            EServiceDescriptorRef descriptorRef
    ) {
        return interfaceGateway.readSnapshot(serviceRef, descriptorRef);
    }

    @Override
    public Optional<EServiceDescriptorInterface> readInterfaceSnapshotAfterDelay(
            EServiceRef serviceRef,
            EServiceDescriptorRef descriptorRef
    ) {
        return interfaceGateway.readSnapshotAfterDelay(serviceRef, descriptorRef);
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.BFF;
    }
}
