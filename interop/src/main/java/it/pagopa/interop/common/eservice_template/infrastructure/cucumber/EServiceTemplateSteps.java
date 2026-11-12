package it.pagopa.interop.common.eservice_template.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.DataTableType;
import io.cucumber.datatable.DataTable;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateRequestFactory;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplatePreparationConfiguration;
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

    @Given("un template e-service in ricezione con una versione in bozza")
    public void createReceiveDraftTemplate() {
        journey.createEServiceTemplate(command -> command.mode(EServiceMode.RECEIVE),
                EServiceTemplateVersionState.DRAFT);
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
}
