package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.RadioButton;
import it.pagopa.infrastructure.suit.component.TextField;
import org.assertj.core.api.Assertions;

/**
 * Questa pagina rappresenta la pagina di creazione di una nuova delega per il cittadino, accessibile dalla sezione Deleghe.
 * La pagina contiene il form con i dati del delegato, gli enti, la scadenza e il codice di verifica da condividere.
 */
@Url("${url.notifiche.cittadino.add-deleghe}")
public interface NewDelegationPFPage extends Page {

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

    @XPath("//*[@id=\"cognome-label\"]")
    Readable<String> lastNameLabel();

    @XPath("//*[@id=\"cognome\"]")
    TextField lastNameInput();

    @XPath("//*[@id=\"codiceFiscale-label\"]")
    Readable<String> taxIdLabel();

    @XPath("//*[@id=\"codiceFiscale\"]")
    TextField taxIdInput();

    @XPath("//*[@id=\"selectEntities\"]")
    Readable<String> entitiesLabel();

    @XPath("//*[@id=\"tutti-gli-enti-selezionati\"]")
    RadioButton allEntitiesRadio();

    @XPath("//*[@id=\"enti-selezionati\"]")
    RadioButton selectedEntitiesRadio();

    @XPath("//*[@id=\"expirationDate-label\"]")
    Readable<String> expirationDateLabel();

    @XPath("//*[@id=\"expirationDate\"]")
    TextField expirationDateInput();

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

    @Override
    default void assertLoaded() {
        breadcrumbs().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Aggiungi una delega"));
        delegationsBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Deleghe"));
        subtitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Inserisci i dati della persona fisica o giuridica a cui vuoi delegare la lettura delle tue notifiche."));

        personType().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Soggetto giuridico:*"));
        Assertions.assertThat(naturalPersonRadio().getLabel()).isEqualTo("Persona fisica");
        Assertions.assertThat(legalPersonRadio().getLabel()).isEqualTo("Persona giuridica");

        firstNameLabel().readAndAssert(h -> Assertions.assertThat(h).contains("Nome"));
        Assertions.assertThat(firstNameInput().get(FindPolicy.PRESENT)).isPresent();
        lastNameLabel().readAndAssert(h -> Assertions.assertThat(h).contains("Cognome"));
        Assertions.assertThat(lastNameInput().get(FindPolicy.PRESENT)).isPresent();
        taxIdLabel().readAndAssert(h -> Assertions.assertThat(h).contains("Codice Fiscale"));
        Assertions.assertThat(taxIdInput().get(FindPolicy.PRESENT)).isPresent();

        entitiesLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Potrà consultare le notifiche da parte di:*"));
        Assertions.assertThat(allEntitiesRadio().getLabel()).isEqualTo("Tutti gli enti");
        Assertions.assertThat(selectedEntitiesRadio().getLabel()).isEqualTo("Solo enti selezionati");

        expirationDateLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Termine delega"));
        Assertions.assertThat(expirationDateInput().get(FindPolicy.PRESENT)).isPresent();

        verificationCode().readAndAssert(h -> Assertions.assertThat(h.replaceAll("\\s", "")).matches("\\d{5}"));
        copyVerificationCodeButton().assertLoaded();
        submitButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Invia la richiesta"));

        // labels
        currentBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Nuova delega"));
        requiredFieldsLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Campi obbligatori*"));
        personTypeHelper().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Seleziona la tipologia di soggetto giuridico"));
        entitiesHelper().readAndAssert(h -> Assertions.assertThat(h).startsWith("Seleziona un"));
        validityPeriodLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Periodo di validità della delega*"));
        verificationCodeTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Codice di verifica"));
        verificationCodeDescription().readAndAssert(h -> Assertions.assertThat(h).startsWith("Condividi questo codice con la persona delegata"));
    }
}
