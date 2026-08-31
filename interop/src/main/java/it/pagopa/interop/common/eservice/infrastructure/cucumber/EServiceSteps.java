package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.java.PendingException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.eservice.application.EServiceUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.infrastructure.context.cucumber.UserContext;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EServiceSteps {
    private final EServiceUseCase eServiceUseCase;
    private final CurrentUserSession userContext;

    @When("{tenant} crea una nuova versione del {currentEService}")
    public void createNewEserviceVersion(Tenant tenant, EService eService){
        userContext.set(User.getTenantAdmin(tenant), tenant);
        eServiceUseCase.addDescriptor(eService);
    }

    @Given("il {tenant} consulta la pagina dell'eservice")
    public void ilPotenzialeFruitoreConsultaLaPaginaDellEservice(Tenant potenzialeFruitore) {
        // Write code here that turns the phrase above into concrete actions
        System.out.println("il fruitore " + potenzialeFruitore + " fa cose ...");
    }

    @Then("il {tenant} trova il pulsante di richiesta di fruizione disabilitato per tutte le versioni antecedenti l'ultima")
    public void ilPotenzialeFruitoreTrovaIlPulsanteDiRichiestaDiFruizioneDisabilitatoPerTutteLeVersioniAntecedentiLUltima(Tenant potenzialeFruitore) {
        // Write code here that turns the phrase above into concrete actions
        System.out.println("il fruitore " + potenzialeFruitore + " fa cose ...");
    }
}
