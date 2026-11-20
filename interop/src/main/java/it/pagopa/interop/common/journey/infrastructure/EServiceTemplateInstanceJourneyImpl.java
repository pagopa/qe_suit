package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateInstanceUseCase;
import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateInstanceCreationCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.journey.application.EServiceTemplateInstanceJourney;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EServiceTemplateInstanceJourneyImpl
        implements EServiceTemplateInstanceJourney<EServiceTemplateInstanceJourneyImpl> {
    private final EServiceTemplateInstanceUseCase instances;
    private final EntityStore entityStore;

    @Override
    public EServiceTemplateInstanceJourneyImpl createPublishedEServiceTemplateInstance(
            EServiceTemplate template, EServiceTemplateInstanceCreationCommand command, String alias
    ) {
        var instance = instances.createEServiceInstance(template, command);
        var storedInstance = entityStore.getById(instance.getId(), EService.class)
                .orElseThrow(() -> new IllegalStateException("Created template instance missing from context"));
        entityStore.upsert(alias, storedInstance);
        instances.publishEServiceInstance(template, storedInstance);
        return this;
    }
}
