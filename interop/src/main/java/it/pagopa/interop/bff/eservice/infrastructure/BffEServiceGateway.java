package it.pagopa.interop.bff.eservice.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.application.context.EntityStore;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.bff.eservice.application.BffEServiceCreationCommand;
import it.pagopa.interop.bff.eservice.application.BffEServiceUpdateCommand;
import it.pagopa.interop.common.eservice.application.EServiceGateway;
import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.domain.*;
import it.pagopa.interop.common.eservice.application.command.EServiceUpdateCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceArchivingFailureReason;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;
import it.pagopa.interop.common.eservice.domain.EServiceUpdateFailureReason;
import it.pagopa.interop.common.eservice.domain.EServiceCreationFailureReason;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.generated.openapi.clients.bff.model.FileResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.PresignedUrl;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServices;
import it.pagopa.utils.FileUtils;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.assertj.core.api.Assertions.assertThat;

@Service
@RequiredArgsConstructor
public class BffEServiceGateway implements EServiceGateway {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final BffEServiceRestClient restClient;
    private final BffEServiceDescriptorGateway descriptorGateway;
    private final EntityStore entityStore;
    private final BffEServiceMapper mapper;
    private final LastApiResponseStore lastApiResponseStore;
    private final BffEServiceRequestFactory requestFactory;

    @Override
    public EService createEService(EServiceCreationCommand command) {
        if (!(command instanceof BffEServiceCreationCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of BffEServiceCreationCommand");

        return restClient.createEService(bffCommand.getBffCreationPayload())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(createdEServiceDescriptor -> {
                    final EServiceRef eServiceRef = EServiceRef.of(createdEServiceDescriptor.getId());
                    final EServiceDescriptorRef descriptorRef = EServiceDescriptorRef.of(createdEServiceDescriptor.getDescriptorId());

                    final EServiceDescriptor createdDescriptor = descriptorGateway.getEServiceDescriptor(eServiceRef, descriptorRef);
                    final EService savedEService = getEService(eServiceRef);
                    savedEService.upsertDescriptor(createdDescriptor);

                    return savedEService;
                })
                .updateContext(command.getAlias())
                .get();
    }

    @Override
    public void tryUpdateEServiceMetadata(EServiceRef eServiceRef, EServiceUpdateCommand command) {
        if (!(command instanceof BffEServiceUpdateCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of BffEServiceUpdateCommand");
        restClient.updateEServiceMetadata(eServiceRef.id(), bffCommand.getBffUpdatePayload())
                .withoutPolling()
                .get();
    }

    @Override
    public void updateEServiceMetadata(EServiceRef eServiceRef, EServiceUpdateCommand command) {
        if (!(command instanceof BffEServiceUpdateCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of BffEServiceUpdateCommand");

        restClient.updateEServiceMetadata(eServiceRef.id(), bffCommand.getBffUpdatePayload())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();

        PollingUtils.pollUntil(
                () -> getEService(eServiceRef),
                eService -> matchesUpdate(eService, bffCommand),
                "I metadati aggiornati dell'e-service " + eServiceRef.id() + " non sono diventati visibili"
        );
    }

    @Override
    public void shouldFailToUpdateEServiceMetadata(EServiceUpdateFailureReason reason) {
        var expected = switch (reason) {
            case INVALID_DATA, NOT_ALLOWED -> 400;
            case CONFLICT, OUT_OF_DRAFT_OPERATION_FIELD -> 409;
            case UNAUTHORIZED -> 403;
            default -> throw new UnsupportedOperationException("Unsupported EServiceUpdateFailureReason: " + reason);
        };
        var actual = lastApiResponseStore.getLastResponse().getStatusCode();
        assertThat(actual).isEqualTo(expected);
    }

    @Override
    public void deleteEService(EService eService) {
        restClient.deleteEService(eService.getId())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();
    }

    @Override
    public EService getEService(EServiceRef eServiceRef) {
        return restClient.readEService(eServiceRef.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(eServiceDetails -> {
                    Optional<EService> maybeEService = entityStore.getById(eServiceRef.id(), EService.class);
                    return mapper.toEServicePreservingDescriptors(eServiceDetails, maybeEService.orElse(null));
                })
                .updateContext()
                .get();
    }

    @Override
    public void archiveEService(EServiceRef eServiceRef, GracePeriodDays gracePeriodDays) {
        var payload = requestFactory.archivingRequest(gracePeriodDays);

        restClient.scheduleArchiveEservice(eServiceRef.id(), payload)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();
    }

    @Override
    public void cloneEService(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef) {
        restClient.cloneEService(eServiceRef.id(), descriptorRef.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(createdEServiceDescriptor -> {
                    final EServiceRef clonedEServiceRef = EServiceRef.of(createdEServiceDescriptor.getId());
                    final EServiceDescriptorRef clonedDescriptorRef = EServiceDescriptorRef.of(createdEServiceDescriptor.getDescriptorId());

                    final EServiceDescriptor createdDescriptor = descriptorGateway.getEServiceDescriptor(clonedEServiceRef, clonedDescriptorRef);
                    final EService clonedEService = getEService(clonedEServiceRef);
                    clonedEService.upsertDescriptor(createdDescriptor);

                    return clonedEService;
                })
                .updateContext()
                .get();
    }

    @Override
    public void shouldFailToArchiveEService(EServiceRef eServiceRef, GracePeriodDays gracePeriodDays, EServiceArchivingFailureReason reason) {
        throw new UnsupportedOperationException("Verification of EService archiving rejection is not supported on the BFF channel");
    }

    @Override
    public void shouldFailToRequestEServiceArchiving(EServiceRef eServiceRef, GracePeriodDays gracePeriodDays, EServiceArchivingFailureReason reason) {
        throw new UnsupportedOperationException("Verification of delegated EService archiving request rejection is not supported on the BFF channel");
    }

    @Override
    public void shouldFailToImportSOAPEService(
            String eserviceName,
            String wsdlResourcePath,
            SoapInterfaceUploadFailureReason reason
    ) {
        byte[] zipBytes = buildSoapImportZip(eserviceName, wsdlResourcePath);
        String zipFileName = eserviceName + ".zip";
        PresignedUrl presignedUrl = restClient.getImportEservicePresignedUrl(zipFileName)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();
        restClient.uploadToPresignedUrl(presignedUrl.getUrl(), zipBytes)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();
        FileResource fileResource = new FileResource().filename(zipFileName).url(presignedUrl.getUrl());
        var rawResponse = restClient.importEService(fileResource)
//                .withPolling(PollingStrategy.UNTIL_ERROR)
                .withoutPolling()
                .assertStatusCode(400)
                .getRaw();
        BffSoapInterfaceProblemValidator.assertSoapInterfaceProblem(rawResponse, reason);
    }

    @Override
    public void assertEServiceNotPresentInProducerCatalog(String eserviceName) {
        ProducerEServices producerEServices = restClient.getProducerEServices(eserviceName)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();
        assertThat(producerEServices.getResults())
                .noneMatch(eService -> eserviceName.equals(eService.getName()));
    }

    private byte[] buildSoapImportZip(String eserviceName, String wsdlResourcePath) {
        File wsdlFile = FileUtils.loadClasspathResourceAsTempFile(wsdlResourcePath);
        String wsdlFileName = Path.of(wsdlResourcePath).getFileName().toString();
        Map<String, Object> descriptor = new LinkedHashMap<>();
        descriptor.put("description", "Descrizione valida della versione e-service SOAP");
        descriptor.put("interface", Map.of("prettyName", "Interfaccia WSDL SOAP", "path", wsdlFileName));
        descriptor.put("docs", List.of());
        descriptor.put("audience", List.of("QA"));
        descriptor.put("voucherLifespan", 60);
        descriptor.put("dailyCallsPerConsumer", 10);
        descriptor.put("dailyCallsTotal", 100);
        descriptor.put("agreementApprovalPolicy", "AUTOMATIC");
        Map<String, Object> configuration = new LinkedHashMap<>();
        configuration.put("name", eserviceName);
        configuration.put("description", "Descrizione valida per importazione e-service SOAP");
        configuration.put("technology", "SOAP");
        configuration.put("mode", "DELIVER");
        configuration.put("descriptor", descriptor);
        configuration.put("riskAnalysis", List.of());
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {
            zos.putNextEntry(new ZipEntry("configuration.json"));
            zos.write(OBJECT_MAPPER.writeValueAsBytes(configuration));
            zos.closeEntry();
            zos.putNextEntry(new ZipEntry(wsdlFileName));
            zos.write(Files.readAllBytes(wsdlFile.toPath()));
            zos.closeEntry();
            zos.finish();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("Errore nella creazione del pacchetto ZIP di importazione E-Service SOAP", e);
        }
    }

    @Override
    public void openEServiceCreation() {
        throw new UnsupportedOperationException("Opening EService creation is not supported on the BFF channel");
    }

    @Override
    public void openDraftEServiceGeneralData(EService eService) {
        throw new UnsupportedOperationException("Opening draft EService editing is not supported on the BFF channel");
    }

    @Override
    public void shouldFailToUpdateDraftEServiceDescription(String description, EServiceUpdateFailureReason reason) {
        throw new UnsupportedOperationException("Attempting draft EService description update is not supported on the BFF channel");
    }

    @Override
    public void shouldFailToUpdateDraftEServiceName(String name, EServiceUpdateFailureReason reason) {
        throw new UnsupportedOperationException("Attempting draft EService name update is not supported on the BFF channel");
    }

    @Override
    public void shouldFailToCreateEService(EServiceCreationCommand command, EServiceCreationFailureReason reason) {
        if (!(command instanceof BffEServiceCreationCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of BffEServiceCreationCommand");
        restClient.createEService(bffCommand.getBffCreationPayload()).withoutPolling();
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }

    private static boolean matchesUpdate(EService actual, BffEServiceUpdateCommand command) {
        var requested = command.getBffUpdatePayload();
        return isApplied(actual.getName(), requested.getName())
                && isApplied(actual.getDescription(), requested.getDescription())
                && isApplied(
                        actual.getTechnology() == null ? null : actual.getTechnology().getValue(),
                        requested.getTechnology() == null ? null : requested.getTechnology().getValue()
                )
                && isApplied(
                        actual.getMode() == null ? null : actual.getMode().getValue(),
                        requested.getMode() == null ? null : requested.getMode().getValue()
                )
                && isApplied(actual.getIsSignalHubEnabled(), requested.getIsSignalHubEnabled())
                && isApplied(actual.getIsConsumerDelegable(), requested.getIsConsumerDelegable())
                && isApplied(actual.getIsClientAccessDelegable(), requested.getIsClientAccessDelegable())
                && isApplied(actual.getPersonalData(), requested.getPersonalData())
                && isApplied(actual.getAsyncExchange(), requested.getAsyncExchange());
    }

    private static <T> boolean isApplied(T actual, T requested) {
        return requested == null || Objects.equals(actual, requested);
    }

    @Override
    public void attemptProducerEServiceDetailsRead(EServiceRef eServiceRef) {
        restClient.readEService(eServiceRef.id()).withoutPolling();
    }

    @Override
    public void attemptNameUpdate(EServiceRef eServiceRef, String name) {
        restClient.updateName(eServiceRef.id(), requestFactory.updateNameRequest().name(name)).withoutPolling();
    }

    @Override
    public void attemptDelegatedArchivingRequest(EServiceRef eServiceRef, GracePeriodDays gracePeriodDays) {
        restClient.submitDelegatedEServiceArchiving(eServiceRef.id(), requestFactory.archivingRequest(gracePeriodDays))
                .withoutPolling();
    }

    @Override
    public void attemptDescriptionUpdate(EServiceRef eServiceRef, String description) {
        restClient.updateDescription(eServiceRef.id(), requestFactory.updateDescriptionRequest().description(description))
                .withoutPolling();
    }

    @Override
    public void attemptPartialNameUpdate(EServiceRef eServiceRef, String name) {
        restClient.partialUpdateEService(eServiceRef.id(), requestFactory.partialNameUpdateRequest(name)).withoutPolling();
    }

    @Override
    public void attemptEServiceImportFromFile(String filePath) {
        File zipFile = resolveImportFixtureFile(filePath);
        String zipFileName = Path.of(filePath).getFileName().toString();
        byte[] zipBytes;
        try {
            zipBytes = Files.readAllBytes(zipFile.toPath());
        } catch (IOException e) {
            throw new UncheckedIOException("Errore nella lettura del file ZIP di importazione E-Service: " + filePath, e);
        }
        PresignedUrl presignedUrl = restClient.getImportEservicePresignedUrl(zipFileName)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();
        restClient.uploadToPresignedUrl(presignedUrl.getUrl(), zipBytes)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();
        FileResource fileResource = new FileResource().filename(zipFileName).url(presignedUrl.getUrl());
        restClient.importEService(fileResource).withoutPolling();
    }

    private static File resolveImportFixtureFile(String fixturePath) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = BffEServiceGateway.class.getClassLoader();
        }
        for (String candidate : List.of(fixturePath, "assets/" + fixturePath, "assets/eservice/" + fixturePath)) {
            String normalized = candidate.startsWith("/") ? candidate.substring(1) : candidate;
            if (classLoader.getResource(normalized) != null) {
                return FileUtils.loadClasspathResourceAsTempFile(normalized);
            }
        }
        throw new IllegalStateException("Risorsa non trovata nel classpath: " + fixturePath);
    }
}
