package it.pagopa.interop.common.eservice_template.application;

import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateVersionCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class EServiceTemplateVersionUseCase {
    private final EServiceTemplateVersionGateway eServiceTemplateVersionGateway;
    private final EServiceTemplateRequestFactory requestFactory;

    public EServiceTemplateVersion getVersion(EServiceTemplate eServiceTemplate, EServiceTemplateVersion version) {
        return eServiceTemplateVersionGateway.getEServiceTemplateVersion(eServiceTemplate.getRef(), version.getRef());
    }

    public EServiceTemplateVersion addVersion(EServiceTemplate eServiceTemplate) {
        return eServiceTemplateVersionGateway.addVersion(eServiceTemplate.getRef());
    }

    public EServiceTemplateVersion publishVersion(EServiceTemplate eServiceTemplate, EServiceTemplateVersion version) {
        if (version.getState() == EServiceTemplateVersionState.PUBLISHED) {
            return version;
        }

        if (version.getState() != EServiceTemplateVersionState.DRAFT) {
            throw new IllegalStateException("Cannot publish a template version that is in state " + version.getState());
        }

        return eServiceTemplateVersionGateway.publishVersion(eServiceTemplate.getRef(), version.getRef());
    }

    public EServiceTemplateVersion updateDraftVersion(
            EServiceTemplate eServiceTemplate,
            EServiceTemplateVersion version,
            UpdateEServiceTemplateVersionCommand command
    ) {
        return eServiceTemplateVersionGateway.updateDraftVersion(eServiceTemplate.getRef(), version.getRef(), command);
    }

    public EServiceTemplateVersion linkOpenApiInterface(
            EServiceTemplate eServiceTemplate,
            EServiceTemplateVersion version,
            String openApiInterfacePath
    ) {
        return eServiceTemplateVersionGateway.linkOpenApiInterface(eServiceTemplate.getRef(), version.getRef(), openApiInterfacePath);
    }

    public EServiceTemplateVersion prepareVersionForPublication(
            EServiceTemplate eServiceTemplate,
            EServiceTemplateVersion version,
            Consumer<UpdateEServiceTemplateVersionCommand> config
    ) {
        UpdateEServiceTemplateVersionCommand command = requestFactory.defaultUpdateTemplateVersionCommand();
        config.accept(command);

        return updateDraftVersion(eServiceTemplate, version, command);
    }

    public EServiceTemplateVersion prepareVersionForPublication(
            EServiceTemplate eServiceTemplate,
            EServiceTemplateVersion version
    ) {
        UpdateEServiceTemplateVersionCommand command = requestFactory.defaultUpdateTemplateVersionCommand();
        EServiceTemplateVersion updatedVersion = updateDraftVersion(eServiceTemplate, version, command);

        return linkOpenApiInterface(eServiceTemplate, updatedVersion, "assets/origin-interface.yaml");
    }
}

