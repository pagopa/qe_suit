package it.pagopa.interop.common.producer_keychain.infrastructure.cucumber;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.kernel.domain.KeyRegistrationMaterial;
import it.pagopa.interop.common.producer_keychain.application.ProducerKeychainFactory;
import io.cucumber.java.en.Then;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.producer_keychain.application.ProducerKeychainUseCase;
import it.pagopa.interop.common.producer_keychain.domain.ProducerKeychain;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class ProducerKeychainSteps {
    private final EntityStore entityStore;
    private final InteropJourney journey;
    private final ProducerKeychainFactory factory;
    private final ProducerKeychainUseCase useCase;
    private ProducerKeychain keychainReference;

    @Given("un portachiavi erogatore creato dal {tenant}")
    public void createProducerKeychain(Tenant producer) {
        journey.withProducer(producer, UserRole.ADMIN)
            .createProducerKeychain(factory.creationCommand(
                List.of(User.getTenantAdmin(producer).getUserId())));
    }

    @When("l'utente tenta di associare il {currentProducerKeychain} al{eServiceByAlias}")
    public void attemptEServiceAssociation(ProducerKeychain keychain, EService eService) {
        useCase.attemptEServiceAssociation(keychain.getRef(), eService.getRef());
    }

    @When("l'erogatore tenta di aggiungere la chiave di test \"{keyRegistrationMaterial}\" al \"{currentProducerKeychain}\" con il nome {string}")
    public void attemptKeyRegistration(KeyRegistrationMaterial material, ProducerKeychain keychain, String name) {
        useCase.attemptKeyRegistration(keychain, material, name);
    }

    @When("l'utente tenta di creare un portachiavi di erogazione con il nome {string}")
    public void attemptProducerKeychainCreation(String name) {
        useCase.attemptProducerKeychainCreation(factory.creationCommand().name(name));
    }

    @Given("i dati e le associazioni del portachiavi {string} allo stato attuale appuntati come riferimento")
    public void saveKeychainReference(String keychainAlias) {
        ProducerKeychain keychain = entityStore.getByAliasOrThrow(keychainAlias, ProducerKeychain.class);
        keychain = useCase.getProducerKeychain(keychain.getRef());
        keychainReference = keychain.toBuilder()
                .keys(keychain.getKeys() != null ? List.copyOf(keychain.getKeys()) : null)
                .publicKeys(keychain.getPublicKeys() != null ? List.copyOf(keychain.getPublicKeys()) : null)
                .users(keychain.getUsers() != null ? Set.copyOf(keychain.getUsers()) : null)
                .eservices(keychain.getEservices() != null ? Set.copyOf(keychain.getEservices()) : null)
                .build();
    }

    @ParameterType("il nome e la descrizione|il nome|la descrizione")
    public ProducerKeychainMetadataField producerKeychainMetadataField(String field) {
        return switch (field) {
            case "il nome" -> ProducerKeychainMetadataField.NAME;
            case "la descrizione" -> ProducerKeychainMetadataField.DESCRIPTION;
            case "il nome e la descrizione" -> ProducerKeychainMetadataField.NAME_AND_DESCRIPTION;
            default -> throw new IllegalArgumentException("Campo metadato portachiavi non supportato: " + field);
        };
    }

    @Then("la richiesta di modifica dei metadati del portachiavi erogatore fallisce come operazione malformata")
    public void shouldFailToUpdateMetadataAsMalformed() {
        useCase.shouldFailToUpdateMetadataAsMalformed();
    }

    @Then("la richiesta di modifica dei metadati del portachiavi erogatore fallisce come operazione vietata")
    public void shouldFailToUpdateMetadataAsForbidden() {
        useCase.shouldFailToUpdateMetadataAsForbidden();
    }

    @Then("la richiesta di modifica dei metadati del portachiavi erogatore fallisce per conflitto di risorsa")
    public void shouldFailToUpdateMetadataAsConflict() {
        useCase.shouldFailToUpdateMetadataAsConflict();
    }

    @Then("il portachiavi {string} conserva le associazioni e i dati di riferimento")
    public void assertKeychainPreserved(String keychainAlias) {
        assertKeychainPreserved(keychainAlias, Set.of(), "Dati e associazioni del portachiavi " + keychainAlias);
    }

    @Then("il portachiavi {string} conserva le associazioni e i dati di riferimento eccetto {producerKeychainMetadataField}")
    public void assertKeychainPreservedExceptField(String keychainAlias, ProducerKeychainMetadataField excludedField) {
        assertKeychainPreserved(keychainAlias, excludedField.fieldNames,
                "Dati e associazioni del portachiavi " + keychainAlias + " eccetto " + excludedField.getLabel());
    }

    private void assertKeychainPreserved(String keychainAlias, Set<String> excludedFields, String description) {
        assertThat(keychainReference)
                .as("Riferimento del portachiavi salvato prima della modifica")
                .isNotNull();
        ProducerKeychain keychain = useCase.getProducerKeychain(
                entityStore.getByAliasOrThrow(keychainAlias, ProducerKeychain.class).getRef());

        if (excludedFields.contains("name")) {
            assertThat(keychain.getName())
                    .as("Nome del portachiavi %s dopo la modifica", keychainAlias)
                    .isNotEqualTo(keychainReference.getName());
        }
        if (excludedFields.contains("description")) {
            assertThat(keychain.getDescription())
                    .as("Descrizione del portachiavi %s dopo la modifica", keychainAlias)
                    .isNotEqualTo(keychainReference.getDescription());
        }
        assertThat(keychain)
                .as(description)
                .usingRecursiveComparison()
                .ignoringFields(excludedFields.toArray(String[]::new))
                .isEqualTo(keychainReference);
    }

    public enum ProducerKeychainMetadataField {
        NAME(Set.of("name"), "il nome"),
        DESCRIPTION(Set.of("description"), "la descrizione"),
        NAME_AND_DESCRIPTION(Set.of("name", "description"), "il nome e la descrizione");

        private final Set<String> fieldNames;
        private final String label;

        ProducerKeychainMetadataField(Set<String> fieldNames, String label) {
            this.fieldNames = fieldNames;
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }
}
