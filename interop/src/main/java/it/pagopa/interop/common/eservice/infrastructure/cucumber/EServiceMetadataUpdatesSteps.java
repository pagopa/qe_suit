package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.application.EServiceRequestFactory;
import it.pagopa.interop.common.eservice.application.command.EServiceUpdateCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServiceMetadataUpdateConfiguration;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper.EServiceMetadataUpdateConfigurationMapper;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.utils.RandomUtils;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class EServiceMetadataUpdatesSteps {

    private final InteropJourney interopJourney;
    private final EServiceDescriptorUseCase eServiceDescriptorUseCase;
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
        EServiceUpdateCommand updateCommand = requestFactory.defaultUpdateEServiceCommand();
        updateCommand.alias(alias);
        requestedMetadata = configurationMapper.apply(configurations, updateCommand);
        interopJourney
                .withProducer(producer, role)
                .updateEService(updateCommand);
    }

    @Then("l'e-service {string} presenta i metadati richiesti e conserva quelli non modificati, le versioni, gli stati e le configurazioni dei descrittori")
    public void assertMetadataUpdatedAndOtherDataPreserved(String alias) {

        EService actualEService = entityStore.getByAliasOrThrow(alias, EService.class);

        EService expectedEService = initialEService.toBuilder()
                .name(requestedMetadata.getOrDefault("nome", initialEService.getName()))
                .description(requestedMetadata.getOrDefault("descrizione", initialEService.getDescription()))
                .technology(requestedMetadata.containsKey("technology")
                        ? EServiceTechnology.valueOf(requestedMetadata.get("technology"))
                        : initialEService.getTechnology())
                .mode(requestedMetadata.containsKey("mode")
                        ? EServiceMode.valueOf(requestedMetadata.get("mode"))
                        : initialEService.getMode())
                .isSignalHubEnabled(requestedBoolean("signalHubEnabled", initialEService.getIsSignalHubEnabled()))
                .isConsumerDelegable(requestedBoolean("consumerDelegable", initialEService.getIsConsumerDelegable()))
                .isClientAccessDelegable(requestedBoolean("clientAccessDelegable", initialEService.getIsClientAccessDelegable()))
                .personalData(requestedBoolean("handlePersonalData", initialEService.getPersonalData()))
                .asyncExchange(requestedBoolean("asyncExchange", initialEService.getAsyncExchange()))
                .build();

        assertThat(actualEService)
                .as("E-service %s dopo la modifica dei metadati", alias)
                .isEqualTo(expectedEService);
    }

    @DataTableType
    public EServiceUpdateCommand eServiceUpdateCommand(DataTable dataTable) {
        EServiceUpdateCommand command = requestFactory.defaultUpdateEServiceCommand();
        requestedMetadata.clear();
        dataTable.cells().stream().skip(1).forEach(update -> {
            String field = update.get(0);
            String value = update.get(1);

            switch (field) {
                case "nome" -> {
                    String requestedName = RandomUtils.randomAlphanumericName(value, 10);
                    requestedMetadata.put(field, requestedName);
                    command.name(requestedName);
                }
                case "descrizione" -> {
                    requestedMetadata.put(field, value);
                    command.description(value);
                }
                case "technology" -> {
                    requestedMetadata.put(field, value);
                    command.technology(EServiceTechnology.valueOf(value));
                }
                case "mode" -> {
                    requestedMetadata.put(field, value);
                    command.mode(EServiceMode.valueOf(value));
                }
                case "signalHubEnabled" -> {
                    requestedMetadata.put(field, value);
                    command.isSignalHubEnabled(Boolean.parseBoolean(value));
                }
                case "consumerDelegable" -> {
                    requestedMetadata.put(field, value);
                    command.isConsumerDelegable(Boolean.parseBoolean(value));
                }
                case "clientAccessDelegable" -> {
                    requestedMetadata.put(field, value);
                    command.isClientAccessDelegable(Boolean.parseBoolean(value));
                }
                case "handlePersonalData" -> {
                    requestedMetadata.put(field, value);
                    command.handlePersonalData(Boolean.parseBoolean(value));
                }
                case "asyncExchange" -> {
                    requestedMetadata.put(field, value);
                    command.asyncExchange(Boolean.parseBoolean(value));
                }
                default -> throw new IllegalArgumentException(
                        "Campo metadato e-service non supportato: " + field
                );
            }
        });
        return command;
    }

    private Boolean requestedBoolean(String field, Boolean initialValue) {
        return requestedMetadata.containsKey(field)
                ? Boolean.valueOf(requestedMetadata.get(field))
                : initialValue;
    }
}
