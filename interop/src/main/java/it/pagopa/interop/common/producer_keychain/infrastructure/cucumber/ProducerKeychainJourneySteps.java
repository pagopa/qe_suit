package it.pagopa.interop.common.producer_keychain.infrastructure.cucumber;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.producer_keychain.application.ProducerKeychainFactory;
import it.pagopa.interop.common.producer_keychain.application.ProducerKeychainUseCase;
import it.pagopa.interop.common.producer_keychain.domain.ProducerKeychain;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.m2m.kernel.domain.M2MRole;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class ProducerKeychainJourneySteps {
    private final InteropJourney interopJourney;
    private final ProducerKeychainFactory factory;
    private final ProducerKeychainUseCase useCase;
    private final EntityStore entityStore;
    private final AuthSession authSession;

    @Given("un portachiavi {string} creato da/dal {tenant} in cui è presente un (utente ){userRole} e una coppia di chiavi crittografiche")
    public void setupKeychain(String alias, Tenant producer, UserRole userRole) {
        interopJourney.withProducer(producer, UserRole.ADMIN);
        authSession.set(producer);
        interopJourney.createProducerKeychain(
                factory.creationCommand(List.of(User.getTenantUser(producer, userRole).getUserId())));
        entityStore.upsert(alias, entityStore.getLastOrThrow(ProducerKeychain.class));
        interopJourney.createProducerKey(factory.keyCreationCommand());
    }

    @Given("un {userRole} di/del {tenant} ha associato l'e-service al portachiavi {string}")
    public void linkEServiceToKeychain(UserRole userRole, Tenant producer, String keychainAlias) {
        ProducerKeychain keychain = entityStore.getByAliasOrThrow(keychainAlias, ProducerKeychain.class);
        EService eservice = entityStore.getLastOrThrow(EService.class);

        interopJourney.withProducer(producer, userRole);
        authSession.set(producer);
        useCase.addEService(keychain.getRef(), eservice.getRef());
    }

    @When("un {userRole} di/del {tenant} modifica i metadati del portachiavi {string}:")
    public void updateKeychainMetadata(UserRole userRole, Tenant producer, String keychainAlias, DataTable dataTable) {
        ProducerKeychain keychain = entityStore.getByAliasOrThrow(keychainAlias, ProducerKeychain.class);
        MetadataUpdate metadata = readMetadataUpdate(keychain, dataTable);

        if (userRole == UserRole.ADMIN) {
            interopJourney.withProducer(producer);
        } else {
            interopJourney.withConsumer(producer, userRole);
        }
        useCase.updateMetadata(keychain.getRef(), metadata.name(), metadata.description());
    }

    @When("un {userRole} di/del {tenant} tenta di modificare i metadati del portachiavi {string}:")
    public void tryToUpdateKeychainMetadata(UserRole userRole, Tenant producer, String keychainAlias,
                                            DataTable dataTable) {
        ProducerKeychain keychain = entityStore.getByAliasOrThrow(keychainAlias, ProducerKeychain.class);
        MetadataUpdate metadata = readMetadataUpdate(keychain, dataTable);

        if (userRole == UserRole.ADMIN) {
            interopJourney.withProducer(producer);
        } else {
            interopJourney.withConsumer(producer, userRole);
        }
        useCase.tryToUpdateMetadata(keychain.getRef(), metadata.name(), metadata.description());
    }

    private MetadataUpdate readMetadataUpdate(ProducerKeychain keychain, DataTable dataTable) {
        List<List<String>> rows = dataTable.cells();

        if (rows.size() < 2 || !rows.get(0).equals(List.of("campo", "valore"))) {
            throw new IllegalArgumentException(
                    "La tabella dei metadati del portachiavi deve avere le colonne 'campo' e 'valore' e almeno un campo"
            );
        }

        String name = keychain.getName();
        String description = keychain.getDescription();
        Set<String> updatedFields = new HashSet<>();

        for (List<String> row : rows.subList(1, rows.size())) {
            if (row.size() != 2) {
                throw new IllegalArgumentException("Ogni riga dei metadati del portachiavi deve contenere campo e valore");
            }

            String field = row.get(0);
            if (!updatedFields.add(field)) {
                throw new IllegalArgumentException("Campo metadato portachiavi duplicato: " + field);
            }

            switch (field) {
                case "nome" -> name = row.get(1);
                case "descrizione" -> description = row.get(1);
                default -> throw new IllegalArgumentException("Campo metadato portachiavi non supportato: " + field);
            }
        }

        return new MetadataUpdate(name, description);
    }

    private record MetadataUpdate(String name, String description) {}
}
