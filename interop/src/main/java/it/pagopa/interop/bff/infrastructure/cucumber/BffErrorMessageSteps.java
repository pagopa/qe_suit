package it.pagopa.interop.bff.infrastructure.cucumber;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.restassured.path.json.JsonPath;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.interop.common.infrastructure.copy.ExpectedErrorMessages;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class BffErrorMessageSteps {

    private final LastApiResponseStore responses;

    @Then("il messaggio di errore corrisponde al caso {string} in lingua {string}")
    public void assertErrorMessage(String messageId, String language) {
        assertErrorMessage(messageId, language, Map.of());
    }

    @Then("il messaggio di errore corrisponde al caso {string} in lingua {string} con i valori:")
    public void assertErrorMessageWithValues(String messageId, String language, DataTable table) {
        Map<String, String> values = new LinkedHashMap<>();
        for (var row : table.cells()) {
            if (row.size() != 2 || row.get(0) == null || row.get(0).isBlank() || row.get(1) == null) {
                throw new IllegalArgumentException("Ogni riga deve contenere nome del placeholder e valore");
            }
            String name = row.get(0);
            if (values.putIfAbsent(name, row.get(1)) != null) {
                throw new IllegalArgumentException("Placeholder duplicato: " + name);
            }
        }
        assertErrorMessage(messageId, language, values);
    }

    private void assertErrorMessage(String messageId, String language, Map<String, String> values) {
        String expected = ExpectedErrorMessages.load().render(messageId, language, values);
        var response = responses.getLastResponse();
        assertThat(response).as("Risposta BFF da verificare per %s", messageId).isNotNull();
        assertThat(response.getStatusCode())
                .as("Status HTTP di errore per %s", messageId)
                .isBetween(400, 499);
        Object userMessage = JsonPath.from(response.getRawContent()).get("userMessage");
        assertThat(userMessage)
                .as("userMessage per %s in lingua %s", messageId, language)
                .isEqualTo(expected);
    }
}
