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

    public EServiceTemplateVersion getVersion(EServiceTemplate template, EServiceTemplateVersion version) {
        return eServiceTemplateVersionGateway.getVersion(template.getId(), version.getId());
    }

    public EServiceTemplateVersion publishVersion(EServiceTemplate template, EServiceTemplateVersion version) {
        if (version.getState() == EServiceTemplateVersionState.PUBLISHED) return version;

        if (version.getState() != EServiceTemplateVersionState.DRAFT)
            throw new IllegalStateException("Cannot publish a version that is in state " + version.getState());

        return eServiceTemplateVersionGateway.publishVersion(template.getId(), version.getId());
    }

    public EServiceTemplateVersion updateVersion(EServiceTemplate template, EServiceTemplateVersion version, UpdateEServiceTemplateVersionCommand command) {
        return eServiceTemplateVersionGateway.updateVersion(template.getId(), version.getId(), command);
    }

    public EServiceTemplateVersion linkOpenApiInterface(EServiceTemplate template, EServiceTemplateVersion version, String openApiInterfacePath) {
        return eServiceTemplateVersionGateway.linkOpenApiInterface(template.getId(), version.getId(), openApiInterfacePath);
    }

    public EServiceTemplateVersion prepareVersionForPublication(EServiceTemplate template, EServiceTemplateVersion version, Consumer<UpdateEServiceTemplateVersionCommand> config) {
        UpdateEServiceTemplateVersionCommand command = requestFactory.defaultUpdateVersionCommand();
        config.accept(command);
        EServiceTemplateVersion updatedVersion = updateVersion(template, version, command);

        return linkOpenApiInterface(template, updatedVersion, "assets/origin-interface.yaml");
    }

    public EServiceTemplateVersion prepareVersionForPublication(EServiceTemplate template, EServiceTemplateVersion version) {
        return prepareVersionForPublication(template, version, command -> {
        });
    }
}

