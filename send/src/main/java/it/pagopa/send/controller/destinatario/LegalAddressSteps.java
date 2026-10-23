package it.pagopa.send.controller.destinatario;

import io.cucumber.java.en.When;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.send.common.user.domain.UserType;
import it.pagopa.send.web.infrastructure.cucumber.WebBrowserContext;
import it.pagopa.send.web.infrastructure.page.AddressPage;
import it.pagopa.send.web.infrastructure.page.DigitalDomicileOnboardPage;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.AddressPFPage;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.AddressPGPage;
import it.pagopa.send.services.VerificationCodeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class LegalAddressSteps {
    private final WebBrowserContext webBrowserContext;
    private final WebPresentationGateway browser;
    private final VerificationCodeProvider verificationCodeProvider;

    @When("viene abilitato il domicilio digitale SERCQ utilizzando la seguente email: {string}")
    public void enableSercqDigitalDomicile(String email) {
        browser.bind(AddressPage.class).addLegalAddress();
        DigitalDomicileOnboardPage currentPage = browser.bind(DigitalDomicileOnboardPage.class);
        currentPage.startOnboarding(email);
        String otp = verificationCodeProvider.makeRequest(email);
        currentPage.submitOtp(otp);
        currentPage.continueToSummary();
        currentPage.activateDigitalDomicile();
    }

    @When("se presente viene disabilitato il domicilio digitale SERCQ")
    @When("viene disabilitato il domicilio digitale SERCQ")
    public void disableSercqDigitalDomicile() {
        // l'utente è quello del login: LoginService lo imposta in WebBrowserContext
        UserType type = webBrowserContext.getCurrentUser().getType();
        AddressPage addressPage;
        if (type == UserType.PG) {
            addressPage = browser.bind(AddressPGPage.class);
            addressPage.navigateTo();
            ((AddressPGPage) addressPage).assertLoaded();
        } else {
            addressPage = browser.bind(AddressPFPage.class);
            addressPage.navigateTo();
            ((AddressPFPage) addressPage).assertLoaded();
        }
        addressPage.removeDigitalDomicile();
    }
}
