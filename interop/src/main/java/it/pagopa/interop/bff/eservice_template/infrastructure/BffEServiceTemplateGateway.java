package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.bff.eservice_template.application.BffEServiceTemplateCreationCommand;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorGateway;
import it.pagopa.interop.common.eservice.application.EServiceGateway;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateGateway;
import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateCreationCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import it.pagopa.interop.generated.openapi.clients.bff.model.InstanceEServiceSeed;
import it.pagopa.utils.FileUtils;
import it.pagopa.utils.RandomUtils;
import it.pagopa.utils.async.DelayUtils;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BffEServiceTemplateGateway implements EServiceTemplateGateway {

    private final BffEServiceTemplateRestClient restClient;
    private final BffEServiceTemplateMapper mapper;
    private final EServiceGateway eServiceGateway;
    private final EServiceDescriptorGateway eServiceDescriptorGateway;

    @Override
    public EServiceTemplate createEServiceTemplate(EServiceTemplateCreationCommand command) {
        if (!(command instanceof BffEServiceTemplateCreationCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of BffEServiceTemplateCreationCommand");

        return restClient.createEServiceTemplate(bffCommand.getBffCreationPayload())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(created -> getEServiceTemplate(new EServiceTemplateRef(created.getId()), created.getVersionId()))
                .get();
    }

    @Override
    public EServiceTemplate getEServiceTemplate(EServiceTemplateRef templateRef, UUID versionId) {
        return restClient.readVersion(templateRef.templateId(), versionId)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(mapper::toEServiceTemplate)
                .updateContext()
                .get();
    }

    @Override
    public EServiceTemplate linkOpenApiInterface(EServiceTemplateRef templateRef, UUID versionId, String openApiInterfacePath) {
        File openapiFile = FileUtils.loadClasspathResourceAsTempFile(openApiInterfacePath);
        String documentName = RandomUtils.randomAlphanumericName("interface") + ".yaml";
        DelayUtils.waitForSeconds(1); // Avoid eventual consistency errors

        return restClient.addDocument(templateRef.templateId(), versionId, "INTERFACE", documentName, openapiFile)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(createdResource -> getEServiceTemplate(templateRef, versionId))
                .get();
    }

    @Override
    public EServiceTemplate publishVersion(EServiceTemplateRef templateRef, UUID versionId) {
        restClient.publishVersion(templateRef.templateId(), versionId)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();

        return PollingUtils.pollUntil(
                () -> getEServiceTemplate(templateRef, versionId),
                template -> template.findVersion(versionId).getState() == EServiceTemplateVersionState.PUBLISHED
        );
    }

    @Override
    public EService instantiateEService(EServiceTemplateRef templateRef) {
        InstanceEServiceSeed payload = new InstanceEServiceSeed()
                .isSignalHubEnabled(false)
                .isConsumerDelegable(false)
                .isClientAccessDelegable(false);

        return restClient.instantiateEService(templateRef.templateId(), payload)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(created -> {
                    EServiceRef eServiceRef = EServiceRef.of(created.getId());
                    EServiceDescriptor descriptor = eServiceDescriptorGateway
                            .getEServiceDescriptor(eServiceRef, EServiceDescriptorRef.of(created.getDescriptorId()));
                    EService eService = eServiceGateway.getEService(eServiceRef);
                    eService.addDescriptor(descriptor);
                    return eService;
                })
                .updateContext()
                .get();
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}

