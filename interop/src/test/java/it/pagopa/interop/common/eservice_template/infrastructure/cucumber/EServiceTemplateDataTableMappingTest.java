package it.pagopa.interop.common.eservice_template.infrastructure.cucumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import it.pagopa.interop.common.eservice.domain.RestInterfaceConfiguration;
import it.pagopa.interop.common.eservice.domain.SoapAddressConfiguration;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateRequestFactory;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplatePreparationConfiguration;
import it.pagopa.interop.common.journey.application.InteropJourney;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class EServiceTemplateDataTableMappingTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void typed_preparation_accepts_gherkin_aliases() {
        EServiceTemplatePreparationConfiguration configuration = mapper.convertValue(Map.of(
                "tecnologia", "SOAP",
                "stato", "PUBLISHED",
                "interfaccia", "soap.wsdl"
        ), EServiceTemplatePreparationConfiguration.class);

        assertThat(configuration.getTechnology().name()).isEqualTo("SOAP");
        assertThat(configuration.getState().name()).isEqualTo("PUBLISHED");
        assertThat(configuration.getInterfaceFixture()).isEqualTo("soap.wsdl");
    }

    @Test
    void soap_rows_preserve_order_duplicates_and_unvalidated_strings() {
        List<SoapAddressConfiguration> addresses = mapper.convertValue(
                List.of(
                        Map.of("servizio", "Svc", "porta", "P2", "indirizzo", "https://host/x<node/>"),
                        Map.of("servizio", "Svc", "porta", "P2", "indirizzo", "https://host/x<node/>")
                ),
                mapper.getTypeFactory().constructCollectionType(List.class, SoapAddressConfiguration.class)
        );

        assertThat(addresses).extracting(SoapAddressConfiguration::address)
                .containsExactly("https://host/x<node/>", "https://host/x<node/>");
        List<SoapAddressConfiguration> empty = mapper.convertValue(
                List.of(),
                mapper.getTypeFactory().constructCollectionType(List.class, SoapAddressConfiguration.class)
        );
        assertThat(empty).isEmpty();
    }

    @Test
    void rest_table_type_converts_vertical_json_url_configuration() throws Exception {
        EServiceTemplateSteps steps = new EServiceTemplateSteps(mock(InteropJourney.class), mock(EServiceTemplateRequestFactory.class), mapper);
        DataTable table = DataTable.create(List.of(
                List.of("campo", "valore"),
                List.of("serverUrls", "[\"https://host.example/api\"]"),
                List.of("contactName", "Nome"),
                List.of("contactEmail", "nome@example.test"),
                List.of("contactUrl", "https://host.example/contact"),
                List.of("termsAndConditionsUrl", "https://host.example/terms")
        ));

        RestInterfaceConfiguration configuration = steps.restInterfaceConfiguration(table);

        assertThat(configuration.serverUrls()).containsExactly("https://host.example/api");
        assertThat(configuration.contactName()).isEqualTo("Nome");
    }
}
