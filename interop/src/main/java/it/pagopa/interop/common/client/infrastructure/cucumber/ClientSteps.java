package it.pagopa.interop.common.client.infrastructure.cucumber;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.client.application.ClientUseCase;
import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose.domain.Purpose;
import lombok.RequiredArgsConstructor;

import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class ClientSteps {

    private final EntityStore entityStore;
    private final ClientUseCase clientUseCase;
    private final InteropJourney interopJourney;
    private Client clientReference;

    @When("l'{userRole} di/del {tenant} richiede la generazione del voucher per il client e la finalità attiva")
    public void generateClientVoucher(UserRole role, Tenant tenant)
            throws NoSuchAlgorithmException, JsonProcessingException {
        Client client = entityStore.getLastOrThrow(Client.class);
        Purpose purpose = entityStore.getAll(Purpose.class).stream()
                .filter(candidate -> candidate.getLastActiveVersion() != null)
                .reduce((first, last) -> last)
                .orElseThrow(() -> new NoSuchElementException("Nessuna finalita attiva presente nell'entity store"));
        interopJourney.withProducer(tenant, role);
        clientUseCase.generateVoucher(client, purpose);
    }

    @Given("i dati e le associazioni del client {string} allo stato attuale appuntati come riferimento")
    public void saveClientReference(String clientAlias) {
        Client client = entityStore.getByAliasOrThrow(clientAlias, Client.class);
        client = clientUseCase.getClient(client);
        clientReference = client.toBuilder()
                .keys(new LinkedHashSet<>(client.getKeys()))
                .purposes(List.copyOf(client.getPurposes()))
                .users(List.copyOf(client.getUsers()))
                .build();
    }

    @ParameterType("il nome e la descrizione|il nome|la descrizione")
    public ClientMetadataField clientMetadataField(String field) {
        return switch (field) {
            case "il nome" -> ClientMetadataField.NAME;
            case "la descrizione" -> ClientMetadataField.DESCRIPTION;
            case "il nome e la descrizione" -> ClientMetadataField.NAME_AND_DESCRIPTION;
            default -> throw new IllegalArgumentException("Campo metadato client non supportato: " + field);
        };
    }

    @Then("il client {string} conserva le associazioni e i dati di riferimento")
    public void assertClientPreserved(String clientAlias) {
        assertClientPreserved(clientAlias, Set.of(), "Dati e associazioni del client " + clientAlias);
    }

    @Then("il client {string} conserva le associazioni e i dati di riferimento eccetto {clientMetadataField}")
    public void assertClientPreservedExceptField(String clientAlias, ClientMetadataField excludedField) {
        assertClientPreserved(clientAlias, excludedField.fieldNames,
                "Dati e associazioni del client " + clientAlias + " eccetto " + excludedField.getLabel());
    }

    private void assertClientPreserved(String clientAlias, Set<String> excludedFields, String description) {
        assertThat(clientReference)
                .as("Riferimento del client salvato prima della modifica")
                .isNotNull();
        Client client = entityStore.getByAliasOrThrow(clientAlias, Client.class);
        client = clientUseCase.getClient(client);

        if (excludedFields.contains("name")) {
            assertThat(client.getName())
                    .as("Nome del client %s dopo la modifica", clientAlias)
                    .isNotEqualTo(clientReference.getName());
        }
        if (excludedFields.contains("description")) {
            assertThat(client.getDescription())
                    .as("Descrizione del client %s dopo la modifica", clientAlias)
                    .isNotEqualTo(clientReference.getDescription());
        }
        assertThat(client)
                .as(description)
                .usingRecursiveComparison()
                .ignoringFields(excludedFields.toArray(String[]::new))
                .isEqualTo(clientReference);
    }

    @Given("un {userRole} di/del {tenant} ha associato la finalità al client {string}")
    public void linkClientToPurpose(UserRole role, Tenant consumer, String clientAlias) {
        Client client = entityStore.getByAliasOrThrow(clientAlias, Client.class);
        Purpose purpose = entityStore.getLastOrThrow(Purpose.class);

        interopJourney.withConsumer(consumer, role);
        clientUseCase.linkPurpose(client, purpose);
    }

    @When("un {userRole} di/del {tenant} modifica i metadati del client {string}:")
    public void updateClientMetadata(
            UserRole role,
            Tenant producer,
            String clientAlias,
            DataTable dataTable
    ) {
        Client client = entityStore.getByAliasOrThrow(clientAlias, Client.class);
        MetadataUpdate metadata = readMetadataUpdate(client, dataTable);

        interopJourney.withProducer(producer, role);
        clientUseCase.updateMetadata(client, metadata.name(), metadata.description());
    }

    @When("un {userRole} di/del {tenant} tenta di modificare i metadati del client {string}:")
    public void tryToUpdateClientMetadata(
            UserRole role,
            Tenant producer,
            String clientAlias,
            DataTable dataTable
    ) {
        Client client = entityStore.getByAliasOrThrow(clientAlias, Client.class);
        MetadataUpdate metadata = readMetadataUpdate(client, dataTable);

        interopJourney.withProducer(producer, role);
        clientUseCase.tryToUpdateMetadata(client, metadata.name(), metadata.description());
    }

    @Then("la richiesta di modifica fallisce come operazione vietata")
    public void shouldFailToUpdateMetadataAsForbidden() {
        clientUseCase.shouldFailToUpdateMetadataAsForbidden();
    }

    private MetadataUpdate readMetadataUpdate(Client client, DataTable dataTable) {
        List<List<String>> rows = dataTable.cells();

        if (rows.size() < 2 || !rows.get(0).equals(List.of("campo", "valore"))) {
            throw new IllegalArgumentException(
                    "La tabella dei metadati del client deve avere le colonne 'campo' e 'valore' e almeno un campo"
            );
        }

        String name = client.getName();
        String description = client.getDescription();
        Set<String> updatedFields = new HashSet<>();

        for (List<String> row : rows.subList(1, rows.size())) {
            if (row.size() != 2) {
                throw new IllegalArgumentException("Ogni riga dei metadati del client deve contenere campo e valore");
            }

            String field = row.get(0);
            if (!updatedFields.add(field)) {
                throw new IllegalArgumentException("Campo metadato client duplicato: " + field);
            }

            switch (field) {
                case "nome" -> name = row.get(1);
                case "descrizione" -> description = row.get(1);
                default -> throw new IllegalArgumentException("Campo metadato client non supportato: " + field);
            }
        }

        return new MetadataUpdate(name, description);
    }

    private record MetadataUpdate(String name, String description) {}

    public enum ClientMetadataField {
        NAME(Set.of("name"), "il nome"),
        DESCRIPTION(Set.of("description"), "la descrizione"),
        NAME_AND_DESCRIPTION(Set.of("name", "description"), "il nome e la descrizione");

        private final Set<String> fieldNames;
        private final String label;

        ClientMetadataField(Set<String> fieldNames, String label) {
            this.fieldNames = fieldNames;
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }
}
