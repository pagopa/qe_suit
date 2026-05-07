package it.pagopa.interop.controller;

import io.cucumber.java.en.When;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.domain.enums.Tenant;
import it.pagopa.interop.domain.enums.UserRole;

public class BrowserNavigationController {

    @When("l'utente {userRole} di {tenant} si trova alla pagina {page} del portale Interop")
    public void navigateToPage(UserRole user, Tenant tenant, Page page){

    }
}
