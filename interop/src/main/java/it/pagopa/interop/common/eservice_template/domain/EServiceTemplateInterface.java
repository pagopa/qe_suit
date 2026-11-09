package it.pagopa.interop.common.eservice_template.domain;

import it.pagopa.domain.Identifiable;
import it.pagopa.interop.common.kernel.domain.Document;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateVersionRef;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.List;
import it.pagopa.interop.common.eservice.domain.SoapAddressConfiguration;

@Getter
@EqualsAndHashCode
public final class EServiceTemplateInterface implements Identifiable {
    private final Document metadata;
    private final EServiceTemplateRef templateRef;
    private final EServiceTemplateVersionRef versionRef;
    private final byte[] content;

    public EServiceTemplateInterface(
            UUID id,
            EServiceTemplateRef templateRef,
            EServiceTemplateVersionRef versionRef,
            String name,
            String contentType,
            byte[] content
    ) {
        this(id, templateRef, versionRef, name, contentType, name, null, content);
    }

    public EServiceTemplateInterface(
            UUID id,
            EServiceTemplateRef templateRef,
            EServiceTemplateVersionRef versionRef,
            String name,
            String contentType,
            String prettyName,
            String checksum,
            byte[] content
    ) {
        this(Document.builder()
                        .id(id)
                        .name(name)
                        .contentType(contentType)
                        .prettyName(prettyName)
                        .checksum(checksum)
                        .build(),
                templateRef, versionRef, content);
    }

    private EServiceTemplateInterface(
            Document metadata,
            EServiceTemplateRef templateRef,
            EServiceTemplateVersionRef versionRef,
            byte[] content
    ) {
        this.metadata = Objects.requireNonNull(metadata);
        this.templateRef = Objects.requireNonNull(templateRef);
        this.versionRef = Objects.requireNonNull(versionRef);
        this.content = content == null ? null : Arrays.copyOf(content, content.length);
    }

    @Override
    public UUID getId() {
        return metadata.getId();
    }

    public String getName() {
        return metadata.getName();
    }

    public String getContentType() {
        return metadata.getContentType();
    }

    public String getPrettyName() {
        return metadata.getPrettyName();
    }

    public String getChecksum() {
        return metadata.getChecksum();
    }

    public byte[] getContent() {
        return content == null ? null : Arrays.copyOf(content, content.length);
    }

    public List<SoapAddressConfiguration> soapAddressesAt(String endpoint) {
        if (!hasDownloadedContent()) throw new IllegalStateException("Template interface content has not been downloaded");
        return new WsdlAddressReader().parse(content).stream()
                .map(address -> new SoapAddressConfiguration(address.service(), address.port(), endpoint))
                .toList();
    }

    public boolean hasDownloadedContent() {
        return content != null;
    }

    public EServiceTemplateInterface withContent(byte[] bytes) {
        return new EServiceTemplateInterface(metadata, templateRef, versionRef, bytes);
    }

    public EServiceTemplateInterface refreshMetadata(EServiceTemplateInterface updated) {
        byte[] retained = metadata.getId().equals(updated.getId())
                && metadata.getChecksum() != null
                && metadata.getChecksum().equals(updated.getChecksum()) ? content : null;
        return new EServiceTemplateInterface(
                updated.metadata, updated.templateRef, updated.versionRef, retained
        );
    }

}
