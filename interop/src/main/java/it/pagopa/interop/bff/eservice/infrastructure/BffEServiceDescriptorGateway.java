package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.interop.bff.eservice.application.BffUpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.kernel.domain.DocumentKind;
import it.pagopa.interop.generated.openapi.clients.bff.model.AsyncExchangeProperties;
import it.pagopa.utils.FileUtils;
import it.pagopa.utils.RandomUtils;
import it.pagopa.utils.async.DelayUtils;
import it.pagopa.utils.async.PollingUtils;
import it.pagopa.utils.async.DelayUtils;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorGateway;
import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceTemplateInterfaceFailureReason;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;
import it.pagopa.interop.common.eservice.domain.RestInterfaceConfiguration;
import it.pagopa.interop.common.eservice.domain.SoapAddressConfiguration;
import it.pagopa.interop.common.kernel.domain.Document;
import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.generated.openapi.clients.bff.model.GracePeriodDaysSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static it.pagopa.interop.common.eservice.domain.EServiceDescriptorState.PUBLISHED;
import static org.instancio.Select.field;

@Service
@RequiredArgsConstructor
@Slf4j
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
    public EServiceDescriptor configureTemplateInstanceSOAPInterface(
            EServiceRef eServiceRef,
            EServiceDescriptorRef descriptorRef,
            List<SoapAddressConfiguration> addresses
    ) {
        log.info("PIN-10024 SOAP configuration: eServiceId={}, descriptorId={}, addresses={}",
                eServiceRef.id(), descriptorRef.id(), addresses);
        restClient.addEServiceTemplateInstanceInterfaceSoap(
                        eServiceRef.id(), descriptorRef.id(), soapPayload(addresses)
                )
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();

        EServiceDescriptor configured = PollingUtils.pollUntil(
                () -> getEServiceDescriptor(eServiceRef, descriptorRef),
                descriptor -> descriptor.getInterfaceDocument() != null
        );
        restClient.getEServiceDocumentById(
                        eServiceRef.id(),
                        descriptorRef.id(),
                        configured.getInterfaceDocument().getId()
                )
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();
        return configured;
    }

    @Override
    public void shouldFailToConfigureSOAPInterface(
            EServiceRef eServiceRef,
            EServiceDescriptorRef descriptorRef,
            List<SoapAddressConfiguration> addresses,
            EServiceTemplateInterfaceFailureReason reason
    ) {
        log.info("PIN-10024 expected SOAP rejection (HTTP 400): eServiceId={}, descriptorId={}, reason={}, addresses={}",
                eServiceRef.id(), descriptorRef.id(), reason, addresses);
        EServiceDescriptor beforeDescriptor = getEServiceDescriptor(eServiceRef, descriptorRef);
        Document beforeDocument = beforeDescriptor.getInterfaceDocument();
        byte[] beforeBytes = beforeDocument == null
                ? null
                : downloadDocument(eServiceRef, descriptorRef, beforeDocument.getId());

        restClient.addEServiceTemplateInstanceInterfaceSoap(
                        eServiceRef.id(), descriptorRef.id(), soapPayload(addresses)
                )
                .withoutPolling()
                .assertStatusCode(400);

        DelayUtils.waitForSeconds(15);

        EServiceDescriptor afterDescriptor = getEServiceDescriptor(eServiceRef, descriptorRef);
        Document afterDocument = afterDescriptor.getInterfaceDocument();
        Assertions.assertThat(afterDocument).as("Interfaccia invariata dopo il rifiuto (%s)", reason)
                .isEqualTo(beforeDocument);
        if (beforeDocument != null) {
            Assertions.assertThat(downloadDocument(eServiceRef, descriptorRef, afterDocument.getId()))
                    .as("Byte dell'interfaccia invariati dopo il rifiuto (%s)", reason)
                    .containsExactly(beforeBytes);
        }
    }

    @Override
    public EServiceDescriptor configureTemplateInstanceRESTInterface(
            EServiceRef eServiceRef,
            EServiceDescriptorRef descriptorRef,
            RestInterfaceConfiguration configuration
    ) {
        Map<String, Object> payload = restPayload(configuration);
        restClient.addEServiceTemplateInstanceInterfaceRest(eServiceRef.id(), descriptorRef.id(), payload)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();

        EServiceDescriptor configured = PollingUtils.pollUntil(
                () -> getEServiceDescriptor(eServiceRef, descriptorRef),
                descriptor -> descriptor.getInterfaceDocument() != null
        );
        restClient.getEServiceDocumentById(
                        eServiceRef.id(),
                        descriptorRef.id(),
                        configured.getInterfaceDocument().getId()
                )
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();
        return configured;
    }

    private byte[] downloadDocument(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, UUID documentId) {
        return restClient.getEServiceDocumentById(eServiceRef.id(), descriptorRef.id(), documentId)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();
    }

    @Override
    public byte[] downloadInterfaceBytes(
            EServiceRef eServiceRef,
            EServiceDescriptorRef descriptorRef,
            UUID documentId
    ) {
        return downloadDocument(eServiceRef, descriptorRef, documentId);
    }

    static Map<String, Object> soapPayload(List<SoapAddressConfiguration> addresses) {
        List<Map<String, String>> serverUrls = addresses.stream()
                .map(address -> Map.of("url", address.address()))
                .toList();
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("serverUrls", serverUrls);
        return payload;
    }

    private static Map<String, Object> restPayload(RestInterfaceConfiguration configuration) {
        List<Map<String, String>> serverUrls = configuration.serverUrls().stream()
                .map(url -> Map.of("url", url))
                .toList();
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("serverUrls", serverUrls);
        payload.put("contactName", configuration.contactName());
        payload.put("contactEmail", configuration.contactEmail());
        if (configuration.contactUrl() != null) {
            payload.put("contactUrl", configuration.contactUrl());
        }
        if (configuration.termsAndConditionsUrl() != null) {
            payload.put("termsAndConditionsUrl", configuration.termsAndConditionsUrl());
        }
        return payload;
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
