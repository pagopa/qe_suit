package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateRequestFactory;
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
    private final EServiceTemplateRequestFactory requestFactory;
    private final EntityStore entityStore;

    @Override
    public EServiceTemplateJourneyImpl createEServiceTemplate(EServiceTemplateCreationCommand command, EServiceTemplateVersionState targetState) {
        EServiceTemplate draftTemplate = eServiceTemplateUseCase.createEServiceTemplate(command);
        return processLifecycle(draftTemplate, draftTemplate.getLastDraftVersion(), targetState, command);
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


    private EServiceTemplateJourneyImpl processLifecycle(EServiceTemplate template, EServiceTemplateVersion version, EServiceTemplateVersionState targetState, EServiceTemplateCreationCommand configuration) {
        return switch (targetState) {
            case DRAFT -> this;

            case PUBLISHED -> publishPipeline(template, version, configuration);

            default -> throw new UnsupportedOperationException(
                    String.format("La transizione allo stato %s non è ancora supportata nel Journey.", targetState)
            );
        };
    }

    private EServiceTemplateJourneyImpl processLifecycle(EServiceTemplate template, EServiceTemplateVersion version, EServiceTemplateVersionState targetState) {
        return processLifecycle(template, version, targetState, requestFactory.defaultCreationEServiceTemplateCommand());
    }

    private EServiceTemplateJourneyImpl publishPipeline(EServiceTemplate eServiceTemplate, EServiceTemplateVersion eServiceTemplateVersion, EServiceTemplateCreationCommand configuration) {
        var enrichedVersion = eServiceTemplateVersionUseCase.prepareVersionForPublishing(eServiceTemplate, eServiceTemplateVersion, configuration.getInterfaceDocumentPath());
        var publishedVersion = eServiceTemplateVersionUseCase.publishVersion(eServiceTemplate, enrichedVersion);
        eServiceTemplateVersionUseCase.downloadInterface(eServiceTemplate, publishedVersion);
        return this;
    }
}
