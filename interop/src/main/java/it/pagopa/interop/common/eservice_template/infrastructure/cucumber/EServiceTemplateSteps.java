package it.pagopa.interop.common.eservice_template.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.DataTableType;
import io.cucumber.datatable.DataTable;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateRequestFactory;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplatePreparationConfiguration;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.RestInterfaceConfiguration;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
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

    @Given("un template e-service preparato con la seguente configurazione:")
    public void prepareTemplate(EServiceTemplatePreparationConfiguration configuration) {
        var command = requestFactory.defaultCreationEServiceTemplateCommand();
        configurationMapper.apply(configuration, command);
        journey.createEServiceTemplate(command, configuration.getState());
    }

    @Given("un template e-service in modalità di {eServiceMode} creato e pubblicato da {tenant}")
    public void prepareTemplateWithMode(EServiceMode mode, Tenant producer) {
        journey.withProducer(producer, UserRole.ADMIN);
        var command = requestFactory.defaultCreationEServiceTemplateCommand();
        command.mode(mode);
        journey.createEServiceTemplate(command, EServiceTemplateVersionState.PUBLISHED);
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
