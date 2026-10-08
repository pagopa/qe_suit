package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.RadioButton;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import org.assertj.core.api.Assertions;

import java.util.Optional;

/**
 * {@code {baseUrl}/deleghe/nuova}
 * Pagina "Aggiungi una delega" del cittadino.
 * Si apre dal pulsante "Aggiungi una delega" della pagina {@code {baseUrl}/deleghe}.
 * Contiene il form con i dati del delegato, gli enti, la scadenza e il codice di verifica da condividere con il delegato.
 * L'assertLoaded verifica che la pagina sia caricata, cioè il titolo e la presenza degli input e dei pulsanti del form;
 * testi, stato iniziale e validazioni del form sono verificati da {@code WebNewDelegationPFContractTest}.
 */
@Url("${url.notifiche.cittadino.add-deleghe}")
public interface NewDelegationPFPage extends Page {

    Optional<OneTrustBanner> oneTrustBanner();

    @XPath("//*[@data-testid=\"titleBox\"]")
    Readable<String> breadcrumbs();

    @XPath("//*[@data-testid=\"breadcrumb-root-button\"]")
    Button delegationsBreadcrumb();

    @XPath("//*[@id=\"subtitle-page\"]")
    Readable<String> subtitle();

    @XPath("//*[@id=\"personType\"]")
    Readable<String> personType();

    @XPath("//*[@id=\"recipient-pf\"]")
    RadioButton naturalPersonRadio();

    @XPath("//*[@id=\"recipent-pg\"]")
    RadioButton legalPersonRadio();

    @XPath("//*[@id=\"nome-label\"]")
    Readable<String> firstNameLabel();

    @XPath("//*[@id=\"nome\"]")
    TextField firstNameInput();

    @XPath("//*[@id=\"nome-helper-text\"]")
    Readable<String> firstNameHelperText();

    @XPath("//*[@id=\"cognome-label\"]")
    Readable<String> lastNameLabel();

    @XPath("//*[@id=\"cognome\"]")
    TextField lastNameInput();

    @XPath("//*[@id=\"cognome-helper-text\"]")
    Readable<String> lastNameHelperText();

    @XPath("//*[@id=\"ragioneSociale-label\"]")
    Readable<String> businessNameLabel();

    @XPath("//*[@id=\"ragioneSociale\"]")
    TextField businessNameInput();

    @XPath("//*[@id=\"ragioneSociale-helper-text\"]")
    Readable<String> businessNameHelperText();

    @XPath("//*[@id=\"codiceFiscale-label\"]")
    Readable<String> taxIdLabel();

    @XPath("//*[@id=\"codiceFiscale\"]")
    TextField taxIdInput();

    @XPath("//*[@id=\"codiceFiscale-helper-text\"]")
    Readable<String> taxIdHelperText();

    @XPath("//*[@id=\"selectEntities\"]")
    Readable<String> entitiesLabel();

    @XPath("//*[@id=\"tutti-gli-enti-selezionati\"]")
    RadioButton allEntitiesRadio();

    @XPath("//*[@id=\"enti-selezionati\"]")
    RadioButton selectedEntitiesRadio();

    @XPath("//*[@id=\"enti-label\"]")
    Readable<String> selectedEntitiesLabel();

    @XPath("//*[@id=\"enti\"]")
    TextField selectedEntitiesInput();

    @XPath("//*[@id=\"enti\"]")
    Button selectedEntitiesDropdown();

    @XPath("//*[@id=\"enti-listbox\"]//*[@role=\"option\"]")
    Readable<String> selectedEntitiesOptions();

    @XPath("//*[@id=\"expirationDate-label\"]")
    Readable<String> expirationDateLabel();

    @XPath("//*[@id=\"expirationDate\"]")
    TextField expirationDateInput();

    @XPath("//*[@id=\"expirationDate-helper-text\"]")
    Readable<String> expirationDateHelperText();

    @XPath("//*[@data-testid=\"verificationCode\"]")
    Readable<String> verificationCode();

    @XPath("//*[@data-testid=\"verificationCode\"]//button")
    Button copyVerificationCodeButton();

    @XPath("//*[@data-testid=\"createButton\"]")
    Button submitButton();

    // labels

    @XPath("//*[@data-testid=\"breadcrumb-root-button\"]/ancestor::ol/li[last()]")
    Readable<String> currentBreadcrumb();

    @XPath("//*[@id=\"page-header-container\"]/following-sibling::p[1]")
    Readable<String> requiredFieldsLabel();

    @XPath("//*[@id=\"personType\"]/following-sibling::p[1]")
    Readable<String> personTypeHelper();

    @XPath("//*[@id=\"selectEntities\"]/following-sibling::p[1]")
    Readable<String> entitiesHelper();

    @XPath("//*[@id=\"expirationDate-label\"]/preceding::p[1]")
    Readable<String> validityPeriodLabel();

    @XPath("//*[@data-testid=\"verificationCode\"]/preceding::p[2]")
    Readable<String> verificationCodeTitle();

    @XPath("//*[@data-testid=\"verificationCode\"]/preceding::p[1]")
    Readable<String> verificationCodeDescription();

    default String getFirstNameErrorMessage() {
        return firstNameHelperText().read();
    }

    default String getLastNameErrorMessage() {
        return lastNameHelperText().read();
    }

    default String getBusinessNameErrorMessage() {
        return businessNameHelperText().read();
    }

    default String getTaxIdErrorMessage() {
        return taxIdHelperText().read();
    }

    default String getExpirationDateErrorMessage() {
        return expirationDateHelperText().read();
    }

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::acceptIfShown);
        breadcrumbs().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Aggiungi una delega"));
        Assertions.assertThat(naturalPersonRadio().radio().get(FindPolicy.PRESENT)).isPresent();
        Assertions.assertThat(legalPersonRadio().radio().get(FindPolicy.PRESENT)).isPresent();
        Assertions.assertThat(firstNameInput().get(FindPolicy.PRESENT)).isPresent();
        Assertions.assertThat(lastNameInput().get(FindPolicy.PRESENT)).isPresent();
        Assertions.assertThat(taxIdInput().get(FindPolicy.PRESENT)).isPresent();
        Assertions.assertThat(allEntitiesRadio().radio().get(FindPolicy.PRESENT)).isPresent();
        Assertions.assertThat(selectedEntitiesRadio().radio().get(FindPolicy.PRESENT)).isPresent();
        Assertions.assertThat(expirationDateInput().get(FindPolicy.PRESENT)).isPresent();
        copyVerificationCodeButton().assertLoaded();
        submitButton().assertLoaded();
    }
}
