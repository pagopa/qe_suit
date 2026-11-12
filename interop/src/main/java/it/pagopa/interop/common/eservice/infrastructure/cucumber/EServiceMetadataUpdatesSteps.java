package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.eservice.application.EServiceRequestFactory;
import it.pagopa.interop.common.eservice.application.command.EServiceUpdateCommand;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.utils.RandomUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EServiceMetadataUpdatesSteps {

    private final InteropJourney interopJourney;
    private final EServiceRequestFactory requestFactory;

    @When("un {userRole} di/del {tenant} richiede la modifica dei metadati dell'e-service {string}:")
    public void updateMetadata(UserRole role, Tenant producer, String alias, EServiceUpdateCommand updateCommand) {
        updateCommand.alias(alias);
        interopJourney
                .withProducer(producer, role)
                .createEService()
                .updateEService(updateCommand);
    }

    @DataTableType
    public EServiceUpdateCommand eServiceUpdateCommand(DataTable dataTable) {
        EServiceUpdateCommand command = requestFactory.defaultUpdateEServiceCommand();
        dataTable.cells().stream().skip(1).forEach(update -> {
            String field = update.get(0);
            String value = update.get(1);

            switch (field) {
                case "nome" -> command.name(RandomUtils.randomAlphanumericName(value, 10));
                case "descrizione" -> command.description(value);
                case "technology" -> command.technology(EServiceTechnology.valueOf(value));
                case "mode" -> command.mode(EServiceMode.valueOf(value));
                case "signalHubEnabled" -> command.isSignalHubEnabled(Boolean.parseBoolean(value));
                case "consumerDelegable" -> command.isConsumerDelegable(Boolean.parseBoolean(value));
                case "clientAccessDelegable" -> command.isClientAccessDelegable(Boolean.parseBoolean(value));
                case "handlePersonalData" -> command.handlePersonalData(Boolean.parseBoolean(value));
                case "asyncExchange" -> command.asyncExchange(Boolean.parseBoolean(value));
                default -> throw new IllegalArgumentException(
                        "Campo metadato e-service non supportato: " + field
                );
            }
        });
        return command;
    }
}
