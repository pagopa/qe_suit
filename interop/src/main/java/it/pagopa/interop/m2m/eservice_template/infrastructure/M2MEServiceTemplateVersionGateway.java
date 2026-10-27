package it.pagopa.interop.m2m.eservice_template.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateVersionGateway;
import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateVersionCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateInterface;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateVersionRef;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.FileDownloadMultipart;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class M2MEServiceTemplateVersionGateway implements EServiceTemplateVersionGateway {

    private static final Pattern MULTIPART_FIELD_NAME = Pattern.compile("(?:^|;)\\s*name=\"?([^\";]+)\"?");

    private final M2MEServiceTemplateRestClient restClient;
    private final EntityStore entityStore;

    @Override
    public EServiceTemplateVersion addVersion(EServiceTemplateRef templateRef) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EServiceTemplateVersion getEServiceTemplateVersion(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EServiceTemplateVersion publishEServiceTemplateVersion(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EServiceTemplateVersion uploadInterface(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef, String fixturePath) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EServiceTemplateVersion updateDraftVersion(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef, UpdateEServiceTemplateVersionCommand command) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EServiceTemplateInterface downloadInterface(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef) {
        return getInterfaceMetadata(templateRef, versionRef)
                .withContent(download(templateRef.id(), versionRef.versionId()));
    }

    @Override
    public Optional<EServiceTemplateInterface> readCurrentInterface(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef) {
        return Optional.of(downloadInterface(templateRef, versionRef));
    }

    @Override
    public boolean supports(@NonNull Channel delimiter) {
        return delimiter == Channel.M2M_V3;
    }

    private byte[] download(UUID templateId, UUID versionId) {
        byte[] download = restClient.downloadEServiceTemplateVersionInterface(templateId, versionId)
            .withPolling(PollingStrategy.UNTIL_SUCCESS)
            .get();

        return processDownloadedFile(download).getFile().getBytes(StandardCharsets.UTF_8);
    }

    private FileDownloadMultipart processDownloadedFile(byte[] downloadedFile) {
        if (downloadedFile == null || downloadedFile.length == 0) {
            throw new IllegalArgumentException("Downloaded multipart file is empty");
        }

        Charset bytePreservingCharset = StandardCharsets.ISO_8859_1;
        String body = new String(downloadedFile, bytePreservingCharset);
        int firstLineEnd = body.indexOf("\r\n");
        if (firstLineEnd < 3 || !body.startsWith("--")) {
            throw new IllegalArgumentException("Downloaded content is not a valid multipart body");
        }

        String boundary = body.substring(0, firstLineEnd);
        FileDownloadMultipart result = new FileDownloadMultipart();
        int partStart = firstLineEnd + 2;
        String nextBoundaryMarker = "\r\n" + boundary;

        while (!body.startsWith(boundary + "--", partStart)) {
            if (!body.startsWith(boundary + "\r\n", partStart)) {
                throw new IllegalArgumentException("Downloaded multipart body has an invalid boundary");
            }

            int headersStart = partStart + boundary.length() + 2;
            int headersEnd = body.indexOf("\r\n\r\n", headersStart);
            if (headersEnd < 0) {
                throw new IllegalArgumentException("Downloaded multipart part has invalid headers");
            }

            Matcher fieldNameMatcher = MULTIPART_FIELD_NAME.matcher(body.substring(headersStart, headersEnd));
            if (!fieldNameMatcher.find()) {
                throw new IllegalArgumentException("Downloaded multipart part has no field name");
            }

            int contentStart = headersEnd + 4;
            int contentEnd = body.indexOf(nextBoundaryMarker, contentStart);
            if (contentEnd < 0) {
                throw new IllegalArgumentException("Downloaded multipart part has no closing boundary");
            }
            String value = new String(
                    body.substring(contentStart, contentEnd).getBytes(bytePreservingCharset),
                    StandardCharsets.UTF_8
            );

            switch (fieldNameMatcher.group(1)) {
                case "file" -> result.setFile(value);
                case "filename" -> result.setFilename(value);
                case "contentType" -> result.setContentType(value);
                case "id" -> result.setId(UUID.fromString(value.trim()));
                case "prettyName" -> result.setPrettyName(value);
                default -> {
                }
            }
            partStart = contentEnd + 2;
        }

        if (result.getFile() == null) {
            throw new IllegalArgumentException("Downloaded multipart body does not contain a file field");
        }
        return result;
    }

    private EServiceTemplateInterface getInterfaceMetadata(
            EServiceTemplateRef templateRef,
            EServiceTemplateVersionRef versionRef
    ) {
        EServiceTemplate template = entityStore.getById(templateRef.id(), EServiceTemplate.class)
                .orElseThrow(() -> new IllegalStateException(
                        "EService template not found in entity store: " + templateRef.id()
                ));
        EServiceTemplateVersion version = Optional.ofNullable(template.findVersion(versionRef.versionId()))
                .orElseThrow(() -> new IllegalStateException(
                        "EService template version not found in entity store: " + versionRef.versionId()
                ));
        return Optional.ofNullable(version.getInterfaceDocument())
                .orElseThrow(() -> new IllegalStateException(
                        "The E-Service Template version has no interface"
                ));
    }
}
