package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateUseCase;
import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateCreationCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.journey.application.EServiceTemplateJourney;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EServiceTemplateJourneyImpl implements EServiceTemplateJourney<EServiceTemplateJourneyImpl> {
    private final EServiceTemplateUseCase useCase;
    private final EntityStore entityStore;

    @Override
    public EServiceTemplateJourneyImpl createEServiceTemplate(EServiceTemplateCreationCommand configuration, EServiceTemplateVersionState state) {
        var template = useCase.createEServiceTemplate(configuration);
        return processLifecycle(template, template.getVersion(1), state, configuration);
    }

    private EServiceTemplateJourneyImpl processLifecycle(EServiceTemplate template, EServiceTemplateVersion version, EServiceTemplateVersionState targetState, EServiceTemplateCreationCommand configuration) {
        entityStore.upsert(template);

        return switch (targetState) {
            case DRAFT -> this;

            case PUBLISHED -> publishPipeline(template, version, configuration);

            default -> throw new UnsupportedOperationException(
                    String.format("La transizione allo stato %s non è ancora supportata nel Journey.", targetState)
            );
        };
    }

    private EServiceTemplateJourneyImpl publishPipeline(EServiceTemplate template, EServiceTemplateVersion version, EServiceTemplateCreationCommand configuration) {
        template = useCase.uploadInterface(
                template, version, "assets/eservice-template/" + configuration.getInterfaceDocumentPath()
        );
        version = template.getVersion(Integer.parseInt(version.getVersion()));
        template = useCase.publishEServiceTemplateVersion(template, version);
        version = template.getVersion(Integer.parseInt(version.getVersion()));
        useCase.downloadInterface(template, version);
        return this;
    }
}
