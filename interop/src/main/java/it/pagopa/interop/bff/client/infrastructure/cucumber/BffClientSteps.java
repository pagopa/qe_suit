package it.pagopa.interop.bff.client.infrastructure.cucumber;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.client.application.ClientUseCase;
import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose.domain.Purpose;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class BffClientSteps {

    private final EntityStore entityStore;
    private final ClientUseCase clientUseCase;
    private final InteropJourney interopJourney;

    @Given("il client {string} associato alla finalità")
    public void linkClientToPurpose(String clientAlias) {
        Client client = entityStore.getByAliasOrThrow(clientAlias, Client.class);
        Purpose purpose = entityStore.getLastOrThrow(Purpose.class);

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
