package it.pagopa.interop.common.eservice_template.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.DataTableType;
import io.cucumber.datatable.DataTable;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateRequestFactory;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateUseCase;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplatePreparationConfiguration;
import it.pagopa.interop.common.eservice.domain.RestInterfaceConfiguration;
import it.pagopa.interop.common.journey.application.InteropJourney;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

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

    @Given("un template e-service preparato con la seguente configurazione:")
    public void prepareTemplate(EServiceTemplatePreparationConfiguration configuration) {
        Assertions.assertThat(configuration.getTechnology()).isNotNull();
        Assertions.assertThat(configuration.getState()).isNotNull();
        Assertions.assertThat(configuration.getInterfaceFixture()).isNotBlank();

        var command = requestFactory.defaultCreationEServiceTemplateCommand(configuration);
        journey.createEServiceTemplate(command, configuration.getState());
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
