package it.pagopa.interop.bff.infrastructure.cucumber;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.restassured.path.json.JsonPath;
import it.pagopa.application.context.EntityStore;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.client.domain.ClientKey;
import it.pagopa.interop.common.infrastructure.copy.CopyTemplateResolver;
import it.pagopa.interop.common.infrastructure.copy.ExpectedErrorMessages;
import it.pagopa.interop.common.kernel.domain.Channel;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class BffErrorMessageSteps {

    private final LastApiResponseStore responses;
    private final CurrentChannel<Channel> currentChannel;
    private final EntityStore entityStore;

    @Then("il messaggio di errore corrisponde al caso {string}")
    public void assertErrorMessage(String messageId) {
        assertErrorMessage(messageId, currentChannel.getCurrentLanguage().getLanguage(), Map.of());
    }

    @Then("il messaggio di errore corrisponde al caso {string} in lingua {string} con i valori:")
    public void assertErrorMessageWithValues(String messageId, String language, DataTable table) {
        assertErrorMessage(messageId, language, parsePlaceholderValues(table));
    }

    private Map<String, String> parsePlaceholderValues(DataTable table) {
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
        return values;
    }

    private void assertErrorMessage(String messageId, String language, Map<String, String> values) {
        ExpectedErrorMessages catalog = ExpectedErrorMessages.load();
        Map<String, String> resolvedValues = resolvePlaceholders(catalog, messageId, language, values);
        String expected = catalog.render(messageId, language, resolvedValues);
        var response = responses.getLastResponse();
        assertThat(response).as("Risposta BFF da verificare per %s", messageId).isNotNull();
        assertThat(response.getStatusCode())
                .as("Status HTTP di errore per %s", messageId)
                .satisfiesAnyOf(
                        statusCode -> assertThat(statusCode).isBetween(400, 499),
                        statusCode -> assertThat(statusCode).isEqualTo(503));
        Object userMessage = JsonPath.from(response.getRawContent()).get("userMessage");
        assertThat(userMessage)
                .as("userMessage per %s in lingua %s", messageId, language)
                .isEqualTo(expected);
    }

    private Map<String, String> resolvePlaceholders(
            ExpectedErrorMessages catalog,
            String messageId,
            String language,
            Map<String, String> explicitValues
    ) {
        Set<String> required = CopyTemplateResolver.placeholders(catalog.text(messageId, language));
        if (required.isEmpty()) {
            return explicitValues;
        }
        Map<String, String> resolved = new LinkedHashMap<>(explicitValues);
        if (required.contains("keyName") && !resolved.containsKey("keyName")) {
            resolveClientKeyName().ifPresent(keyName -> resolved.put("keyName", keyName));
        }
        return Map.copyOf(resolved);
    }

    private Optional<String> resolveClientKeyName() {
        return entityStore.getLast(Client.class)
                .map(Client::getFirstRegisteredKey)
                .map(ClientKey::name)
                .filter(name -> !name.isBlank());
    }
}
