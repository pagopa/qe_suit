package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.bff.eservice_template.application.*;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateGateway;
import it.pagopa.interop.common.eservice_template.application.command.*;
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
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BffEServiceTemplateGateway implements EServiceTemplateGateway {

    private final BffEServiceTemplateRestClient restClient;
    private final BffEServiceTemplateVersionGateway versionGateway;
    private final EntityStore entityStore;
    private final BffEServiceTemplateMapper mapper;

    @Override
    public EServiceTemplate createEServiceTemplate(EServiceTemplateCreationCommand command) {
        if (!(command instanceof BffEServiceTemplateCreationCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of " + BffEServiceTemplateCreationCommand.class.getSimpleName());

        var created = restClient.createEServiceTemplate(bffCommand.getBffCreationPayload())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();

        return refresh(new EServiceTemplateRef(created.getId()), new EServiceTemplateVersionRef(created.getVersionId()));
    }

    @Override
    public EServiceTemplate getEServiceTemplate(EServiceTemplateRef templateRef) {
        return restClient.getEServiceTemplate(templateRef.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(templateDetails -> {
                    Optional<EServiceTemplate> maybeTemplate = entityStore.getById(templateRef.id(), EServiceTemplate.class);
                    return mapper.toEServiceTemplatePreservingVersions(templateDetails, maybeTemplate.orElse(null));
                })
                .updateContext()
                .get();
    }

    @Override
    public EServiceTemplate getEServiceTemplateVersion(EServiceTemplate template, EServiceTemplateVersion version) {
        return refresh(template.getRef(), version.getRef());
    }

    @Override
    public EServiceTemplate updateEServiceTemplate(EServiceTemplateRef templateRef, UpdateEServiceTemplateCommand command) {
        if (!(command instanceof BffUpdateEServiceTemplateCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of " + BffUpdateEServiceTemplateCommand.class.getSimpleName());

        return restClient.updateEServiceTemplate(templateRef.id(), bffCommand.getBffPayload())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(ignored -> getEServiceTemplate(templateRef))
                .get();
    }

    @Override
    public EServiceTemplate updateTemplateName(EServiceTemplateRef templateRef, UpdateEServiceTemplateNameCommand command) {
        if (!(command instanceof BffUpdateEServiceTemplateNameCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of " + BffUpdateEServiceTemplateNameCommand.class.getSimpleName());

        return restClient.updateTemplateName(templateRef.id(), bffCommand.getBffPayload())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(ignored -> getEServiceTemplate(templateRef))
                .get();
    }

    @Override
    public EServiceTemplate updateTemplateIntendedTarget(
            EServiceTemplateRef templateRef,
            UpdateEServiceTemplateIntendedTargetCommand command
    ) {
        if (!(command instanceof BffUpdateEServiceTemplateIntendedTargetCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of " + BffUpdateEServiceTemplateIntendedTargetCommand.class.getSimpleName());

        return restClient.updateTemplateIntendedTarget(templateRef.id(), bffCommand.getBffPayload())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(ignored -> getEServiceTemplate(templateRef))
                .get();
    }

    @Override
    public EServiceTemplate updateTemplateDescription(
            EServiceTemplateRef templateRef,
            UpdateEServiceTemplateDescriptionCommand command
    ) {
        if (!(command instanceof BffUpdateEServiceTemplateDescriptionCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of " + BffUpdateEServiceTemplateDescriptionCommand.class.getSimpleName());

        return restClient.updateTemplateDescription(templateRef.id(), bffCommand.getBffPayload())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(ignored -> getEServiceTemplate(templateRef))
                .get();
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
                current -> current.findVersion(version.getId()).getState() == EServiceTemplateVersionState.PUBLISHED,
                "EService template version was not published"
        );
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
        EServiceTemplateVersion createdVersion = versionGateway.getEServiceTemplateVersion(templateRef, versionRef);
        EServiceTemplate savedTemplate = getEServiceTemplate(templateRef);
        savedTemplate.addVersion(createdVersion);

        return savedTemplate;
    }

    private static Optional<EServiceTemplateInterface> interfaceDocument(EServiceTemplate template, UUID versionId) {
        return Optional.ofNullable(template.findVersion(versionId).getInterfaceDocument());
    }

    private byte[] download(UUID templateId, UUID versionId, UUID documentId) {
        return restClient.getEServiceTemplateDocumentById(templateId, versionId, documentId)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();
    }

    @Override
    public boolean supports(@NonNull Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}
