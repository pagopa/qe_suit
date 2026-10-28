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
import it.pagopa.interop.common.eservice_template.domain.FileDownloadMultipart;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class M2MEServiceTemplateVersionGateway implements EServiceTemplateVersionGateway {

    private final M2MEServiceTemplateRestClient restClient;
    private final M2MEServiceTemplateVersionMapper mapper;
    private final EntityStore entityStore;

    @Override
    public EServiceTemplateVersion addVersion(EServiceTemplateRef templateRef) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EServiceTemplateVersion getEServiceTemplateVersion(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef) {
        var templateFromStore = entityStore.getById(templateRef.id(), EServiceTemplate.class).orElseThrow();
        return restClient.getEServiceTemplateVersion(templateRef.id(), versionRef.versionId())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(version -> {
                    var mapped = mapper.toTemplateVersion(version);
                    templateFromStore.addVersion(mapped);
                    return mapped;
                })
                .updateContext()
                .get();
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
        var fileDownloadMultipart = download(templateRef.id(), versionRef.versionId());
        var eServiceTemplateInterface = new EServiceTemplateInterface(
                fileDownloadMultipart.getId(),
                templateRef,
                versionRef,
                fileDownloadMultipart.getFilename(),
                fileDownloadMultipart.getContentType(),
                fileDownloadMultipart.getPrettyName(),
                // The checksum is not available in the response from
                // /eserviceTemplates/{templateId}/versions/{versionId}/interface
                null,
                fileDownloadMultipart.getFile().getBytes(StandardCharsets.UTF_8)
        );

        var templateVersion = getEServiceTemplateVersion(templateRef, versionRef);
        var enrichedVersion = templateVersion.toBuilder()
                .interfaceDocument(eServiceTemplateInterface)
                .build();

        EServiceTemplate enriched = upsertVersion(templateRef, enrichedVersion);
        entityStore.upsert(enriched);

        return eServiceTemplateInterface;
    }

    @Override
    public Optional<EServiceTemplateInterface> readCurrentInterface(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef) {
        return Optional.of(downloadInterface(templateRef, versionRef));
    }

    @Override
    public boolean supports(@NonNull Channel delimiter) {
        return delimiter == Channel.M2M_V3;
    }

    private FileDownloadMultipart download(UUID templateId, UUID versionId) {
        return restClient.downloadEServiceTemplateVersionInterface(templateId, versionId)
            .withPolling(PollingStrategy.UNTIL_SUCCESS)
            .get();
    }

    private EServiceTemplate upsertVersion(
        EServiceTemplateRef templateRef,
        EServiceTemplateVersion updatedVersion
    ) {
        EServiceTemplate template = this.entityStore.getById(templateRef.id(), EServiceTemplate.class)
            .orElseThrow(() -> new IllegalStateException("Template not found in entity store"));

        List<EServiceTemplateVersion> versions = new ArrayList<>(template.getVersions());
        for (int i = 0; i < versions.size(); i++) {
            if (versions.get(i).getId().equals(updatedVersion.getId())) {
                versions.set(i, updatedVersion);
                return template.toBuilder()
                        .clearVersions()
                        .versions(versions)
                        .build();
            }
        }
        versions.add(updatedVersion);
        return template.toBuilder()
            .clearVersions()
            .versions(versions)
            .build();
    }
}
