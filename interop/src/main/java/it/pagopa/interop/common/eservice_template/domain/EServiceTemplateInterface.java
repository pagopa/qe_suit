package it.pagopa.interop.common.eservice_template.domain;

import it.pagopa.domain.Identifiable;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateVersionRef;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public final class EServiceTemplateInterface implements Identifiable {
    private final UUID id;
    private final EServiceTemplateRef templateRef;
    private final EServiceTemplateVersionRef versionRef;
    private final String name;
    private final String contentType;
    private final byte[] content;

    public EServiceTemplateInterface(
            UUID id,
            EServiceTemplateRef templateRef,
            EServiceTemplateVersionRef versionRef,
            String name,
            String contentType,
            byte[] content
    ) {
        this.id = Objects.requireNonNull(id);
        this.templateRef = Objects.requireNonNull(templateRef);
        this.versionRef = Objects.requireNonNull(versionRef);
        this.name = Objects.requireNonNull(name);
        this.contentType = Objects.requireNonNull(contentType);
        this.content = Arrays.copyOf(Objects.requireNonNull(content), content.length);
    }

    @Override
    public UUID getId() {
        return id;
    }

    public EServiceTemplateRef getTemplateRef() {
        return templateRef;
    }

    public EServiceTemplateVersionRef getVersionRef() {
        return versionRef;
    }

    public String getName() {
        return name;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getContent() {
        return Arrays.copyOf(content, content.length);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof EServiceTemplateInterface that)) return false;
        return id.equals(that.id)
                && templateRef.equals(that.templateRef)
                && versionRef.equals(that.versionRef)
                && name.equals(that.name)
                && contentType.equals(that.contentType)
                && Arrays.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hash(id, templateRef, versionRef, name, contentType) + Arrays.hashCode(content);
    }
}
