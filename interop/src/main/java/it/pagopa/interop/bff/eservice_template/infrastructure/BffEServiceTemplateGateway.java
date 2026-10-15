package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.bff.eservice_template.application.*;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateGateway;
import it.pagopa.interop.common.eservice_template.application.command.*;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateVersionRef;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BffEServiceTemplateGateway implements EServiceTemplateGateway {

    private final BffEServiceTemplateRestClient restClient;
    private final BffEServiceTemplateVersionGateway versionGateway;
    private final EntityStore entityStore;
    private final BffEServiceTemplateAssembler assembler;

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
                    return assembler.toEServiceTemplatePreservingVersions(templateDetails, maybeTemplate.orElse(null));
                })
                .updateContext()
                .get();
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
    public EServiceTemplate updateTemplateIntendedTarget(EServiceTemplateRef templateRef, UpdateEServiceTemplateIntendedTargetCommand command) {
        if (!(command instanceof BffUpdateEServiceTemplateIntendedTargetCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of " + BffUpdateEServiceTemplateIntendedTargetCommand.class.getSimpleName());

        return restClient.updateTemplateIntendedTarget(templateRef.id(), bffCommand.getBffPayload())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(ignored -> getEServiceTemplate(templateRef))
                .get();
    }

    @Override
    public EServiceTemplate updateTemplateDescription(EServiceTemplateRef templateRef, UpdateEServiceTemplateDescriptionCommand command) {
        if (!(command instanceof BffUpdateEServiceTemplateDescriptionCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of " + BffUpdateEServiceTemplateDescriptionCommand.class.getSimpleName());

        return restClient.updateTemplateDescription(templateRef.id(), bffCommand.getBffPayload())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(ignored -> getEServiceTemplate(templateRef))
                .get();
    }

    private EServiceTemplate refresh(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef) {
        EServiceTemplateVersion createdVersion = versionGateway.getEServiceTemplateVersion(templateRef, versionRef);
        EServiceTemplate savedTemplate = getEServiceTemplate(templateRef);
        savedTemplate.addVersion(createdVersion);

        return savedTemplate;
    }

    @Override
    public boolean supports(@NonNull Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}
