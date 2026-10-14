package it.pagopa.interop.common.eservice_template.application;

import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateCreationCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateInterface;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EServiceTemplateUseCase {
    private final EServiceTemplateGateway gateway;
    private final EServiceTemplateRequestFactory requestFactory;

    public EServiceTemplate createEServiceTemplate(Consumer<EServiceTemplateCreationCommand> config) {
        EServiceTemplateCreationCommand command = requestFactory.defaultCreationEServiceTemplateCommand();
        config.accept(command);
        return createEServiceTemplate(command);
    }

    public EServiceTemplate createEServiceTemplate(EServiceTemplateCreationCommand command) {
        return gateway.createEServiceTemplate(command);
    }

    public EServiceTemplate uploadInterface(EServiceTemplate template, EServiceTemplateVersion version, String fixturePath) {
        return gateway.uploadInterface(template, version, fixturePath);
    }

    public EServiceTemplate publishEServiceTemplateVersion(EServiceTemplate template, EServiceTemplateVersion version) {
        return gateway.publishEServiceTemplateVersion(template, version);
    }

    public EServiceTemplate getEServiceTemplateVersion(EServiceTemplate template, EServiceTemplateVersion version) {
        return gateway.getEServiceTemplateVersion(template, version);
    }

    public EServiceTemplateInterface downloadInterface(EServiceTemplate template, EServiceTemplateVersion version) {
        return gateway.downloadInterface(template, version);
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
