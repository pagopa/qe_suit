package it.pagopa.interop.common.eservice_template.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.DataTableType;
import io.cucumber.datatable.DataTable;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateRequestFactory;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateVersionUseCase;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateUseCase;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplatePreparationConfiguration;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.eservice.domain.RestInterfaceConfiguration;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.journey.application.InteropJourney;
import lombok.RequiredArgsConstructor;
import it.pagopa.interop.common.eservice_template.infrastructure.cucumber.dto.mapper.EServiceTemplateConfigurationMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
public class EServiceTemplateSteps {
    private final InteropJourney journey;
    private final EServiceTemplateRequestFactory requestFactory;
    private final ObjectMapper objectMapper;
    private final EServiceTemplateConfigurationMapper configurationMapper;
    private final EntityStore entities;
    private final EServiceTemplateVersionUseCase templateVersionUseCase;
    private final EServiceTemplateUseCase templateUseCase;

    @Given("la bozza di {eServiceTemplate} creata viene eliminata")
    public void deleteDraftVersion(EServiceTemplate template) {
        templateVersionUseCase.deleteDraftVersion(template);
    }

    @When("l'erogatore allega un file di documentazione all'{eServiceTemplate}")
    public void requestDocumentationUpload(EServiceTemplate template) {
        templateVersionUseCase.requestDocumentationUpload(template);
    }

    @Given("un template e-service in ricezione con una versione in bozza")
    public void createReceiveDraftTemplate() {
        journey.createEServiceTemplate(command -> command.mode(EServiceMode.RECEIVE),
                EServiceTemplateVersionState.DRAFT);
    }

    @Given("un template e-service SOAP in bozza creato dall'erogatore")
    public void createDraftSoapEServiceTemplate() {
        journey.createEServiceTemplate(
                cmd -> cmd.technology(EServiceTechnology.SOAP),
                EServiceTemplateVersionState.DRAFT
        );
    }

    @Given("un template e-service preparato con la seguente configurazione:")
    public void prepareTemplate(EServiceTemplatePreparationConfiguration configuration) {
        var command = requestFactory.defaultCreationEServiceTemplateCommand();
        configurationMapper.apply(configuration, command);
        journey.createEServiceTemplate(command, configuration.getState());
        if (configuration.getAlias() != null && !configuration.getAlias().isBlank()) {
            entities.upsert(configuration.getAlias(), entities.getLastOrThrow(EServiceTemplate.class));
        }
    }

    @DataTableType
    public RestInterfaceConfiguration restInterfaceConfiguration(DataTable table) throws IOException {
        Map<String, String> values = new java.util.LinkedHashMap<>();
        table.cells().stream().skip(1)
                .forEach(row -> values.put(row.get(0), row.get(1)));
        List<String> serverUrls = objectMapper.readValue(
                values.get("serverUrls"), new TypeReference<>() {}
        );
        return new RestInterfaceConfiguration(
                serverUrls,
                values.get("contactName"),
                values.get("contactEmail"),
                values.get("contactUrl"),
                values.get("termsAndConditionsUrl")
        );
    }

    @When("l'erogatore tenta di aggiornare il campo {string} del {eServiceTemplateByAlias} con il valore {string}")
    public void attemptTemplateFieldUpdate(String field, EServiceTemplate template, String value) {
        switch (field) {
            case "nome" -> templateUseCase.attemptTemplateNameUpdate(template, value);
            case "descrizione" -> templateUseCase.attemptTemplateDescriptionUpdate(template, value);
            case "destinatario" -> templateUseCase.attemptTemplateIntendedTargetUpdate(template, value);
            default -> throw new IllegalArgumentException("Unsupported template field: " + field);
        }
    }

    @When("l'erogatore tenta di rinominare il {eServiceTemplateByAlias} con il nome del {eServiceTemplateByAlias}")
    public void attemptTemplateRenameWithDuplicateName(EServiceTemplate template, EServiceTemplate existing) {
        templateUseCase.attemptTemplateNameUpdate(template, existing.getName());
    }

    @When("l'erogatore tenta di creare un template e-service duplicando il nome del {eServiceTemplateByAlias}")
    public void attemptTemplateCreationWithDuplicateName(EServiceTemplate existing) {
        templateUseCase.attemptTemplateCreation(existing.getName());
    }

    @When("l'erogatore tenta di creare un template e-service con il nome {string}")
    public void attemptTemplateCreation(String name) {
        templateUseCase.attemptTemplateCreation(name);
    }

    @When("l'erogatore tenta di modificare la descrizione della versione {int} del {eServiceTemplateByAlias} in {string}")
    public void attemptDraftVersionDescriptionUpdate(int version, EServiceTemplate template, String description) {
        templateVersionUseCase.attemptDraftVersionDescriptionUpdate(template, template.getVersion(version), description);
    }

    @When("l'erogatore allega il file {string} come {string} sulla versione del template con il nome {string}")
    public void attachFileToTemplateVersion(String filePath, String documentType, String documentName) {
        // TODO
        // Usato negli scenari con id ERROR_188, ERROR_189, ERROR_190, ERROR_192, ERROR_193, ERROR_194
    }
}
