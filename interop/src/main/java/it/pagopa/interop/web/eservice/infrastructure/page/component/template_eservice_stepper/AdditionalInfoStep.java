package it.pagopa.interop.web.eservice.infrastructure.page.component.template_eservice_stepper;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.*;

@XPath(".//form")
public interface AdditionalInfoStep extends Component {

    VersionDescriptionCard versionDescriptionCard();

    DocumentationCard documentationCard();

    RequestManagementCard requestManagementCard();

    @XPath(".//button[@type='button' and normalize-space()='Indietro' and .//*[@data-testid='ArrowBackIcon']]")
    Button previousStepButton();

    @XPath(".//button[@type='submit' and normalize-space()='Vai al riepilogo' and .//*[@data-testid='ArrowForwardIcon']]")
    Button goToSummaryButton();

    @XPath(".//div[contains(@class, 'MuiPaper-root') and .//h2[normalize-space()='Descrizione della versione']]")
    interface VersionDescriptionCard extends Card {
        @XPath(".//h2[contains(@class, 'MuiTypography-root') and normalize-space()='Descrizione della versione']")
        Label cardTitle();

        @XPath(".//label[@id='description-label']")
        Label descriptionLabel();

        @XPath(".//textarea[@id='description' and not(@aria-hidden='true')]")
        TextField descriptionInput();

        @XPath(".//span[@id='description-infoLabel']")
        Label descriptionHelperText();
    }

    @XPath(".//div[contains(@class, 'MuiPaper-root') and .//h2[normalize-space()='Documentazione']]")
    interface DocumentationCard extends Card {
        @XPath(".//h2[contains(@class, 'MuiTypography-root') and normalize-space()='Documentazione']")
        Label cardTitle();

        @XPath(".//p[contains(@class, 'MuiTypography-body2') and normalize-space()='Allega la documentazione tecnica utile ai fruitori degli e-service creati a partire da questo template.']")
        Label subtitle();

        @XPath(".//button[@data-testid='loadFromPc']")
        Button dropzoneAreaButton();

        @XPath(".//button[@data-testid='loadFromPc']//p[normalize-space()='Trascina qui il file o clicca qui per selezionarlo dal tuo computer']")
        Label dropzoneLabel();

        @XPath(".//button[@data-testid='loadFromPc']//button[normalize-space()='Carica il file']")
        Button uploadFileButton();

        @XPath(".//div[@data-testid='fileInput']//input[@type='file']")
        TextField fileInput();

        @XPath(".//span[contains(@class, 'MuiFormHelperText-root') and contains(normalize-space(), 'Puoi caricare solo file con estensione')]")
        Label allowedExtensionsHelperText();
    }

    @XPath(".//div[contains(@class, 'MuiPaper-root') and .//h2[normalize-space()='Gestione richieste']]")
    interface RequestManagementCard extends Card {

        @XPath(".//h2[contains(@class, 'MuiTypography-root') and normalize-space()='Gestione richieste']")
        Label cardTitle();

        Switch agreementApprovalPolicySwitch();

        @XPath(".//span[@id='agreementApprovalPolicy-label']")
        Label agreementApprovalPolicyLabel();
    }
}
