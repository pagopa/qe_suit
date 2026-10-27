package it.pagopa.send.web.address.infrastructure;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.send.common.address.application.AddressGateway;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.user.domain.UserType;
import it.pagopa.send.services.VerificationCodeProvider;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.AddressPFPage;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.AddressPGPage;
import it.pagopa.send.web.infrastructure.page.AddressPage;
import it.pagopa.send.web.infrastructure.page.DigitalDomicileOnboardPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebAddressGateway implements AddressGateway {
    private final WebPresentationGateway browser;
    private final CurrentUserSession currentUserSession;
    private final VerificationCodeProvider verificationCodeProvider;

    @Override
    public void enableSercqDigitalDomicile(String email) {
        browser.bind(AddressPage.class).addLegalAddress();
        DigitalDomicileOnboardPage page = browser.bind(DigitalDomicileOnboardPage.class);
        page.startOnboarding(email);
        page.submitOtp(verificationCodeProvider.makeRequest(email));
        page.continueToSummary();
        page.activateDigitalDomicile();
    }

    @Override
    public void disableSercqDigitalDomicile() {
        currentAddressPage().removeDigitalDomicile();
    }

    @Override
    public void removeCourtesyAddress() {
        browser.bind(AddressPage.class).removeCourtesyAddress();
    }

    // la pagina dei recapiti dipende dal tipo dell'utente del login
    private AddressPage currentAddressPage() {
        if (currentUserSession.getUser().getType() == UserType.PG) {
            AddressPGPage page = browser.bind(AddressPGPage.class);
            page.navigateTo();
            page.assertLoaded();
            return page;
        }
        AddressPFPage page = browser.bind(AddressPFPage.class);
        page.navigateTo();
        page.assertLoaded();
        return page;
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.WEB_BROWSER;
    }
}
