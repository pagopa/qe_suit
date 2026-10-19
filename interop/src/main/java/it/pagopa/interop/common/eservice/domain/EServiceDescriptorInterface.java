package it.pagopa.interop.common.eservice.domain;

import it.pagopa.domain.Identifiable;
import it.pagopa.interop.common.kernel.domain.Document;
import it.pagopa.interop.common.kernel.domain.DocumentMetadata;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public final class EServiceDescriptorInterface implements Identifiable, DocumentMetadata {
    private final Document metadata;
    private final EServiceRef eServiceRef;
    private final EServiceDescriptorRef descriptorRef;
    private final byte[] content;

    public EServiceDescriptorInterface(
            Document metadata,
            EServiceRef eServiceRef,
            EServiceDescriptorRef descriptorRef,
            byte[] content
    ) {
        this.metadata = Objects.requireNonNull(metadata);
        this.eServiceRef = eServiceRef;
        this.descriptorRef = descriptorRef;
        this.content = content == null ? null : Arrays.copyOf(content, content.length);
    }

    @Override
    public UUID getId() {
        return metadata.getId();
    }

    @Override
    public String getName() {
        return metadata.getName();
    }

    @Override
    public String getContentType() {
        return metadata.getContentType();
    }

    @Override
    public String getPrettyName() {
        return metadata.getPrettyName();
    }

    @Override
    public String getChecksum() {
        return metadata.getChecksum();
    }

    public EServiceRef getEServiceRef() {
        return eServiceRef;
    }

    public EServiceDescriptorRef getDescriptorRef() {
        return descriptorRef;
    }

    public Document getMetadata() {
        return metadata;
    }

    public byte[] getContent() {
        return content == null ? null : Arrays.copyOf(content, content.length);
    }

    public boolean hasDownloadedContent() {
        return content != null;
    }

    public EServiceDescriptorInterface withContent(byte[] bytes) {
        return new EServiceDescriptorInterface(metadata, eServiceRef, descriptorRef, bytes);
    }

    public EServiceDescriptorInterface refresh(Document updated, EServiceRef serviceRef, EServiceDescriptorRef ref) {
        byte[] retained = metadata.getId().equals(updated.getId())
                && metadata.getChecksum() != null
                && metadata.getChecksum().equals(updated.getChecksum()) ? content : null;
        return new EServiceDescriptorInterface(updated, serviceRef, ref, retained);
    }
}
