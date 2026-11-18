package it.pagopa.interop.web.eservice.infrastructure.suit.component.additional_info_form;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Uploadable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.FormHelperText;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Switch;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.utils.async.PollingUtils;

@XPath(".//form[.//button[contains(normalize-space(.), 'Vai al riepilogo')]]")
public interface EServiceTemplateAdditionalInfoForm extends Component {

    @XPath(".//h2[normalize-space(.)='Descrizione della versione']")
    Label versionDescriptionTitle();

    @XPath(".//textarea[@name='description' and not(@aria-hidden='true')]/ancestor::div[contains(@class, 'MuiTextField-root')][1]//label")
    Label versionDescriptionLabel();

    @XPath(".//textarea[@name='description' and not(@aria-hidden='true')]")
    TextField versionDescriptionInput();

    @XPath(".//textarea[@name='description' and not(@aria-hidden='true')]/ancestor::div[contains(@class, 'MuiTextField-root')][1]//span[contains(@class, 'MuiFormHelperText-root') and not(contains(@class, 'Mui-error'))]")
    FormHelperText versionDescriptionHelperText();

    @XPath(".//h2[normalize-space(.)='Documentazione']")
    Label documentationTitle();

    @XPath(".//h2[normalize-space(.)='Documentazione']/following-sibling::p[1]")
    Label documentationDescription();

    @XPath(".//h2[normalize-space(.)='Documentazione']/ancestor::*[contains(@class, 'MuiPaper-root')][1]//label[contains(normalize-space(.), 'Nome documento')]")
    Label documentNameLabel();

    @XPath(".//h2[normalize-space(.)='Documentazione']/ancestor::*[contains(@class, 'MuiPaper-root')][1]//label[contains(normalize-space(.), 'Nome documento')]/ancestor::div[contains(@class, 'MuiTextField-root')][1]//input")
    TextField documentNameInput();

    @XPath(".//h2[normalize-space(.)='Documentazione']/ancestor::*[contains(@class, 'MuiPaper-root')][1]//input[@type='file']")
    Uploadable documentUploadInput();

    @XPath(".//button[contains(normalize-space(.), 'Salva documento')]")
    Button saveDocumentButton();

    @XPath(".//button[contains(normalize-space(.), 'Aggiungi un altro file')]")
    Button addAnotherFileButton();

    @XPath(".//h2[normalize-space(.)='Gestione richieste']")
    Label requestManagementTitle();

    @XPath(".//h2[normalize-space(.)='Gestione richieste']/ancestor::*[contains(@class, 'MuiPaper-root')][1]//span[contains(@class, 'MuiSwitch-root')]")
    Switch manualApprovalSwitch();

    @XPath(".//h2[normalize-space(.)='Gestione richieste']/ancestor::*[contains(@class, 'MuiPaper-root')][1]//label[.//span[contains(@class, 'MuiSwitch-root')]]")
    Label manualApprovalLabel();

    @XPath(".//button[contains(normalize-space(.), 'Indietro')]")
    Button backButton();

    @XPath(".//button[contains(normalize-space(.), 'Vai al riepilogo')]")
    Button goToSummaryButton();

    @Override
    default void assertLoaded() {
        PollingUtils.pollUntil(
                () -> versionDescriptionInput().get(FindPolicy.PRESENT).isPresent()
                        && goToSummaryButton().get(FindPolicy.VISIBLE).isPresent(),
                Boolean::booleanValue,
                "Informazioni aggiuntive template e-service non caricate"
        );
    }
}

