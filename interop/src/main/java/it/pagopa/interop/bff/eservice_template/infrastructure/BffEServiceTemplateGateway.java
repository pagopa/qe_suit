package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.bff.eservice_template.application.BffEServiceTemplateCreationCommand;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateGateway;
import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateCreationCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.kernel.domain.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BffEServiceTemplateGateway implements EServiceTemplateGateway {

    private final BffEServiceTemplateRestClient restClient;
    private final BffEServiceTemplateVersionGateway versionGateway;
    private final BffEServiceTemplateMapper mapper;

    @Override
    public EServiceTemplate createEServiceTemplate(EServiceTemplateCreationCommand command) {
        if (!(command instanceof BffEServiceTemplateCreationCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of BffEServiceTemplateCreationCommand");

        return restClient.createEServiceTemplate(bffCommand.getBffCreationPayload())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(created -> {
                    EServiceTemplate template = getEServiceTemplate(created.getId());
                    EServiceTemplateVersion version = versionGateway.getVersion(created.getId(), created.getVersionId());

                    return template.toBuilder()
                            .clearVersions()
                            .version(version)
                            .build();
                })
                .updateContext()
                .get();
    }

    @Override
    public EServiceTemplate getEServiceTemplate(UUID eServiceTemplateId) {
        return restClient.readEServiceTemplate(eServiceTemplateId)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(mapper::toEServiceTemplateBase)
                .get();
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}

