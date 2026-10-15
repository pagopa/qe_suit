package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.bff.eservice_template.application.BffEServiceTemplateCreationCommand;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateGateway;
import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateCreationCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateInterface;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.DocumentKind;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateVersionRef;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import it.pagopa.utils.FileUtils;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BffEServiceTemplateGateway implements EServiceTemplateGateway {
    private final BffEServiceTemplateRestClient restClient;
    private final BffEServiceTemplateMapper mapper;
    private final EntityStore entityStore;

    @Override
    public EServiceTemplate createEServiceTemplate(EServiceTemplateCreationCommand command) {
        if (!(command instanceof BffEServiceTemplateCreationCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of BffEServiceTemplateCreationCommand");

        var created = restClient.createEServiceTemplate(bffCommand.getBffCreationPayload())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();

        return refresh(new EServiceTemplateRef(created.getId()), new EServiceTemplateVersionRef(created.getVersionId()));
    }

    @Override
    public EServiceTemplate uploadInterface(EServiceTemplate template, EServiceTemplateVersion version, String fixturePath) {
        File document = FileUtils.loadClasspathResourceAsTempFile(fixturePath);
        String prettyName = Path.of(fixturePath).getFileName().toString();

        var created = restClient.createEServiceTemplateDocument(
                        template.getId(), version.getId(), DocumentKind.INTERFACE.name(), prettyName, document
                )
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();

        return PollingUtils.pollUntil(
                () -> refresh(template.getRef(), new EServiceTemplateVersionRef(version.getId())),
                current -> interfaceDocument(current, version.getId())
                        .map(metadata -> metadata.getId().equals(created.getId()))
                        .orElse(false),
                "EService template interface was not visible after upload"
        );
    }

    @Override
    public EServiceTemplate publishEServiceTemplateVersion(EServiceTemplate template, EServiceTemplateVersion version) {
        restClient.publishEServiceTemplateVersion(template.getId(), version.getId())
                .withPolling(PollingStrategy.UNTIL_SUCCESS);

        return PollingUtils.pollUntil(
                () -> refresh(template.getRef(), new EServiceTemplateVersionRef(version.getId())),
                current -> selectedVersion(current, version.getId()).getState() == EServiceTemplateVersionState.PUBLISHED,
                "EService template version was not published"
        );
    }

    @Override
    public EServiceTemplate getEServiceTemplateVersion(EServiceTemplate template, EServiceTemplateVersion version) {
        return refresh(template.getRef(), new EServiceTemplateVersionRef(version.getId()));
    }

    @Override
    public EServiceTemplateInterface downloadInterface(EServiceTemplate template, EServiceTemplateVersion version) {
        EServiceTemplate current = refresh(template.getRef(), new EServiceTemplateVersionRef(version.getId()));
        EServiceTemplateInterface metadata = interfaceDocument(current, version.getId())
                .orElseThrow(() -> new IllegalStateException("La versione del template non ha un'interfaccia"));

        byte[] content = download(template.getId(), version.getId(), metadata.getId());

        EServiceTemplateInterface downloaded = metadata.withContent(content);
        EServiceTemplate enriched = replaceInterface(current, version.getId(), downloaded);
        entityStore.upsert(enriched);

        return downloaded;
    }

    @Override
    public Optional<EServiceTemplateInterface> readCurrentInterface(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef) {
        EServiceTemplateVersionDetails details = getDetails(templateRef.templateId(), versionRef.versionId());
        if (details.getInterface() == null) return Optional.empty();

        EServiceTemplate metadataOnly = mapper.toEServiceTemplate(details);
        EServiceTemplateInterface metadata = interfaceDocument(metadataOnly, versionRef.versionId())
                .orElseThrow(() -> new IllegalStateException("Metadati interfaccia non mappati"));

        return Optional.of(metadata.withContent(
                download(templateRef.templateId(), versionRef.versionId(), metadata.getId())
        ));
    }

    private EServiceTemplate refresh(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef) {
        EServiceTemplate existing = entityStore.getById(templateRef.templateId(), EServiceTemplate.class).orElse(null);
        EServiceTemplate mapped = mapper.toEServiceTemplate(
                getDetails(templateRef.templateId(), versionRef.versionId()), existing
        );
        entityStore.upsert(mapped);
        return mapped;
    }

    private EServiceTemplateVersionDetails getDetails(UUID templateId, UUID versionId) {
        return restClient.getEServiceTemplateVersion(templateId, versionId)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();
    }

    private byte[] download(UUID templateId, UUID versionId, UUID documentId) {
        return restClient.getEServiceTemplateDocumentById(templateId, versionId, documentId)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();
    }

    private static EServiceTemplateVersion selectedVersion(EServiceTemplate template, UUID versionId) {
        return template.getVersions().stream()
                .filter(version -> version.getId().equals(versionId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Versione template non presente: " + versionId));
    }

    private static Optional<EServiceTemplateInterface> interfaceDocument(EServiceTemplate template, UUID versionId) {
        return Optional.ofNullable(selectedVersion(template, versionId).getInterfaceDocument());
    }

    private static EServiceTemplate replaceInterface(EServiceTemplate template, UUID versionId, EServiceTemplateInterface document) {
        List<EServiceTemplateVersion> versions = template.getVersions().stream()
                .map(version -> version.getId().equals(versionId)
                        ? version.toBuilder().interfaceDocument(document).build()
                        : version)
                .toList();
        return template.toBuilder().versions(versions).build();
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.BFF;
    }
}
