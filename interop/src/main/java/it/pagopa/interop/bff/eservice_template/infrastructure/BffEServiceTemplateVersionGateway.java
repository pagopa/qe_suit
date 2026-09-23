package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.bff.eservice_template.application.BffUpdateEServiceTemplateVersionCommand;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateVersionGateway;
import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateVersionCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateVersionRef;
import it.pagopa.utils.FileUtils;
import it.pagopa.utils.RandomUtils;
import it.pagopa.utils.async.DelayUtils;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BffEServiceTemplateVersionGateway implements EServiceTemplateVersionGateway {

    private final BffEServiceTemplateRestClient restClient;
    private final EntityStore entityStore;
    private final BffEServiceTemplateVersionMapper mapper;

    @Override
    public EServiceTemplateVersion getEServiceTemplateVersion(
            EServiceTemplateRef templateRef,
            EServiceTemplateVersionRef versionRef
    ) {
        return restClient.getVersion(templateRef.id(), versionRef.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(versionDetails -> {
                    Optional<EServiceTemplate> maybeTemplate = entityStore.getById(templateRef.id(), EServiceTemplate.class);
                    return mapper.toTemplateWithUpsertVersion(versionDetails, maybeTemplate.orElse(null));
                })
                .updateContext()
                .map(template -> {
                    EServiceTemplateVersion foundVersion = template.findVersion(versionRef.id());
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
    public EServiceTemplateVersion publishVersion(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef) {
        restClient.publishVersion(templateRef.id(), versionRef.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(ignored -> getEServiceTemplateVersion(templateRef, versionRef))
                .get();

        return PollingUtils.pollUntil(
                () -> getEServiceTemplateVersion(templateRef, versionRef),
                version -> version.getState() == EServiceTemplateVersionState.PUBLISHED
        );
    }

    @Override
    public EServiceTemplateVersion updateDraftVersion(
            EServiceTemplateRef templateRef,
            EServiceTemplateVersionRef versionRef,
            UpdateEServiceTemplateVersionCommand command
    ) {
        if (!(command instanceof BffUpdateEServiceTemplateVersionCommand bffCommand)) {
            throw new IllegalArgumentException("Command must be an instance of BffUpdateEServiceTemplateVersionCommand");
        }

        return restClient.updateDraftVersion(templateRef.id(), versionRef.id(), bffCommand.getBffPayload())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(ignored -> getEServiceTemplateVersion(templateRef, versionRef))
                .get();
    }

    @Override
    public EServiceTemplateVersion linkOpenApiInterface(
            EServiceTemplateRef templateRef,
            EServiceTemplateVersionRef versionRef,
            String openApiInterfacePath
    ) {
        File openapiFile = FileUtils.loadClasspathResourceAsTempFile(openApiInterfacePath);
        String documentName = RandomUtils.randomAlphanumericName("interface") + ".yaml";
        DelayUtils.waitForSeconds(1);

        return restClient.addDocument(
                        templateRef.id(),
                        versionRef.id(),
                        "INTERFACE",
                        documentName,
                        openapiFile
                )
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(ignored -> getEServiceTemplateVersion(templateRef, versionRef))
                .get();
    }

    @Override
    public boolean supports(@NonNull Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}

