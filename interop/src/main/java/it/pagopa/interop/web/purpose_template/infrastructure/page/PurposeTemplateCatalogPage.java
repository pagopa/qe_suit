package it.pagopa.interop.web.purpose_template.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.RadioButton;

/**
 * Page Object per la pagina di elenco dei Template di Finalità: è il punto di
 * ingresso da cui un Ente Erogatore avvia la creazione di un nuovo template
 * (bottone "Crea nuovo" + form di conferma con la selezione del trattamento
 * di dati personali).
 */
@Url("${interop.web.template-purpose}")
public interface PurposeTemplateCatalogPage extends Page {

    @XPath(".//button[contains(., 'Crea nuovo')]")
    Button createNewButton();

    @XPath(".//label[.//input[@name='personalData' and @value='true']]")
    RadioButton personalDataYesOption();

    @XPath(".//label[.//input[@name='personalData' and @value='false']]")
    RadioButton personalDataNoOption();

    @XPath(".//button[@type='submit' and contains(., 'Conferma')]")
    Button confirmButton();

    default PurposeTemplateCatalogPage openCreationForm() {
        createNewButton().click();
        return this;
    }

    default PurposeTemplateCatalogPage setPersonalData(boolean personalData) {
        if (personalData) personalDataYesOption().select();
        else personalDataNoOption().select();
        return this;
    }

    default void confirmCreation() {
        confirmButton().click();
    }
}

