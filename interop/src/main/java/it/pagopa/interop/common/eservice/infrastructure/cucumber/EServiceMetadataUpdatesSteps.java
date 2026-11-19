package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.application.EServiceRequestFactory;
import it.pagopa.interop.common.eservice.application.command.EServiceUpdateCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.eservice.domain.EServiceUpdateFailureReason;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServiceMetadataUpdateConfiguration;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper.EServiceMetadataUpdateConfigurationMapper;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.utils.RandomUtils;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class EServiceMetadataUpdatesSteps {

    private final InteropJourney interopJourney;
    private final EServiceRequestFactory requestFactory;
    private final EntityStore entityStore;
    private final EServiceMetadataUpdateConfigurationMapper configurationMapper;
    private EService initialEService;
    private Map<String, String> requestedMetadata = Map.of();

    @When("un {userRole} di/del {tenant} richiede la modifica dei metadati dell'e-service {string}:")
    public void updateMetadata(UserRole role, Tenant producer, String alias, List<EServiceMetadataUpdateConfiguration> configurations) {
        EService currentEService = entityStore.getByAliasOrThrow(alias, EService.class);
        initialEService = currentEService.toBuilder()
                .descriptors(new ArrayList<>(currentEService.getDescriptors()))
                .build();
        EServiceUpdateCommand updateCommand = requestFactory.defaultUpdateEServiceCommand().alias(alias);
        requestedMetadata = configurationMapper.apply(configurations, updateCommand);
        interopJourney
                .withProducer(producer, role)
                .updateEService(updateCommand);
    }

    @When("un {userRole} di/del {tenant} tenta la modifica dei metadati dell'e-service {string}:")
    public void tryUpdateMetadata(UserRole role, Tenant producer, String alias, List<EServiceMetadataUpdateConfiguration> configurations) {
        EService currentEService = entityStore.getByAliasOrThrow(alias, EService.class);
        initialEService = currentEService.toBuilder()
                .descriptors(new ArrayList<>(currentEService.getDescriptors()))
                .build();
        EServiceUpdateCommand updateCommand = requestFactory.defaultUpdateEServiceCommand().alias(alias);
        requestedMetadata = configurationMapper.apply(configurations, updateCommand);
        interopJourney
                .withProducer(producer, role)
                .tryUpdateEService(updateCommand);
    }

    @When("un {userRole} di/del {tenant} richiede per l'e-service {string} il nome di {string} cambiandone soltanto le maiuscole e una nuova descrizione")
    public void updateEServiceNameWithExisting(UserRole role, Tenant producer, String targetAlias, String existingAlias) {
        updateEServiceNameWithExisting(role, producer, targetAlias, existingAlias, true, true, true);
    }

    @When("un {userRole} di/del {tenant} richiede di assegnare all'e-service {string} il nome di {string}")
    public void updateEServiceNameWithExistingOfAnotherTenant(UserRole role, Tenant producer, String targetAlias, String existingAlias) {
        updateEServiceNameWithExisting(role, producer, targetAlias, existingAlias, false, false, false);
    }

    private void updateEServiceNameWithExisting(
            UserRole role,
            Tenant producer,
            String targetAlias,
            String existingAlias,
            boolean includeNewDescription,
            boolean tryUpdate,
            boolean changeCase
    ) {
        var existing = entityStore.getByAliasOrThrow(existingAlias, EService.class);
        initialEService = existing.toBuilder()
                .descriptors(new ArrayList<>(existing.getDescriptors()))
                .build();
        String requestedName = changeCase ? existing.getName().toUpperCase(Locale.ROOT) : existing.getName();
        EServiceUpdateCommand updateCommand = requestFactory.defaultUpdateEServiceCommand()
                .alias(targetAlias)
                .name(requestedName);
        if (includeNewDescription) {
            updateCommand.description(RandomUtils.randomAlphanumeric(20));
        }
        if (tryUpdate) {
            interopJourney
                    .withProducer(producer, role)
                    .tryUpdateEService(updateCommand);
        } else {
            interopJourney
                    .withProducer(producer, role)
                    .updateEService(updateCommand)
                    .waitUntilEService(targetAlias, eService -> eService.getName().equals(requestedName));
        }
    }

    @When("un {userRole} di/del {tenant} tenta la modifica del campo {string} dell'e-service {string} con un valore di {int} caratteri")
    public void tryUpdateEServiceField(UserRole role, Tenant producer, String fieldName, String alias, int fieldLength) {
        updateEServiceField(role, producer, fieldName, alias, fieldLength, true);
    }

    @When("un {userRole} di/del {tenant} richiede la modifica del campo {string} dell'e-service {string} con un valore di {int} caratteri")
    public void updateEServiceField(UserRole role, Tenant producer, String fieldName, String alias, int fieldLength) {
        updateEServiceField(role, producer, fieldName, alias, fieldLength, false);
    }

    private void updateEServiceField(UserRole role, Tenant producer, String fieldName, String alias, int fieldLength, boolean onlyTry) {
        if (fieldLength < 0) {
            throw new IllegalArgumentException("La lunghezza del campo non può essere negativa: " + fieldLength);
        }

        String value = RandomUtils.randomAlphanumeric(fieldLength);
        EService currentEService = entityStore.getByAliasOrThrow(alias, EService.class);
        initialEService = currentEService.toBuilder()
                .descriptors(new ArrayList<>(currentEService.getDescriptors()))
                .build();
        requestedMetadata = Map.of(fieldName, value);

        Consumer<EServiceUpdateCommand> updateCommand = command -> {
            command.alias(alias);
            switch (fieldName) {
                case "nome" -> command.name(value);
                case "descrizione" -> command.description(value);
                default ->
                        throw new IllegalArgumentException("Campo metadato e-service non supportato: " + fieldName);
            }
        };

        InteropJourney journey = interopJourney.withProducer(producer, role);
        if (onlyTry) {
            journey.tryUpdateEService(updateCommand);
        } else {
            journey.updateEService(updateCommand);
        }
    }

    @When("un {userRole} di/del {tenant} richiede la modifica dell'e-service {string} con un payload vuoto")
    public void updateEServiceFieldWithEmptyPayload(UserRole role, Tenant producer, String alias) {
        EService currentEService = entityStore.getByAliasOrThrow(alias, EService.class);
        initialEService = currentEService.toBuilder()
                .descriptors(new ArrayList<>(currentEService.getDescriptors()))
                .build();
        requestedMetadata = Map.of();

        interopJourney
                .withProducer(producer, role)
                .tryUpdateEService(command -> command.alias(alias));
    }

    @Then("la rinomina della risorsa {string} viene accettata e ha lo stesso nome di {string}")
    public void eServicesHaveSameName(String aliasEService1, String aliasEService2) {
        var eService1 = entityStore.getByAliasOrThrow(aliasEService1, EService.class);
        var eService2 = entityStore.getByAliasOrThrow(aliasEService2, EService.class);

        assertThat(eService1.getName())
                .as("Nome dell'e-service %s dopo la rinomina", aliasEService1)
                .isEqualTo(eService2.getName());
        assertThat(eService2)
                .as("L'e-service %s non deve essere modificato dalla rinomina di %s", aliasEService2, aliasEService1)
                .isEqualTo(initialEService);
    }

    @Then("la modifica dei metadati dell'e-service {string} viene rifiutata per {eServiceUpdateFailureReason}")
    public void shouldFailToUpdateMetadata(String alias, EServiceUpdateFailureReason reason) {
        interopJourney.shouldFailToUpdateEService(reason);
    }

    @Then("l'e-service {string} presenta i metadati richiesti e conserva quelli non modificati, le versioni, gli stati e le configurazioni dei descrittori")
    public void assertMetadataUpdatedAndOtherDataPreserved(String alias) {
        EService expectedEService = initialEService.toBuilder()
                .name(requestedMetadata.getOrDefault("nome", initialEService.getName()))
                .description(requestedMetadata.getOrDefault("descrizione", initialEService.getDescription()))
                .technology(requestedEnum("technology", EServiceTechnology.class, initialEService.getTechnology()))
                .mode(requestedEnum("mode", EServiceMode.class, initialEService.getMode()))
                .isSignalHubEnabled(requestedBoolean("signalHubEnabled", initialEService.getIsSignalHubEnabled()))
                .isConsumerDelegable(requestedBoolean("consumerDelegable", initialEService.getIsConsumerDelegable()))
                .isClientAccessDelegable(requestedBoolean("clientAccessDelegable", initialEService.getIsClientAccessDelegable()))
                .personalData(requestedBoolean("handlePersonalData", initialEService.getPersonalData()))
                .asyncExchange(requestedBoolean("asyncExchange", initialEService.getAsyncExchange()))
                .build();

        EService actualEService = entityStore.getByAliasOrThrow(alias, EService.class);
        assertThat(actualEService)
                .as("E-service %s dopo la modifica dei metadati", alias)
                .isEqualTo(expectedEService);
    }

    private Boolean requestedBoolean(String field, Boolean initialValue) {
        return requestedMetadata.containsKey(field)
                ? requestedMetadata.get(field) == null ? null : Boolean.valueOf(requestedMetadata.get(field))
                : initialValue;
    }

    private <T extends Enum<T>> T requestedEnum(String field, Class<T> enumType, T initialValue) {
        if (!requestedMetadata.containsKey(field)) {
            return initialValue;
        }
        String value = requestedMetadata.get(field);
        return value == null ? null : Enum.valueOf(enumType, value);
    }
}
