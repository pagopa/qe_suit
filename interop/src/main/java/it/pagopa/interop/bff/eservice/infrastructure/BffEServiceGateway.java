package it.pagopa.interop.bff.eservice.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.application.context.EntityStore;
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
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceArchivingSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.FileResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.PresignedUrl;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServices;
import it.pagopa.utils.FileUtils;
import it.pagopa.utils.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.instancio.Instancio;
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
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@Service
@RequiredArgsConstructor
public class BffEServiceGateway implements EServiceGateway {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final BffEServiceRestClient restClient;
    private final BffEServiceDescriptorGateway descriptorGateway;
    private final EntityStore entityStore;
    private final BffEServiceMapper mapper;

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
    public void updateEService(EServiceRef eServiceRef, EServiceUpdateCommand command) {
        if (!(command instanceof BffEServiceUpdateCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of BffEServiceUpdateCommand");

        restClient.updateEService(eServiceRef.id(), bffCommand.getBffUpdatePayload())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();;
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
        EServiceArchivingSeed payload = Instancio.of(EServiceArchivingSeed.class)
                .set(field(EServiceArchivingSeed::getGracePeriodDays), it.pagopa.interop.generated.openapi.clients.bff.model.GracePeriodDays.fromValue(gracePeriodDays.getDays()))
                .set(field(EServiceArchivingSeed::getArchivingReason), RandomUtils.randomAlphanumericName("archiving-reason"))
                .create();

        restClient.scheduleArchiveEservice(eServiceRef.id(), payload)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
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
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}
