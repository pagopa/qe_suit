package it.pagopa.send.common.login.infrastructure.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.send.common.login.application.LoginUseCase;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.user.domain.UserType;
import lombok.RequiredArgsConstructor;

/**
 * Step di accesso ai portali SEND e di sessione: l'utente si indica solo nell'accesso, gli step successivi si riferiscono
 * all'utente della sessione dello scenario (PT, PA o PG); il canale è quello indicato nel Gherkin.
 */
@RequiredArgsConstructor
public class LoginSteps {
    private final LoginUseCase loginUseCase;

    @Given("la PA {user} (ha )effettua(to) l'accesso a SelfCare con autenticazione SPID")
    public void spidAuthPa(User tenant) {
        loginUseCase.loginAsTenant(tenant);
    }

    @Given("il partner tecnologico: {user} (ha )effettua(to) (nuovamente )l'accesso a SelfCare con autenticazione SPID")
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

    @And("ha accesso alle funzionalità del BackOffice PT")
    @And("ha accesso alle funzionalità del portale ente mittente")
    @And("ha accesso alle funzionalità del portale imprese")
    public void verifyPortalAccess() {
        loginUseCase.verifyPortalAccess();
    }

    @When("richiede la disconnessione dal portale")
    public void logout() {
        loginUseCase.logout();
    }

    @Then("la sessione risulta terminata sul BackOffice PT")
    public void verifyPtSessionEnded() {
        loginUseCase.verifySessionEnded(UserType.PT);
    }

    @Then("la sessione risulta terminata sul portale ente mittente")
    public void verifyPaSessionEnded() {
        loginUseCase.verifySessionEnded(UserType.PA);
    }

    @Then("la sessione risulta terminata sul portale imprese")
    public void verifyPgSessionEnded() {
        loginUseCase.verifySessionEnded(UserType.PG);
    }
}
