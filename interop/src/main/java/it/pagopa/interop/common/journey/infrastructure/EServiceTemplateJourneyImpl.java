package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateUseCase;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateVersionUseCase;
import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateCreationCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.journey.application.EServiceTemplateJourney;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EServiceTemplateJourneyImpl implements EServiceTemplateJourney<EServiceTemplateJourneyImpl> {

    private final EServiceTemplateUseCase eServiceTemplateUseCase;
    private final EServiceTemplateVersionUseCase eServiceTemplateVersionUseCase;
    private final EntityStore entityStore;

    @Override
    public EServiceTemplateJourneyImpl createEServiceTemplate(EServiceTemplateCreationCommand command, EServiceTemplateVersionState targetState) {
        EServiceTemplate draftTemplate = eServiceTemplateUseCase.createEServiceTemplate(command);
        return processLifecycle(draftTemplate, draftTemplate.getVersions().get(0), targetState);
    }

    @Override
    public EServiceTemplateJourneyImpl createEServiceTemplate(EServiceTemplateVersionState targetState) {
        EServiceTemplate draftTemplate = eServiceTemplateUseCase.createEServiceTemplate(cmd -> {
        });
        return processLifecycle(draftTemplate, draftTemplate.getVersions().get(0), targetState);
    }

    private EServiceTemplateJourneyImpl processLifecycle(EServiceTemplate template, EServiceTemplateVersion version, EServiceTemplateVersionState targetState) {
        entityStore.upsert(withVersion(template, version));

        return switch (targetState) {
            case DRAFT -> this;

            case PUBLISHED -> publishPipeline(template, version);

            default -> throw new UnsupportedOperationException(
                    String.format("La transizione allo stato %s non è ancora supportata nel Journey.", targetState)
            );
        };
    }

    private EServiceTemplateJourneyImpl publishPipeline(EServiceTemplate template, EServiceTemplateVersion version) {
        Optional<Attribute> declaredAttribute = entityStore.getLast(Attribute.class);

        EServiceTemplateVersion preparedVersion;
        if (declaredAttribute.isPresent()) {
            preparedVersion = eServiceTemplateVersionUseCase.prepareVersionForPublication(
                    template,
                    version,
                    command -> command.declaredAttribute(declaredAttribute.get())
            );
        } else {
            preparedVersion = eServiceTemplateVersionUseCase.prepareVersionForPublication(template, version);
        }

        EServiceTemplateVersion publishedVersion = eServiceTemplateVersionUseCase.publishVersion(template, preparedVersion);
        entityStore.upsert(withVersion(template, publishedVersion));
        return this;
    }

    private static EServiceTemplate withVersion(EServiceTemplate template, EServiceTemplateVersion version) {
        return template.toBuilder()
                .clearVersions()
                .version(version)
                .build();
    }
}

