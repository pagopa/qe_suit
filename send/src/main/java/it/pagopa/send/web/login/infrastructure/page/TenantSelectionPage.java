package it.pagopa.send.web.login.infrastructure.page;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Writable;
import it.pagopa.send.web.login.infrastructure.page.AbstractComunePickerPage;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import it.pagopa.infrastructure.suit.component.Button;

import java.util.Optional;

public interface TenantSelectionPage extends AbstractComunePickerPage {

    @XPath("//*[@id=\"search\"]")
    Writable<String> searchFor();

    @XPath("//*[@id=\"root\"]/div/div[2]/div[2]/div/div[2]/div/div/div/div[2]/div/div/div/div")
    Clickable tenantRow();

    // Selfcare ha rinominato il pulsante da "Login" ad "Accedi": si accettano entrambi finché tutti gli ambienti non sono allineati
    @XPath(".//button[normalize-space()='Accedi' or normalize-space()='Login']")
    Button confirmBtn();

    // dopo la scelta dell'ente si atterra sulla Panoramica di Area Riservata: si apre il prodotto SEND dell'ambiente
    @XPath("${selettore.login.pa.area-riservata-selettore-env}")
    Button sendProductButton();

    Optional<OneTrustBanner> oneTrustBanner();

    @Override
    default void selectComune(String tenant) {
        oneTrustBanner().ifPresent(OneTrustBanner::acceptIfShown);
        searchFor().writeAndAssert(tenant);
        tenantRow().click();
        confirmBtn().click();
        sendProductButton().click();
    }
}
