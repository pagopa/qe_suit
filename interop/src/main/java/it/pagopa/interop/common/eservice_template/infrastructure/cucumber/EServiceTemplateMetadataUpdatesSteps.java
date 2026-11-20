package it.pagopa.interop.common.eservice_template.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateRequestFactory;
import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateUpdateFailureReason;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.eservice_template.infrastructure.cucumber.dto.EServiceTemplateMetadataUpdateConfiguration;
import it.pagopa.interop.common.eservice_template.infrastructure.cucumber.dto.mapper.EServiceTemplateMetadataUpdateConfigurationMapper;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.InteropRole;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.utils.RandomUtils;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class EServiceTemplateMetadataUpdatesSteps {
    private final InteropJourney interopJourney;
    private final EServiceTemplateRequestFactory requestFactory;
    private final EServiceTemplateMetadataUpdateConfigurationMapper configurationMapper;
    private final EntityStore entityStore;
    private Map<String, String> requestedMetadata = Map.of();
    private EServiceTemplate initialEServiceTemplate;

    @Given("^un template e-service con versione (assenti|in bozza|pubblicata|sospesa|pubblicata e nuova bozza)$")
    public void createTemplateWithVersions(String version) {

        interopJourney.createEServiceTemplate();

        switch (version) {
            case "in bozza" -> interopJourney.publishVersion(EServiceTemplateVersionState.DRAFT);
            case "pubblicata" -> interopJourney.publishVersion(EServiceTemplateVersionState.PUBLISHED);
            case "sospesa" -> {
                interopJourney
                        .publishVersion(EServiceTemplateVersionState.SUSPENDED)
                        .waitUntilEServiceTemplate(template -> template.getLatestVersion().getState() == EServiceTemplateVersionState.SUSPENDED);
            }
            // case "soltanto deprecate" -> {}
            case "pubblicata e nuova bozza" -> {
                interopJourney
                        .publishVersion(EServiceTemplateVersionState.PUBLISHED)
                        .waitUntilEServiceTemplate(template -> template.getLatestVersion().getState() == EServiceTemplateVersionState.PUBLISHED)
                        .addVersion(EServiceTemplateVersionState.DRAFT);
            }
            default -> {
                throw new IllegalArgumentException("Unsupported version: " + version);
            }
        }
    }

    @Given("un {userRole} di/del {tenant} richiede la modifica dei metadati del template e-service {string}:")
    public void updateMetadata(InteropRole role, Tenant producer, String alias, List<EServiceTemplateMetadataUpdateConfiguration> metadata) {
        updateMetadata(role, producer, entityStore.getByAliasOrThrow(alias, EServiceTemplate.class), metadata,
                requestFactory.defaultUpdateEServiceTemplateCommand(true).alias(alias));
    }

    @Given("un sistema informatico autenticato con ruolo {userM2MRole} di/del {tenant} richiede la modifica dei metadati dell'{currentEServiceTemplate}:")
    @Given("un sistema informatico autenticato con ruolo {userM2MRole} di/del {tenant} richiede la modifica dei metadati del {currentEServiceTemplate}:")
    @Given("un {userRole} di/del {tenant} richiede la modifica dei metadati dell'{currentEServiceTemplate}:")
    @Given("un {userRole} di/del {tenant} richiede la modifica dei metadati del {currentEServiceTemplate}:")
    public void updateMetadata(InteropRole role, Tenant producer, EServiceTemplate eServiceTemplate, List<EServiceTemplateMetadataUpdateConfiguration> metadata) {
        updateMetadata(role, producer, eServiceTemplate, metadata, requestFactory.defaultUpdateEServiceTemplateCommand(true));
    }

    private void updateMetadata(InteropRole role, Tenant producer, EServiceTemplate eServiceTemplate,
                                List<EServiceTemplateMetadataUpdateConfiguration> metadata,
                                UpdateEServiceTemplateCommand command) {
        initialEServiceTemplate = eServiceTemplate.toBuilder().build();
        requestedMetadata = configurationMapper.apply(metadata, command);
        interopJourney
                .withProducer(producer, role)
                .updateEServiceTemplateMetadata(command);
    }

    @Given("un {userRole} di/del {tenant} tenta la modifica dei metadati del template e-service {string}:")
    public void tryUpdateMetadata(UserRole role, Tenant producer, String alias, List<EServiceTemplateMetadataUpdateConfiguration> metadata) {
        tryUpdateMetadata(role, producer, entityStore.getByAliasOrThrow(alias, EServiceTemplate.class), metadata);
    }

    @Given("un {userRole} di/del {tenant} tenta la modifica dei metadati dell'{currentEServiceTemplate}:")
    @Given("un {userRole} di/del {tenant} tenta la modifica dei metadati del {currentEServiceTemplate}:")
    public void tryUpdateMetadata(UserRole role, Tenant producer, EServiceTemplate eServiceTemplate, List<EServiceTemplateMetadataUpdateConfiguration> metadata) {
        initialEServiceTemplate = eServiceTemplate.toBuilder()
                // .versions(eServiceTemplate.getVersions())
                .build();
        UpdateEServiceTemplateCommand command = requestFactory.defaultUpdateEServiceTemplateCommand(true);
        requestedMetadata = configurationMapper.apply(metadata, command);
        interopJourney
                .withProducer(producer, role)
                .tryUpdateEServiceTemplateMetadata(command);
    }

    @When("un {userRole} di/del {tenant} richiede di assegnare al template e-service {string} il nome del template e-service {string} cambiandone soltanto le maiuscole e una nuova descrizione")
    public void renameEServiceTemplateWithExisting(UserRole role, Tenant producer, String targetAlias, String originAlias) {
        var originTemplate = entityStore.getByAliasOrThrow(originAlias, EServiceTemplate.class);
        UpdateEServiceTemplateCommand command = requestFactory.defaultUpdateEServiceTemplateCommand(true)
                .alias(targetAlias)
                .name(originTemplate.getName().toUpperCase())
                .description(RandomUtils.randomAlphanumeric(20));
        interopJourney
                .withProducer(producer, role)
                .tryUpdateEServiceTemplateMetadata(command);
    }

    @When("un {userRole} di/del {tenant} richiede di assegnare al template e-service {string} il nome del template e-service {string}")
    public void renameEServiceTemplateWithExistingAnotherTenant(UserRole role, Tenant producer, String targetAlias, String originAlias) {
        var originTemplate = entityStore.getByAliasOrThrow(originAlias, EServiceTemplate.class);
        String requestedName = originTemplate.getName().toUpperCase();
        String requestedDescription = RandomUtils.randomAlphanumeric(20);
        UpdateEServiceTemplateCommand command = requestFactory.defaultUpdateEServiceTemplateCommand(true)
                .alias(targetAlias)
                .name(requestedName)
                .description(requestedDescription);
        requestedMetadata = Map.of(
                "nome", requestedName,
                "descrizione", requestedDescription
        );
        interopJourney
                .withProducer(producer, role)
                .updateEServiceTemplateMetadata(command);
    }

    @Then("il template e-service {string} presenta i metadati richiesti e conserva quelli non modificati")
    public void checkMetadata(String alias) {
        var eServiceTemplate = entityStore.getByAliasOrThrow(alias, EServiceTemplate.class);
        checkMetadata(eServiceTemplate);
    }

    @Then("il {currentEServiceTemplate} presenta i metadati richiesti e conserva quelli non modificati")
    public void checkMetadata(EServiceTemplate actualEServiceTemplate) {
        EServiceTemplate expectedEServiceTemplate = initialEServiceTemplate.toBuilder()
                .name(requestedMetadata.getOrDefault("nome", initialEServiceTemplate.getName()))
                .description(requestedMetadata.getOrDefault("descrizione", initialEServiceTemplate.getDescription()))
                .intendedTarget(requestedMetadata.getOrDefault("intendedTarget", initialEServiceTemplate.getIntendedTarget()))
                .technology(requestedEnum("technology", EServiceTechnology.class, initialEServiceTemplate.getTechnology()))
                .mode(requestedEnum("mode", EServiceMode.class, initialEServiceTemplate.getMode()))
                .isSignalHubEnabled(requestedBoolean("signalHubEnabled", initialEServiceTemplate.getIsSignalHubEnabled()))
                .personalData(requestedBoolean("handlePersonalData", initialEServiceTemplate.getPersonalData()))
                .asyncExchange(requestedBoolean("asyncExchange", initialEServiceTemplate.getAsyncExchange()))
                .build();

        assertThat(actualEServiceTemplate)
                .as("E-service %s dopo la modifica dei metadati")
                .isEqualTo(expectedEServiceTemplate);
    }

    @Then("la modifica dei metadati del {currentEServiceTemplate} viene rifiutata per {eServiceTemplateUpdateFailureReason}")
    public void shouldFailToUpdateMetadata(EServiceTemplate eServiceTemplate, EServiceTemplateUpdateFailureReason reason) {
        interopJourney.shouldFailToUpdateEServiceTemplateMetadata(reason);
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

    @When("un {userRole} di/del {tenant} tenta la modifica del campo {string} del template e-service {string} con un valore di {int} caratteri")
    public void tryUpdateEServiceTemplateField(UserRole role, Tenant producer, String fieldName, String alias, int fieldLength) {
        updateEServiceTemplateField(role, producer, fieldName, alias, fieldLength, true);
    }

    @When("un {userRole} di/del {tenant} richiede la modifica del campo {string} del template e-service {string} con un valore di {int} caratteri")
    public void updateEServiceTemplateField(UserRole role, Tenant producer, String fieldName, String alias, int fieldLength) {
        updateEServiceTemplateField(role, producer, fieldName, alias, fieldLength, false);
    }

    private void updateEServiceTemplateField(UserRole role, Tenant producer, String fieldName, String alias, int fieldLength, boolean onlyTry) {
        if (fieldLength < 0) {
            throw new IllegalArgumentException("La lunghezza del campo non può essere negativa: " + fieldLength);
        }

        String value = RandomUtils.randomAlphanumeric(fieldLength);
        EServiceTemplate currentEServiceTemplate = entityStore.getByAliasOrThrow(alias, EServiceTemplate.class);
        initialEServiceTemplate = currentEServiceTemplate.toBuilder()
                .build();
        String requestedField = "destinatari".equals(fieldName) ? "intendedTarget" : fieldName;
        requestedMetadata = Map.of(requestedField, value);

        UpdateEServiceTemplateCommand updateCommand = requestFactory.defaultUpdateEServiceTemplateCommand(true)
                .alias(alias);
        switch (fieldName) {
            case "nome" -> updateCommand.name(value);
            case "descrizione" -> updateCommand.description(value);
            case "intendedTarget", "destinatari" -> updateCommand.intendedTarget(value);
            default -> throw new IllegalArgumentException("Campo metadato del template e-service non supportato: " + fieldName);
        }

        InteropJourney journey = interopJourney.withProducer(producer, role);
        if (onlyTry) {
            journey.tryUpdateEServiceTemplateMetadata(updateCommand);
        } else {
            journey.updateEServiceTemplateMetadata(updateCommand);
        }
    }

    @When("un {userRole} di/del {tenant} richiede la modifica del template e-service {string} con un payload vuoto")
    public void updateEServiceTemplateFieldWithEmptyPayload(UserRole role, Tenant producer, String alias) {
        EServiceTemplate currentEServiceTemplate = entityStore.getByAliasOrThrow(alias, EServiceTemplate.class);
        initialEServiceTemplate = currentEServiceTemplate.toBuilder().build();
        requestedMetadata = Map.of();
        UpdateEServiceTemplateCommand updateCommand = requestFactory.defaultUpdateEServiceTemplateCommand(true)
                .alias(alias);
        interopJourney
                .withProducer(producer, role)
                .tryUpdateEServiceTemplateMetadata(updateCommand);
    }

    @Then("{tenant} verifica che le istanze e-service presentino i metadati ereditati dal template {string}:")
    public void checkMetadata(Tenant producer, String aliasTemplate, List<String> instanceAliases) {
        interopJourney.withProducer(producer);
        checkInstanceMetadataPropagation(aliasTemplate, instanceAliases);
    }

    @Then("le istanze presentano i metadati ereditati dal template {string}:")
    public void checkInstanceMetadataPropagation(String templateAlias, List<String> instanceAliases) {
        EServiceTemplate updatedTemplate = entityStore.getByAliasOrThrow(templateAlias, EServiceTemplate.class);
        if (initialEServiceTemplate == null || !updatedTemplate.getId().equals(initialEServiceTemplate.getId())) {
            throw new IllegalStateException("Nessuna modifica dei metadati registrata per il template " + templateAlias);
        }

        instanceAliases.forEach(instanceAlias -> {
            EService instance = entityStore.getByAliasOrThrow(instanceAlias, EService.class);
            var association = initialEServiceTemplate.findInstance(instance.getId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Istanza %s (%s) non associata al template %s prima della modifica"
                                    .formatted(instanceAlias, instance.getId(), templateAlias)));
            String expectedName = updatedTemplate.getName() + " - " + association.suffix();

            interopJourney.waitUntilEService(instanceAlias, refreshed ->
                    expectedName.equals(refreshed.getName())
                            && Objects.equals(updatedTemplate.getDescription(), refreshed.getDescription()));

            EService refreshedInstance = entityStore.getByAliasOrThrow(instanceAlias, EService.class);
            assertThat(refreshedInstance.getName())
                    .as("Nome dell'istanza %s aggiornato in base al template %s, mantenendo l'etichetta", instanceAlias, templateAlias)
                    .isEqualTo(expectedName);
            assertThat(refreshedInstance.getDescription())
                    .as("Descrizione dell'istanza %s ereditata dal template %s", instanceAlias, templateAlias)
                    .isEqualTo(updatedTemplate.getDescription());
        });
    }
}
