package it.pagopa.interop.web.agreement.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.interop.common.eservice.domain.EService;
import org.openqa.selenium.Keys;

import java.util.NoSuchElementException;

@Url("${interop.web.agreement-detail-erogatore}/${agreementId}")
public interface AgreementDetailErogatorePage extends Page {

    @XPath(".//h1[normalize-space()='Gestisci richiesta di fruizione']")
    Readable<String> pageTitle();

    @Override
    default void assertLoaded(){
        String title = pageTitle().read();

        if (title == null || title.isBlank()) {
            throw new NoSuchElementException(
                    "Could not load the page : page title 'Gestisci richiesta di fruizione' is not present."
            );
        }
    }

    default void assertNotLoaded() {
        boolean loaded;
        try {
            assertLoaded();
            loaded = true;
        } catch (NoSuchElementException e) {
            loaded = false;
        }

        if (loaded) {
            throw new AssertionError(
                    "Expected page NOT to be loaded, but assertLoaded() succeeded for " + this.getClass().getName()
            );
        }
    }
}

