package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateUseCase;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateVersionUseCase;
import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateCreationCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.journey.application.EServiceTemplateJourney;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
public class EServiceTemplateJourneyImpl implements EServiceTemplateJourney<EServiceTemplateJourneyImpl> {

    private final EServiceTemplateUseCase eServiceTemplateUseCase;
    private final EServiceTemplateVersionUseCase eServiceTemplateVersionUseCase;
    private final EntityStore entityStore;

    @Override
    public EServiceTemplateJourneyImpl createEServiceTemplate(
            EServiceTemplateCreationCommand command,
            EServiceTemplateVersionState targetState
    ) {
        EServiceTemplate draftTemplate = eServiceTemplateUseCase.createEServiceTemplate(command);
        return processLifecycle(draftTemplate, draftTemplate.getLastDraftVersion(), targetState);
    }

    @Override
    public EServiceTemplateJourneyImpl createEServiceTemplate(EServiceTemplateVersionState targetState) {
        EServiceTemplate draftTemplate = eServiceTemplateUseCase.createEServiceTemplate(cmd -> {
        });
        return processLifecycle(draftTemplate, draftTemplate.getLastDraftVersion(), targetState);
    }

    @Override
    public EServiceTemplateJourneyImpl addVersion(EServiceTemplateVersionState state) {
        EServiceTemplate eServiceTemplate = entityStore.getLastOrThrow(EServiceTemplate.class);
        EServiceTemplateVersion eServiceTemplateVersion = eServiceTemplateVersionUseCase.addVersion(eServiceTemplate);
        return processLifecycle(eServiceTemplate, eServiceTemplateVersion, state);
    }

    @Override
    public EServiceTemplateJourneyImpl addVersion(EServiceTemplate eServiceTemplate, EServiceTemplateVersionState state) {
        EServiceTemplateVersion eServiceTemplateVersion = eServiceTemplateVersionUseCase.addVersion(eServiceTemplate);
        return processLifecycle(eServiceTemplate, eServiceTemplateVersion, state);
    }

    @Override
    public EServiceTemplateJourneyImpl waitUntilEServiceTemplate(Predicate<EServiceTemplate> predicate) {
        EServiceTemplate eServiceTemplate = entityStore.getLastOrThrow(EServiceTemplate.class);

        PollingUtils.pollUntil(
                () -> {
                    EServiceTemplate refreshedTemplate = eServiceTemplateUseCase.getEServiceTemplate(eServiceTemplate);
                    refreshedTemplate.getVersions().forEach(
                            version -> eServiceTemplateVersionUseCase.getVersion(refreshedTemplate, version)
                    );
                    return eServiceTemplateUseCase.getEServiceTemplate(refreshedTemplate);
                },
                predicate
        );

        return this;
    }

    private EServiceTemplateJourneyImpl processLifecycle(
            EServiceTemplate eServiceTemplate,
            EServiceTemplateVersion eServiceTemplateVersion,
            EServiceTemplateVersionState targetState
    ) {
        entityStore.upsert(eServiceTemplate);

        return switch (targetState) {
            case DRAFT -> this;
            case PUBLISHED -> publishPipeline(eServiceTemplate, eServiceTemplateVersion);
            default -> throw new UnsupportedOperationException(
                    String.format("Transition to state %s is not yet supported in Template Journey.", targetState)
            );
        };
    }

    private EServiceTemplateJourneyImpl publishPipeline(
            EServiceTemplate eServiceTemplate,
            EServiceTemplateVersion eServiceTemplateVersion
    ) {
        eServiceTemplateVersionUseCase.prepareVersionForPublication(eServiceTemplate, eServiceTemplateVersion);
        eServiceTemplateVersionUseCase.publishVersion(eServiceTemplate, eServiceTemplateVersion);
        return this;
    }
}

