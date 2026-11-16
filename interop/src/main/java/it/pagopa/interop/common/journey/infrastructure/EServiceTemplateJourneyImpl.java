package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.attribute.domain.Attributes;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.application.EServiceRequestFactory;
import it.pagopa.interop.common.eservice.application.command.EServiceTemplateInstanceUpdateDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateInstanceUseCase;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateRequestFactory;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateUseCase;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateVersionUseCase;
import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateCreationCommand;
import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateVersionCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.journey.application.EServiceTemplateJourney;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
public class EServiceTemplateJourneyImpl implements EServiceTemplateJourney<EServiceTemplateJourneyImpl> {

    private final EServiceTemplateUseCase eServiceTemplateUseCase;
    private final EServiceTemplateVersionUseCase eServiceTemplateVersionUseCase;
    private final EServiceTemplateInstanceUseCase eServiceTemplateInstanceUseCase;
    private final EServiceDescriptorUseCase eServiceDescriptorUseCase;
    private final EServiceTemplateRequestFactory requestFactory;
    private final EServiceRequestFactory eServiceRequestFactory;
    private final EntityStore entityStore;

    @Override
    public EServiceTemplateJourneyImpl createEServiceTemplate(EServiceTemplateCreationCommand command, EServiceTemplateVersionState targetState) {
        EServiceTemplate draftTemplate = eServiceTemplateUseCase.createEServiceTemplate(command);
        return processLifecycle(draftTemplate, draftTemplate.getLastDraftVersion(), targetState, command);
    }

    @Override
    public EServiceTemplateJourneyImpl createEServiceTemplate(Consumer<EServiceTemplateCreationCommand> commandConfig, EServiceTemplateVersionState state) {
        EServiceTemplateCreationCommand command = requestFactory.defaultCreationEServiceTemplateCommand();
        commandConfig.accept(command);
        return createEServiceTemplate(command, state);
    }

    @Override
    public EServiceTemplateJourneyImpl createEServiceTemplate(EServiceTemplateVersionState targetState) {
        EServiceTemplate draftTemplate = eServiceTemplateUseCase.createEServiceTemplate(cmd -> {});
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
    public EServiceTemplateJourneyImpl updateTemplateVersionWithContextAttributes(
            Consumer<UpdateEServiceTemplateVersionCommand> commandConfig,
            EServiceTemplateVersionState state
    ) {
        EServiceTemplate eServiceTemplate = entityStore.getLastOrThrow(EServiceTemplate.class);
        EServiceTemplateVersion eServiceTemplateVersion = eServiceTemplate.getLatestVersion();
        if (eServiceTemplateVersion.getState() != EServiceTemplateVersionState.DRAFT) {
            throw new IllegalStateException("Cannot update draft version of EServiceTemplateVersion with state " + eServiceTemplateVersion.getState());
        }
        UpdateEServiceTemplateVersionCommand command = requestFactory.defaultUpdateTemplateVersionCommand();
        commandConfig.accept(command);
        command.attributes(this::prepareContextAttributes);
        EServiceTemplateVersion updatedVersion = eServiceTemplateVersionUseCase.updateDraftVersion(eServiceTemplate, eServiceTemplate.getLatestVersion(), command);
        return processLifecycle(eServiceTemplate, updatedVersion, state);
    }

    @Override
    public EServiceTemplateJourneyImpl publishVersion(EServiceTemplateVersionState state) {
        EServiceTemplate eServiceTemplate = entityStore.getLastOrThrow(EServiceTemplate.class);
        return publishVersion(eServiceTemplate, state);
    }

    @Override
    public EServiceTemplateJourneyImpl publishVersion(EServiceTemplate eServiceTemplate, EServiceTemplateVersionState state) {
        EServiceTemplateVersion eServiceTemplateVersion = eServiceTemplate.getLastDraftVersion();
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
                }, predicate
        );

        return this;
    }

    @Override
    public EServiceTemplateJourneyImpl createEServiceInstanceTemplate() {
        EServiceTemplate template = entityStore.getLastOrThrow(EServiceTemplate.class);
        EService eService = eServiceTemplateInstanceUseCase.createEServiceInstance(template);
        entityStore.upsert(eService);
        return this;
    }

    @Override
    public EServiceTemplateJourneyImpl updateEServiceInstanceDescriptorWithContextAttributes(
            Consumer<EServiceTemplateInstanceUpdateDescriptorCommand> commandConfig,
            int customThreshold,
            EServiceDescriptorState state
    ) {
        EService eServiceInstance = entityStore.getLastOrThrow(EService.class);
        EServiceTemplateInstanceUpdateDescriptorCommand command = eServiceRequestFactory.defaultUpdateDescriptorInstanceCommand(eServiceInstance.getLatestDescriptor().getState());
        commandConfig.accept(command);
        command.attributes(attributes -> prepareContextAttributes(attributes, customThreshold));
        EService updatedEService = eServiceTemplateInstanceUseCase.updateEServiceInstanceDescriptor(
                eServiceInstance.getRef(), eServiceInstance.getLatestDescriptor().getRef(), eServiceInstance.getLatestDescriptor().getState(), command
        );
        EServiceDescriptor updatedDescriptor = updatedEService.getLatestDescriptor();
        entityStore.upsert(updatedEService);
        switch (state) {
            case DRAFT -> {}
            case PUBLISHED -> {
                EServiceDescriptor publishedDescriptor = eServiceDescriptorUseCase.publishDescriptor(updatedEService, updatedDescriptor);
                updatedEService.upsertDescriptor(publishedDescriptor);
                entityStore.upsert(updatedEService);
            }
            default -> throw new UnsupportedOperationException(String.format("La transizione allo stato %s non è ancora supportata nel Journey.", state));
        }
        return this;
    }

    private EServiceTemplateJourneyImpl processLifecycle(EServiceTemplate template, EServiceTemplateVersion version, EServiceTemplateVersionState targetState, EServiceTemplateCreationCommand configuration) {
        return switch (targetState) {
            case DRAFT -> this;
            case PUBLISHED -> publishPipeline(template, version, configuration);
            default -> throw new UnsupportedOperationException(String.format("La transizione allo stato %s non è ancora supportata nel Journey.", targetState));
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

    private Attributes prepareContextAttributes(Attributes attributes) {
        return prepareContextAttributes(attributes, null);
    }

    private Attributes prepareContextAttributes(Attributes attributes, Integer dailyCallsPerConsumer) {
        List<Attribute> certified = new ArrayList<>(attributes.getCertified());
        List<Attribute> declared = new ArrayList<>(attributes.getDeclared());
        List<Attribute> verified = new ArrayList<>(attributes.getVerified());
        for (Attribute attribute : entityStore.getAll(Attribute.class)) {
            var attributeBuilder = attribute.toBuilder();
            if (dailyCallsPerConsumer != null) {
                attributeBuilder.dailyCallsPerConsumer(dailyCallsPerConsumer);
            }
            Attribute updatedAttribute = attributeBuilder.build();
            switch (updatedAttribute.getKind()) {
                case CERTIFIED, CERTIFIED_DISCRETE -> certified.add(updatedAttribute);
                case DECLARED -> declared.add(updatedAttribute);
                case VERIFIED -> verified.add(updatedAttribute);
            }
        }

        return attributes.toBuilder()
                .clearCertified().clearDeclared().clearVerified()
                .certified(certified).declared(declared).verified(verified)
                .build();
    }
}
