package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EServiceMetadataUpdatesSteps {

    private final InteropJourney interopJourney;

    @When("un {userRole} di/del {tenant} richiede la modifica dei metadati dell'e-service {string}:")
    public void updateMetadata(UserRole role, Tenant producer, String alias, DataTable dataTable) {

        interopJourney
                .withProducer(producer, role)
                .createEService()
                .updateEService(cfg -> {
                    cfg.alias(alias);
                    dataTable.asMaps(String.class, String.class).forEach(update -> {
                        String field = update.get("campo");
                        String value = update.get("valore");

                        switch (field) {
                            case "nome" -> cfg.name(value);
                            case "descrizione" -> cfg.description(value);
                            case "technology" -> cfg.technology(EServiceTechnology.valueOf(value));
                            case "mode" -> cfg.mode(EServiceMode.valueOf(value));
                            case "signalHubEnabled" -> cfg.isSignalHubEnabled(Boolean.getBoolean(value));
                            case "consumerDelegable" -> cfg.isConsumerDelegable(Boolean.getBoolean(value));
                            case "clientAccessDelegable" -> cfg.isClientAccessDelegable(Boolean.getBoolean(value));
                            case "handlePersonalData" -> cfg.handlePersonalData(Boolean.getBoolean(value));
                            case "asyncExchange" -> cfg.asyncExchange(Boolean.getBoolean(value));
                            default -> throw new IllegalArgumentException(
                                    "Campo metadato e-service non supportato: " + field
                            );
                        }
                    });
                });
    }
}
