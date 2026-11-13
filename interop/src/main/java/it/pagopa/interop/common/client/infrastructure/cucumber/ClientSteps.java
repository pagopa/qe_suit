package it.pagopa.interop.common.client.infrastructure.cucumber;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.datatable.DataTable;
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

    @Given("appuntati i dati e le associazioni del client {string} allo stato attuale come riferimento")
    public void saveClientReference(String clientAlias) {
        Client client = entityStore.getByAliasOrThrow(clientAlias, Client.class);
        clientReference = client.toBuilder()
                .keys(new LinkedHashSet<>(client.getKeys()))
                .purposes(List.copyOf(client.getPurposes()))
                .users(List.copyOf(client.getUsers()))
                .build();
    }

    @Then("il client {string} conserva le associazioni e i dati di riferimento eccetto il nome")
    public void assertClientPreservedExceptName(String clientAlias) {
        assertThat(clientReference)
                .as("Riferimento del client salvato prima della modifica")
                .isNotNull();
        Client client = entityStore.getByAliasOrThrow(clientAlias, Client.class);

        assertThat(client.getName())
                .as("Nome del client %s dopo la modifica", clientAlias)
                .isNotEqualTo(clientReference.getName());
        assertThat(client)
                .as("Dati e associazioni del client %s eccetto il nome", clientAlias)
                .usingRecursiveComparison()
                .ignoringFields("name")
                .isEqualTo(clientReference);
    }

    @Given("un {userRole} di/del {tenant} ha associato la finalità al client {string}")
    public void linkClientToPurpose(UserRole role, Tenant consumer, String clientAlias) {
        Client client = entityStore.getByAliasOrThrow(clientAlias, Client.class);
        Purpose purpose = entityStore.getLastOrThrow(Purpose.class);

        interopJourney.withConsumer(consumer, role);
        clientUseCase.linkPurpose(client, purpose);
    }

    @When("un {userRole} di/del {tenant} richiede la modifica dei metadati del client {string}:")
    public void updateClientMetadata(
            UserRole role,
            Tenant producer,
            String clientAlias,
            DataTable dataTable
    ) {
        Client client = entityStore.getByAliasOrThrow(clientAlias, Client.class);
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

        interopJourney.withProducer(producer, role);
        clientUseCase.updateMetadata(client, name, description);
    }
}
