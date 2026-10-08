package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import lombok.RequiredArgsConstructor;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.infrastructure.context.CucumberEntityStore;
import it.pagopa.interop.common.eservice.application.EServiceUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.kernel.domain.Tenant;

import java.util.Locale;


@RequiredArgsConstructor
public class EServiceSteps {
    private final EServiceUseCase eServiceUseCase;
    private Tenant tenant;
    private String name;
    private String description;
    private EServiceMode mode;
    private Boolean asyncExchange;
    private EServiceTechnology technology;
    private Boolean personalData;

    @Given("il {tenant} che intraprende la creazione di un nuovo e-service erogatore")
    public void beginEServiceCreation( Tenant tenant) {
        this.name = "Nome dell'eservice di test";
        this.description = "Descrizione dell'eservice di test";
        this.mode = EServiceMode.DELIVER;
        this.tenant = tenant;
    }

    @When("seleziona la modalità di scambio dei dati {string} per l'e-service")
    public void selectExchangeMode(String exchangeMode) {
        asyncExchange = switch (exchangeMode.toLowerCase(Locale.ROOT)) {
            case "sincrono" -> false;
            case "asincrono" -> true;
            default -> throw new IllegalArgumentException("Modalità di scambio non supportata: " + exchangeMode);
        };
    }

    @When("seleziona la tecnologia dell'API {string}")
    public void selectTechnology(String apiTechnology) {
        technology = switch (apiTechnology.toUpperCase(Locale.ROOT)) {
            case "REST" -> EServiceTechnology.REST;
            case "SOAP" -> EServiceTechnology.SOAP;
            default -> throw new IllegalArgumentException("Tecnologia API non supportata: " + apiTechnology);
        };
    }

    @When("seleziona la gestione dei dati personali {string}")
    public void selectPersonalDataHandling(String handling) {
        personalData = switch (handling.toLowerCase(Locale.ROOT)) {
            case "si" -> true;
            case "no" -> false;
            default -> throw new IllegalArgumentException(
                    "Valore di gestione dei dati personali non supportato: " + handling
            );
        };
    }

    @Then("l'utente completa la creazione dell'e-service")
    public void completeEServiceCreation() {
        EService createdEService =
                eServiceUseCase.createEService(configure -> {
                    configure.name(name);
                    configure.description(description);
                    configure.mode(mode);
                    configure.isAsync(asyncExchange);
                    configure.technology(technology);
                    configure.handlePersonalData(personalData);
                });
        eServiceUseCase.shouldCreateEServiceCorrectly(createdEService);
    }
}
