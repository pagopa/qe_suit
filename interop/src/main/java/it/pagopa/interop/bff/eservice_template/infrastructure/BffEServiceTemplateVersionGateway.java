package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.bff.eservice_template.application.BffUpdateEServiceTemplateVersionCommand;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateVersionGateway;
import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateVersionCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateVersionSeed;
import it.pagopa.utils.FileUtils;
import it.pagopa.utils.RandomUtils;
import it.pagopa.utils.async.DelayUtils;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.UUID;

import static it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState.PUBLISHED;

@Service
@RequiredArgsConstructor
public class BffEServiceTemplateVersionGateway implements EServiceTemplateVersionGateway {

    private final BffEServiceTemplateRestClient restClient;
    private final BffEServiceTemplateVersionMapper mapper;

    @Override
    public EServiceTemplateVersion getVersion(UUID eServiceTemplateId, UUID eServiceTemplateVersionId) {
        return restClient.readVersion(eServiceTemplateId, eServiceTemplateVersionId)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(mapper::toEServiceTemplateVersion)
                .get();
    }

    @Override
    public EServiceTemplateVersion publishVersion(UUID eServiceTemplateId, UUID eServiceTemplateVersionId) {
        restClient.publishVersion(eServiceTemplateId, eServiceTemplateVersionId)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();

        return PollingUtils.pollUntil(
                () -> getVersion(eServiceTemplateId, eServiceTemplateVersionId),
                version -> version.getState() == PUBLISHED
        );
    }

    @Override
    public EServiceTemplateVersion updateVersion(UUID eServiceTemplateId, UUID eServiceTemplateVersionId, UpdateEServiceTemplateVersionCommand command) {
        if (!(command instanceof BffUpdateEServiceTemplateVersionCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of BffUpdateEServiceTemplateVersionCommand");

        UpdateEServiceTemplateVersionSeed payload = bffCommand.getBffPayload();

        restClient.updateDraftVersion(eServiceTemplateId, eServiceTemplateVersionId, payload)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();

        return getVersion(eServiceTemplateId, eServiceTemplateVersionId);
    }

    @Override
    public EServiceTemplateVersion linkOpenApiInterface(UUID eServiceTemplateId, UUID eServiceTemplateVersionId, String openApiInterfacePath) {
        File openapiFile = FileUtils.loadClasspathResourceAsTempFile(openApiInterfacePath);
        String documentName = RandomUtils.randomAlphanumericName("interface") + ".yaml";
        DelayUtils.waitForSeconds(1); // Wait for a second to avoid potential eventual consistency error

        restClient.addDocument(
                        eServiceTemplateId,
                        eServiceTemplateVersionId,
                        "INTERFACE",
                        documentName,
                        openapiFile
                )
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();

        return getVersion(eServiceTemplateId, eServiceTemplateVersionId);
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}

