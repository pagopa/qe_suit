package it.pagopa.interop.common.journey.application;

import it.pagopa.interop.common.eservice.application.command.EServiceTemplateInstanceUpdateDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateCreationCommand;
import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateVersionCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;

import java.util.function.Consumer;
import java.util.function.Predicate;

public interface EServiceTemplateJourney<SELF extends EServiceTemplateJourney<SELF>> extends JourneyModule {
    SELF createEServiceTemplate(EServiceTemplateCreationCommand command, EServiceTemplateVersionState state);

    default SELF createEServiceTemplate(EServiceTemplateCreationCommand command) {
        return createEServiceTemplate(command, EServiceTemplateVersionState.DRAFT);
    }

    SELF createEServiceTemplate(Consumer<EServiceTemplateCreationCommand> commandConfig, EServiceTemplateVersionState state);

    SELF createEServiceTemplate(EServiceTemplateVersionState state);

    SELF addVersion(EServiceTemplateVersionState state);

    SELF addVersion(EServiceTemplate eServiceTemplate, EServiceTemplateVersionState state);

    SELF updateTemplateVersionWithContextAttributes(
            Consumer<UpdateEServiceTemplateVersionCommand> commandConfig,
            EServiceTemplateVersionState state
    );

    SELF publishVersion(EServiceTemplateVersionState state);

    SELF publishVersion(EServiceTemplate eServiceTemplate, EServiceTemplateVersionState state);

    SELF waitUntilEServiceTemplate(Predicate<EServiceTemplate> predicate);

    SELF createEServiceInstanceTemplate();

    SELF updateEServiceInstanceDescriptorWithContextAttributes(
            Consumer<EServiceTemplateInstanceUpdateDescriptorCommand> commandConfig,
            int customThreshold,
            EServiceDescriptorState state
    );

    default SELF createEServiceTemplate() {
        return createEServiceTemplate(EServiceTemplateVersionState.DRAFT);
    }
}
