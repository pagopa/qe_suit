package it.pagopa.interop.bff.eservice_template.infrastructure;

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
import it.pagopa.utils.FileUtils;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BffEServiceTemplateGateway implements EServiceTemplateGateway {
    private final BffEServiceTemplateRestClient restClient;
    private final BffEServiceTemplateMapper mapper;

    @Override
    public EServiceTemplate createEServiceTemplate(EServiceTemplateCreationCommand command) {
        if (!(command instanceof BffEServiceTemplateCreationCommand bffCommand)) {
            throw new IllegalArgumentException("Command must be an instance of BffEServiceTemplateCreationCommand");
        }

        var created = restClient.createEServiceTemplate(bffCommand.getBffCreationPayload())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();

        return restClient.getEServiceTemplateVersion(created.getId(), created.getVersionId())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(mapper::toEServiceTemplate)
                .updateContext()
                .get();
    }

    @Override
    public EServiceTemplate uploadInterface(EServiceTemplate template, String fixturePath) {
        EServiceTemplateVersion version = template.getVersions().get(0);
        log.info("PIN-10024 template interface upload: fixture={}, templateId={}, versionId={}",
                fixturePath, template.getId(), version.getId());
        File document = FileUtils.loadClasspathResourceAsTempFile(fixturePath);
        String prettyName = Path.of(fixturePath).getFileName().toString();
        var createdDocument = restClient.createEServiceTemplateDocument(
                        template.getId(),
                        version.getId(),
                        DocumentKind.INTERFACE.name(),
                        prettyName,
                        document
                )
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();

        EServiceTemplate refreshed = PollingUtils.pollUntil(
                () -> getVersion(template),
                t -> t.getVersions().get(0).getInterfaceDocument() != null,
                "Failed to refresh template version after interface upload"
        );

        Assertions.assertThat(refreshed.getVersions().get(0).getInterfaceDocument().id())
                .isEqualTo(createdDocument.getId());
        return refreshed;
    }

    @Override
    public EServiceTemplate publishEServiceTemplateVersion(EServiceTemplate template) {
        EServiceTemplateVersion version = template.getVersions().get(0);
        restClient.publishEServiceTemplateVersion(template.getId(), version.getId())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();

        return PollingUtils.pollUntil(
                () -> getVersion(template),
                state -> state.getVersions().get(0).getState() == EServiceTemplateVersionState.PUBLISHED,
                "Failed to refresh template version after publishing"
        );
    }

    @Override
    public EServiceTemplate getEServiceTemplateVersion(EServiceTemplate template) {
        return getVersion(template);
    }

    @Override
    public EServiceTemplateInterface downloadInterface(EServiceTemplate template) {
        EServiceTemplateVersion version = template.getVersions().get(0);
        UUID documentId = version.getInterfaceDocument().id();
        var details = restClient.getEServiceTemplateVersion(template.getId(), version.getId())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();
        var metadata = details.getInterface();

        return restClient.getEServiceTemplateDocumentById(template.getId(), version.getId(), documentId)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(bytes -> new EServiceTemplateInterface(
                        documentId,
                        new EServiceTemplateRef(template.getId()),
                        new EServiceTemplateVersionRef(template.getId()),
                        metadata.getName(),
                        metadata.getContentType(),
                        bytes
                ))
                .updateContext()
                .get();
    }

    @Override
    public byte[] downloadInterfaceBytes(EServiceTemplate template, UUID documentId) {
        EServiceTemplateVersion version = template.getVersions().get(0);
        return restClient.getEServiceTemplateDocumentById(template.getId(), version.getId(), documentId)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();
    }

    private EServiceTemplate getVersion(EServiceTemplate template) {
        return restClient.getEServiceTemplateVersion(template.getId(), template.getVersions().get(0).getId())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(mapper::toEServiceTemplate)
                .updateContext()
                .get();
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.BFF;
    }
}
