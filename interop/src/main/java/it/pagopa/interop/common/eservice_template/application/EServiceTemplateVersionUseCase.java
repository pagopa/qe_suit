package it.pagopa.interop.common.eservice_template.application;

import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateVersionCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateInterface;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.function.Consumer;

import static it.pagopa.interop.common.eservice.domain.EServiceTechnology.REST;

@Service
@RequiredArgsConstructor
public class EServiceTemplateVersionUseCase {
    private final static String STANDARD_WSDL_PATH = "assets/eservice-template/soap11-due-indirizzi.wsdl";
    private final static String STANDARD_OPEN_API_PATH = "assets/origin-interface.yaml";

    private final EServiceTemplateVersionGateway eServiceTemplateVersionGateway;
    private final EServiceTemplateRequestFactory requestFactory;

    public void retireVersions(EServiceTemplate template) {
        for (var version : template.getVersions()) {
            switch (version.getState()) {
                case DRAFT -> eServiceTemplateVersionGateway.deleteDraftVersion(template.getRef(), version.getRef());
                case PUBLISHED -> eServiceTemplateVersionGateway.suspendVersion(template.getRef(), version.getRef());
                case SUSPENDED, ARCHIVED, DEPRECATED -> { }
                default -> throw new IllegalStateException("Cannot retire template version in state " + version.getState());
            }
        }
    }

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

        return eServiceTemplateVersionGateway.publishEServiceTemplateVersion(eServiceTemplate.getRef(), version.getRef());
    }

    public EServiceTemplateVersion prepareVersionForPublishing(EServiceTemplate eServiceTemplate, EServiceTemplateVersion version) {
        return prepareVersionForPublishing(eServiceTemplate, version, null);
    }

    public EServiceTemplateVersion prepareVersionForPublishing(EServiceTemplate eServiceTemplate, EServiceTemplateVersion version, String interfacePath) {
        if (version.getState() == EServiceTemplateVersionState.PUBLISHED) return version;

        if (version.getState() != EServiceTemplateVersionState.DRAFT)
            throw new IllegalStateException("Cannot prepare a template version for publishing that is in state " + version.getState());

        if (version.getInterfaceDocument() == null)
            return uploadInterface(
                    eServiceTemplate, version,
                    interfacePath == null
                            ? (eServiceTemplate.getTechnology() == REST ? STANDARD_OPEN_API_PATH : STANDARD_WSDL_PATH)
                            : interfacePath
            );

        return version;
    }

    public EServiceTemplateVersion updateDraftVersion(EServiceTemplate eServiceTemplate, EServiceTemplateVersion version, UpdateEServiceTemplateVersionCommand command) {
        return eServiceTemplateVersionGateway.updateDraftVersion(eServiceTemplate.getRef(), version.getRef(), command);
    }

    public EServiceTemplateVersion updateDraftVersion(EServiceTemplate eServiceTemplate, EServiceTemplateVersion version, Consumer<UpdateEServiceTemplateVersionCommand> config) {
        UpdateEServiceTemplateVersionCommand command = requestFactory.defaultUpdateTemplateVersionCommand();
        config.accept(command);

        return updateDraftVersion(eServiceTemplate, version, command);
    }

    public EServiceTemplateVersion uploadInterface(EServiceTemplate template, EServiceTemplateVersion version, String fixturePath) {
        eServiceTemplateVersionGateway.uploadInterface(template.getRef(), version.getRef(), fixturePath);
        return eServiceTemplateVersionGateway.getEServiceTemplateVersion(template.getRef(), version.getRef());
    }

    public EServiceTemplateVersion publishEServiceTemplateVersion(EServiceTemplate template, EServiceTemplateVersion version) {
        eServiceTemplateVersionGateway.publishEServiceTemplateVersion(template.getRef(), version.getRef());
        return eServiceTemplateVersionGateway.getEServiceTemplateVersion(template.getRef(), version.getRef());
    }

    public EServiceTemplateVersion getEServiceTemplateVersion(EServiceTemplate template, EServiceTemplateVersion version) {
        eServiceTemplateVersionGateway.getEServiceTemplateVersion(template.getRef(), version.getRef());
        return eServiceTemplateVersionGateway.getEServiceTemplateVersion(template.getRef(), version.getRef());
    }

    public EServiceTemplateInterface downloadInterface(EServiceTemplate template, EServiceTemplateVersion version) {
        return eServiceTemplateVersionGateway.downloadInterface(template.getRef(), version.getRef());
    }

    public void shouldHaveUnchangedInterface(EServiceTemplateVersion baseline) {
        EServiceTemplateInterface original = Objects.requireNonNull(
                baseline.getInterfaceDocument(), "La versione baseline non contiene un'interfaccia"
        );

        if (!original.hasDownloadedContent())
            throw new IllegalStateException("Il contenuto baseline dell'interfaccia non è stato scaricato");

        EServiceTemplateInterface current = eServiceTemplateVersionGateway.readCurrentInterface(
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
