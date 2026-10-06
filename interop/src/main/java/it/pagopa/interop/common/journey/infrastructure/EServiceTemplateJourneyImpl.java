package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateUseCase;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.journey.application.EServiceTemplateJourney;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EServiceTemplateJourneyImpl implements EServiceTemplateJourney<EServiceTemplateJourneyImpl> {

    private final EServiceTemplateUseCase eServiceTemplateUseCase;
    private final EntityStore entityStore;

    @Override
    public EServiceTemplateJourneyImpl createEServiceTemplate(EServiceTemplateVersionState targetState) {
        EServiceTemplate draftTemplate = eServiceTemplateUseCase.createEServiceTemplate();
        entityStore.upsert(draftTemplate);

        return switch (targetState) {
            case DRAFT -> this;
            case PUBLISHED -> publishPipeline(draftTemplate);
            default -> throw new UnsupportedOperationException(
                    String.format("La transizione allo stato %s non è ancora supportata nel Journey.", targetState)
            );
        };
    }

    @Override
    public EServiceTemplateJourneyImpl instantiateEServiceFromTemplate() {
        EServiceTemplate template = entityStore.getLastOrThrow(EServiceTemplate.class);
        entityStore.upsert(eServiceTemplateUseCase.instantiateEService(template));
        return this;
    }

    private EServiceTemplateJourneyImpl publishPipeline(EServiceTemplate draftTemplate) {
        EServiceTemplate withInterface = eServiceTemplateUseCase.linkStandardInterface(draftTemplate);
        eServiceTemplateUseCase.publishLastVersion(withInterface);
        return this;
    }
}

