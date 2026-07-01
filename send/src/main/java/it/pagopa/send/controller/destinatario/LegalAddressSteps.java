package it.pagopa.send.controller.destinatario;

import io.cucumber.java.en.When;
import it.frontend.e2e.framework.core.model.selector.XPathSelector;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.send.domain.context.CurrentUserContext;
import it.pagopa.send.domain.web.pages.destinatario.AddressPage;
import it.pagopa.send.domain.web.pages.destinatario.DigitalDomicileOnboardPage;
import it.pagopa.send.domain.web.pages.destinatario.pf.AddressPFPage;
import it.pagopa.send.domain.web.pages.destinatario.pg.AddressPGPage;
import it.pagopa.send.services.VerificationCodeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LegalAddressSteps {
    private static final XPathSelector OTP_DIALOG = XPathSelector.of("//div[@role='dialog']");

    private final CurrentUserContext currentUserContext;
    private final WebPresentationGateway browser;
    private final VerificationCodeProvider verificationCodeProvider;

    @When("viene abilitato il domicilio digitale SERCQ utilizzando la seguente email: {string}")
    public void enableSercqDigitalDomicile(String email) {
        browser.bind(AddressPage.class).addLegalAddress();
        DigitalDomicileOnboardPage currentPage = browser.bind(DigitalDomicileOnboardPage.class);
        currentPage.startOnboarding(email);
        String otp = verificationCodeProvider.makeRequest(email);
        currentPage.submitOtp(otp);
        // Wait for the OTP dialog to disappear before continuing
        browser.waitUntilElementDisappears(OTP_DIALOG, 15);
        currentPage.continueToSummary();
        currentPage.activateDigitalDomicile();
    }

    @When("se presente viene disabilitato il domicilio digitale SERCQ")
    @When("viene disabilitato il domicilio digitale SERCQ")
    public void disableSercqDigitalDomicile() {
        String type = currentUserContext.getUser().getType();
        AddressPage addressPage;
        if (type.equals("PG")) {
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
