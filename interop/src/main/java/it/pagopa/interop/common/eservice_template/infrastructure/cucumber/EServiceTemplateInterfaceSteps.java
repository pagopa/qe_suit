package it.pagopa.interop.common.eservice_template.infrastructure.cucumber;

import io.cucumber.java.en.And;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateVersionUseCase;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

@RequiredArgsConstructor
public class EServiceTemplateInterfaceSteps {
    private final EntityStore entityStore;
    private final EServiceTemplateVersionUseCase templateVersionUseCase;

    @And("l'erogatore recupera l'interfaccia {templateVersion}")
    public void retrieveTemplateVersionInterface(EServiceTemplateVersion version) {
        EServiceTemplate lastTemplate = entityStore.getLast(EServiceTemplate.class).isPresent() ?
                entityStore.getLast(EServiceTemplate.class).get() :
                null;
        Assertions.assertThat(lastTemplate).isNotNull();
        Assertions.assertThat(lastTemplate.getRef()).isNotNull();
        Assertions.assertThat(lastTemplate.findVersion(version.getRef().versionId())).isNotNull();
        templateVersionUseCase.downloadInterface(lastTemplate, version);
    }

    @And("l'interfaccia {templateVersion} è rimasta invariata")
    public void assertTemplateInterfaceUnchanged(EServiceTemplateVersion version) {
        templateVersionUseCase.shouldHaveUnchangedInterface(version);
    }
}
