package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.bff.eservice_template.application.BffUpdateEServiceTemplateVersionCommand;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateVersionGateway;
import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateVersionCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateInterface;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.DocumentKind;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateVersionRef;
import it.pagopa.utils.FileUtils;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BffEServiceTemplateVersionGateway implements EServiceTemplateVersionGateway {

    private final BffEServiceTemplateRestClient restClient;
    private final EntityStore entityStore;
    private final BffEServiceTemplateVersionMapper mapper;

    @Override
    public EServiceTemplateVersion getEServiceTemplateVersion(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef) {
        return restClient.getEServiceTemplateVersion(templateRef.id(), versionRef.versionId())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(versionDetails -> {
                    Optional<EServiceTemplate> maybeTemplate = entityStore.getById(templateRef.id(), EServiceTemplate.class);
                    return mapper.toTemplateWithUpsertVersion(versionDetails, maybeTemplate.orElse(null));
                })
                .updateContext()
                .map(template -> {
                    EServiceTemplateVersion foundVersion = template.findVersion(versionRef.versionId());
                    if (foundVersion == null) {
                        throw new IllegalStateException("Template version not found after upsert");
                    }
                    return foundVersion;
                })
                .get();
    }

    @Override
    public EServiceTemplateVersion addVersion(EServiceTemplateRef templateRef) {
        return restClient.createVersion(templateRef.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(createdResource -> getEServiceTemplateVersion(templateRef, EServiceTemplateVersionRef.of(createdResource.getId())))
                .get();
    }

    @Override
    public EServiceTemplateVersion publishEServiceTemplateVersion(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef) {
        restClient.publishEServiceTemplateVersion(templateRef.id(), versionRef.versionId())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(ignored -> getEServiceTemplateVersion(templateRef, versionRef))
                .get();

        return PollingUtils.pollUntil(
                () -> getEServiceTemplateVersion(templateRef, versionRef),
                version -> version.getState() == EServiceTemplateVersionState.PUBLISHED
        );
    }

    @Override
    public EServiceTemplateVersion uploadInterface(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef, String fixturePath) {
        File document = FileUtils.loadClasspathResourceAsTempFile(fixturePath);
        String prettyName = Path.of(fixturePath).getFileName().toString();

        var created = restClient.createEServiceTemplateDocument(
                        templateRef.id(), versionRef.versionId(), DocumentKind.INTERFACE.name(), prettyName, document
                )
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();

        return PollingUtils.pollUntil(
                () -> getEServiceTemplateVersion(templateRef, versionRef),
                current -> Optional.ofNullable(current.getInterfaceDocument())
                        .map(metadata -> metadata.getId().equals(created.getId()))
                        .orElse(false),
                "EService template interface was not visible after upload"
        );
    }

    @Override
    public EServiceTemplateVersion updateDraftVersion(
            EServiceTemplateRef templateRef,
            EServiceTemplateVersionRef versionRef,
            UpdateEServiceTemplateVersionCommand command
    ) {
        if (!(command instanceof BffUpdateEServiceTemplateVersionCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of " + BffUpdateEServiceTemplateVersionCommand.class.getSimpleName());

        return restClient.updateDraftVersion(templateRef.id(), versionRef.versionId(), bffCommand.getBffPayload())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(ignored -> getEServiceTemplateVersion(templateRef, versionRef))
                .get();
    }

    @Override
    public EServiceTemplateInterface downloadInterface(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef) {
        EServiceTemplateVersion templateVersion = getEServiceTemplateVersion(templateRef, versionRef);
        EServiceTemplateInterface metadata = Optional.ofNullable(templateVersion.getInterfaceDocument())
                .orElseThrow(() -> new IllegalStateException("La versione del template non ha un'interfaccia"));

        byte[] content = download(templateRef.id(), versionRef.versionId(), metadata.getId());

        EServiceTemplateInterface downloaded = metadata.withContent(content);
        EServiceTemplateVersion enrichedVersion = templateVersion.toBuilder()
                .interfaceDocument(downloaded)
                .build();
        EServiceTemplate enriched = upsertVersion(templateRef, enrichedVersion);
        entityStore.upsert(enriched);

        return downloaded;
    }

    @Override
    public Optional<EServiceTemplateInterface> readCurrentInterface(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef) {
        EServiceTemplateVersion templateVersion = getEServiceTemplateVersion(templateRef, versionRef);
        EServiceTemplateInterface metadata = Optional.ofNullable(templateVersion.getInterfaceDocument())
                .orElseThrow(() -> new IllegalStateException("La versione del template non ha un'interfaccia"));

        return Optional.of(metadata.withContent(
                download(templateRef.id(), versionRef.versionId(), metadata.getId())
        ));
    }

    private byte[] download(UUID templateId, UUID versionId, UUID documentId) {
        return restClient.getEServiceTemplateDocumentById(templateId, versionId, documentId)
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

    @Override
    public boolean supports(@NonNull Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}

