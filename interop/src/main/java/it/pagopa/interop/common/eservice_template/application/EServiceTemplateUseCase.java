package it.pagopa.interop.common.eservice_template.application;

import it.pagopa.interop.common.eservice_template.application.command.*;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class EServiceTemplateUseCase {
    private final EServiceTemplateGateway eServiceTemplateGateway;
    private final EServiceTemplateRequestFactory requestFactory;

    public EServiceTemplate createEServiceTemplate(Consumer<EServiceTemplateCreationCommand> config) {
        EServiceTemplateCreationCommand creationCommand = requestFactory.defaultCreationEServiceTemplateCommand();
        config.accept(creationCommand);

        return createEServiceTemplate(creationCommand);
    }

    public EServiceTemplate createEServiceTemplate(EServiceTemplateCreationCommand creationCommand) {
        return eServiceTemplateGateway.createEServiceTemplate(creationCommand);
    }

    public EServiceTemplate getEServiceTemplate(EServiceTemplate eServiceTemplate) {
        return eServiceTemplateGateway.getEServiceTemplate(eServiceTemplate.getRef());
    }

    public EServiceTemplate updateEServiceTemplate(EServiceTemplate eServiceTemplate, Consumer<UpdateEServiceTemplateCommand> config) {
        UpdateEServiceTemplateCommand updateCommand = requestFactory.defaultUpdateEServiceTemplateCommand();
        config.accept(updateCommand);

        return updateEServiceTemplate(eServiceTemplate, updateCommand);
    }

    public EServiceTemplate updateEServiceTemplate(EServiceTemplate eServiceTemplate, UpdateEServiceTemplateCommand command) {
        return eServiceTemplateGateway.updateEServiceTemplate(eServiceTemplate.getRef(), command);
    }

    public EServiceTemplate updateTemplateName(EServiceTemplate eServiceTemplate, Consumer<UpdateEServiceTemplateNameCommand> config) {
        UpdateEServiceTemplateNameCommand command = requestFactory.defaultUpdateTemplateNameCommand();
        config.accept(command);

        return updateTemplateName(eServiceTemplate, command);
    }

    public EServiceTemplate updateTemplateName(EServiceTemplate eServiceTemplate, UpdateEServiceTemplateNameCommand command) {
        return eServiceTemplateGateway.updateTemplateName(eServiceTemplate.getRef(), command);
    }

    public EServiceTemplate updateTemplateIntendedTarget(EServiceTemplate eServiceTemplate, Consumer<UpdateEServiceTemplateIntendedTargetCommand> config) {
        UpdateEServiceTemplateIntendedTargetCommand command = requestFactory.defaultUpdateTemplateIntendedTargetCommand();
        config.accept(command);

        return updateTemplateIntendedTarget(eServiceTemplate, command);
    }

    public EServiceTemplate updateTemplateIntendedTarget(EServiceTemplate eServiceTemplate, UpdateEServiceTemplateIntendedTargetCommand command) {
        return eServiceTemplateGateway.updateTemplateIntendedTarget(eServiceTemplate.getRef(), command);
    }

    public EServiceTemplate updateTemplateDescription(EServiceTemplate eServiceTemplate, Consumer<UpdateEServiceTemplateDescriptionCommand> config) {
        UpdateEServiceTemplateDescriptionCommand command = requestFactory.defaultUpdateTemplateDescriptionCommand();
        config.accept(command);

        return updateTemplateDescription(eServiceTemplate, command);
    }

    public EServiceTemplate updateTemplateDescription(EServiceTemplate eServiceTemplate, UpdateEServiceTemplateDescriptionCommand command) {
        return eServiceTemplateGateway.updateTemplateDescription(eServiceTemplate.getRef(), command);
    }

    public EServiceTemplate uploadInterface(EServiceTemplate template, EServiceTemplateVersion version, String fixturePath) {
        return eServiceTemplateGateway.uploadInterface(template, version, fixturePath);
    }

    public EServiceTemplate publishEServiceTemplateVersion(EServiceTemplate template, EServiceTemplateVersion version) {
        return eServiceTemplateGateway.publishEServiceTemplateVersion(template, version);
    }

    public EServiceTemplate getEServiceTemplateVersion(EServiceTemplate template, EServiceTemplateVersion version) {
        return eServiceTemplateGateway.getEServiceTemplateVersion(template, version);
    }

    public EServiceTemplateInterface downloadInterface(EServiceTemplate template, EServiceTemplateVersion version) {
        return eServiceTemplateGateway.downloadInterface(template, version);
    }

    public void shouldHaveUnchangedInterface(EServiceTemplateVersion baseline) {
        EServiceTemplateInterface original = Objects.requireNonNull(
                baseline.getInterfaceDocument(), "La versione baseline non contiene un'interfaccia"
        );

        if (!original.hasDownloadedContent())
            throw new IllegalStateException("Il contenuto baseline dell'interfaccia non è stato scaricato");

        EServiceTemplateInterface current = gateway.readCurrentInterface(
                original.getTemplateRef(), original.getVersionRef()
        ).orElseThrow(() -> new AssertionError("L'interfaccia del template non è più presente"));

        if (!original.getId().equals(current.getId()))
            throw new AssertionError("L'interfaccia del template è stata sostituita");

        if (!original.getMetadata().equals(current.getMetadata())
                || !Objects.equals(original.getTemplateRef(), current.getTemplateRef())
                || !Objects.equals(original.getVersionRef(), current.getVersionRef())) {
            throw new AssertionError("I metadati dell'interfaccia del template sono cambiati");
        }

        if (!java.util.Arrays.equals(original.getContent(), current.getContent()))
            throw new AssertionError("I byte dell'interfaccia del template sono cambiati");
    }
}

