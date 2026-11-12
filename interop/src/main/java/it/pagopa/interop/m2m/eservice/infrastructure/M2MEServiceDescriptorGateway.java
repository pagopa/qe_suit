package it.pagopa.interop.m2m.eservice.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.response.ApiResponse;
import it.pagopa.infrastructure.response.RawResponse;
import it.pagopa.infrastructure.template.action.PollingAction;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.agreement.domain.AgreementApprovalPolicy;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorGateway;
import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.*;
import it.pagopa.interop.common.kernel.domain.ApiInterface;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Document;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.Problem;
import it.pagopa.interop.m2m.eservice_template.infrastructure.FileDownloadMultipart;
import it.pagopa.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class M2MEServiceDescriptorGateway implements EServiceDescriptorGateway {

    private final M2MEServiceRestClient restClient;
    private final EntityStore entityStore;

    @Override
    public EServiceDescriptor getEServiceDescriptor(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef) {
        return restClient.getEServiceDescriptor(eServiceRef.id(), descriptorRef.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(descriptor -> {
                    Optional<EService> existingEService = entityStore.getById(eServiceRef.id(), EService.class);
                    EServiceDescriptor existingDescriptor = existingEService
                            .map(service -> service.findDescriptor(descriptorRef.id()))
                            .orElse(null);
                    EServiceDescriptorInterface remoteInterface = fetchRemoteInterface(eServiceRef, descriptorRef);
                    EServiceDescriptor mapped = toDomainDescriptor(descriptor, existingDescriptor, remoteInterface);
                    existingEService.ifPresent(service -> {
                        List<EServiceDescriptor> descriptors = service.getDescriptors() != null
                                ? new ArrayList<>(service.getDescriptors())
                                : new ArrayList<>();
                        EService updatedService = service.toBuilder().descriptors(descriptors).build();
                        updatedService.upsertDescriptor(mapped);
                        entityStore.upsert(updatedService);
                    });
                    return mapped;
                })
                .get();
    }

    @Override
    public void shouldFailToUploadInterface(
            EServiceRef eServiceRef,
            EServiceDescriptorRef descriptorRef,
            String interfaceResourcePath,
            SoapInterfaceUploadFailureReason reason
    ) {
        File document = FileUtils.loadClasspathResourceAsTempFile(interfaceResourcePath);
        String prettyName = Path.of(interfaceResourcePath).getFileName().toString();
        RawResponse rawResponse = restClient.uploadEServiceDescriptorInterface(
                        eServiceRef.id(), descriptorRef.id(), prettyName, document
                )
                .withoutPolling()
                .assertStatusCode(400)
                .getRaw();
        assertSoapInterfaceProblem(rawResponse, reason);
    }

    @Override
    public EServiceDescriptor awaitDescriptorState(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, EServiceDescriptorState expectedState) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EServiceDescriptor addDescriptor(EServiceRef eServiceRef) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EServiceDescriptor addDocument(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, String path, String name) {
        throw new UnsupportedOperationException("Adding descriptor documents is not supported on M2M V3");
    }

    @Override
    public EServiceDescriptor publishDescriptor(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EServiceDescriptor publishDescriptorAwaitingApproval(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EServiceDescriptor suspendDescriptor(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EServiceDescriptor updateDescriptor(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, UpdateEServiceDescriptorCommand command) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EServiceDescriptor linkOpenApiInterface(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, ApiInterface apiInterface) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EServiceDescriptor linkOpenApiCallbackInterface(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, ApiInterface apiInterface) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void archiveDescriptor(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, GracePeriodDays gracePeriodDays) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EServiceDescriptor configureTemplateInstanceSOAPInterface(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, List<SoapAddressConfiguration> addresses) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void shouldFailToConfigureSOAPInterface(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, List<SoapAddressConfiguration> addresses, EServiceTemplateSOAPInterfaceFailureReason reason) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EServiceDescriptor configureTemplateInstanceRESTInterface(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, RestInterfaceConfiguration configuration) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EServiceDescriptor downloadInterface(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<EServiceDescriptorInterface> readInterfaceSnapshot(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<EServiceDescriptorInterface> readInterfaceSnapshotAfterDelay(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean supports(@NonNull Channel channel) {
        return channel == Channel.M2M_V3;
    }

    private EServiceDescriptorInterface fetchRemoteInterface(
            EServiceRef eServiceRef,
            EServiceDescriptorRef descriptorRef
    ) {
        PollingAction<FileDownloadMultipart> action = restClient
                .downloadEServiceDescriptorInterface(eServiceRef.id(), descriptorRef.id())
                .withoutPolling();
        RawResponse rawResponse = action.getRaw();
        if (!(rawResponse instanceof ApiResponse apiResponse)) {
            throw new IllegalStateException("Response is not ApiResponse");
        }
        int statusCode = apiResponse.getStatusCode();
        if (statusCode == 404) {
            return null;
        }
        if (statusCode == 200) {
            FileDownloadMultipart multipart = Objects.requireNonNull(
                    action.get(),
                    "Expected non-null multipart body when status code is 200"
            );
            Document metadata = Document.builder()
                    .id(multipart.getId())
                    .name(multipart.getFilename())
                    .contentType(multipart.getContentType())
                    .prettyName(multipart.getPrettyName())
                    .build();
            byte[] contentBytes = multipart.getFile() != null
                    ? multipart.getFile().getBytes(StandardCharsets.UTF_8)
                    : null;
            return new EServiceDescriptorInterface(metadata, eServiceRef, descriptorRef, contentBytes);
        }
        throw new AssertionError(
                "Unexpected status code when downloading descriptor interface: " + statusCode
        );
    }

    private static EServiceDescriptor toDomainDescriptor(
            it.pagopa.interop.generated.openapi.clients.m2m.v3.model.EServiceDescriptor source,
            EServiceDescriptor existing,
            EServiceDescriptorInterface remoteInterface
    ) {
        var builder = existing != null ? existing.toBuilder() : EServiceDescriptor.builder();
        return builder.id(source.getId())
                .version(source.getVersion())
                .description(source.getDescription())
                .state(EServiceDescriptorState.valueOf(source.getState().name()))
                .voucherLifespan(source.getVoucherLifespan())
                .dailyCallsPerConsumer(source.getDailyCallsPerConsumer())
                .dailyCallsTotal(source.getDailyCallsTotal())
                .agreementApprovalPolicy(AgreementApprovalPolicy.valueOf(source.getAgreementApprovalPolicy().name()))
                .publishedAt(parseInstant(source.getPublishedAt()))
                .suspendedAt(parseInstant(source.getSuspendedAt()))
                .deprecatedAt(parseInstant(source.getDeprecatedAt()))
                .archivedAt(parseInstant(source.getArchivedAt()))
                .clearAudience()
                .audience(source.getAudience())
                .clearServerUrls()
                .serverUrls(source.getServerUrls())
                .interfaceDocument(remoteInterface)
                .build();
    }

    private static Instant parseInstant(String dateStr) {
        return dateStr == null || dateStr.isBlank() ? null : OffsetDateTime.parse(dateStr).toInstant();
    }

    private static void assertSoapInterfaceProblem(RawResponse rawResponse, SoapInterfaceUploadFailureReason reason) {
        Problem problem = rawResponse.as(Problem.class);
        Assertions.assertThat(problem).isNotNull();
        Assertions.assertThat(problem.getStatus()).isEqualTo(400);
        Assertions.assertThat(problem.getTitle()).isNotEqualTo("Unexpected error");

        switch (reason) {
            case MISSING_SOAP_ADDRESS -> assertProblemFields(
                    problem, "Error extracting field from SOAP file", "Error extracting field soap:address from SOAP file", "10017"
            );
            case NOT_PARSABLE_SOAP_FILE -> assertProblemFields(
                    problem, "Error while parsing SOAP file", "Error while parsing SOAP file", "10016"
            );
            case MISSING_SOAP_OPERATION -> assertProblemFields(
                    problem, "Error extracting field from SOAP file", "Error extracting field soap:operation from SOAP file", "10017"
            );
        }
    }

    private static void assertProblemFields(Problem problem, String expectedTitle, String expectedDetail, String expectedCodeSuffix) {
        Assertions.assertThat(problem.getTitle()).isEqualTo(expectedTitle);
        if (problem.getDetail() != null) {
            Assertions.assertThat(problem.getDetail()).isEqualTo(expectedDetail);
        }
        Assertions.assertThat(problem.getErrors()).isNotNull().isNotEmpty().anySatisfy(error -> {
            Assertions.assertThat(error.getCode()).endsWith(expectedCodeSuffix);
            Assertions.assertThat(error.getDetail()).isEqualTo(expectedDetail);
        });
    }
}