package it.pagopa.send.common.login.infrastructure.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.send.common.login.application.LoginUseCase;
import it.pagopa.send.common.user.domain.User;
import lombok.RequiredArgsConstructor;

/**
 * Step di accesso ai portali SEND e di sessione: dopo l'accesso il tipo di utente (PT, PA o PG) è quello della sessione
 * dello scenario, il canale quello indicato nel Gherkin.
 */
@RequiredArgsConstructor
public class LoginSteps {
    private final LoginUseCase loginUseCase;

    @Given("la PA {user} (ha )effettua(to) l'accesso a SelfCare con autenticazione SPID")
    public void spidAuthPa(User tenant) {
        loginUseCase.loginAsTenant(tenant);
    }

    @Given("il PT {user} (ha )effettua(to) l'accesso a SelfCare con autenticazione SPID")
    public void spidAuthPt(User partner) {
        // il PT entra da SEND come una PA: dopo la scelta dell'ente il portale mittente lo reindirizza al BackOffice PT
        loginUseCase.loginAsTenant(partner);
    }

    @Given("{recipientType} {user} (ha )effettua(to) l'accesso a SelfCare con autenticazione SPID")
    public void spidAuthRecipient(String recipientType, User recipient) {
        loginUseCase.loginAsRecipient(recipientType, recipient);
    }

    @Given("la PA {user} è loggata a SEND")
    public void forceTenantSession(User tenant) {
        loginUseCase.forceTenantSession(tenant);
    }

    @Given("{recipientType} {user} forza l'accesso a SelfCare tramite sessionStorage")
    public void forceRecipientSession(String recipientType, User recipient) {
        loginUseCase.forceRecipientSession(recipientType, recipient);
    }

    @And("se presente, viene saltata la configurazione del prodotto SEND")
    public void skipSendConfiguration() {
        loginUseCase.skipSendConfiguration();
    }

    @And("il PT ha accesso alle funzionalità del BackOffice PT")
    @And("la PA ha accesso alle funzionalità del portale ente mittente")
    @And("la PG ha accesso alle funzionalità del portale imprese")
    public void verifyPortalAccess() {
        loginUseCase.verifyPortalAccess();
    }

    @When("il PT richiede la disconnessione dal portale")
    @When("la PA richiede la disconnessione dal portale")
    @When("la PG richiede la disconnessione dal portale")
    public void logout() {
        loginUseCase.logout();
    }

    @Then("la sessione del PT risulta terminata")
    @Then("la sessione della PA risulta terminata")
    @Then("la sessione della PG risulta terminata")
    public void verifySessionEnded() {
        loginUseCase.verifySessionEnded();
    }
}
