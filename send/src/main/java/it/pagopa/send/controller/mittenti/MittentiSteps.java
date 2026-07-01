package it.pagopa.send.controller.mittenti;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.utils.FakeAuthenticator;
import it.pagopa.send.utils.IAuthenticator;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MittentiSteps {

    private final WebPresentationGateway browser;

    @Given("l'utente è un {string} di {string}")
    public void login(String role, String pa) {
        //Inizializzare un bean Auth
        //Auth auth = Auth.of(role,pa,fake);
        IAuthenticator auth = new FakeAuthenticator(role, pa);
        Assertions.assertTrue(auth.isAuthenticated());
    }



}
