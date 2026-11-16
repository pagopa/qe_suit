package it.pagopa.interop.common.eservice_template.infrastructure.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.domain.SoapInterfaceUploadFailureReason;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateVersionUseCase;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

@RequiredArgsConstructor
public class EServiceTemplateInterfaceSteps {
    private final EntityStore entityStore;
    private final EServiceTemplateVersionUseCase templateVersionUseCase;

    @When("l'erogatore tenta di caricare l'interfaccia SOAP {string} sulla {templateVersion} ottenendo un rifiuto per \"{soapInterfaceUploadFailureReason}\"")
    public void rejectSoapInterfaceUpload(
            String fixture,
            EServiceTemplateVersion version,
            SoapInterfaceUploadFailureReason reason
    ) {
        EServiceTemplate template = entityStore.getLastOrThrow(EServiceTemplate.class);
        templateVersionUseCase.shouldFailToUploadInterface(
                template, version, "assets/wsdl/" + fixture, reason
        );
    }

    @Then("non è disponibile un'interfaccia valida per la {templateVersion}")
    public void assertNoAcceptedInterface(EServiceTemplateVersion version) {
        EServiceTemplate template = entityStore.getLastOrThrow(EServiceTemplate.class);
        EServiceTemplateVersion refreshed = templateVersionUseCase.getVersion(template, version);
        Assertions.assertThat(refreshed.getInterfaceDocument()).isNull();
    }

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
