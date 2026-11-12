package it.pagopa.interop.web.riskanalysis.infrastructure.suit.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Link;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import it.pagopa.utils.async.PollingUtils;

@Url("${interop.web.base-url}/analisi-del-rischio/${riskAnalysisId}/approvazione")
public interface RiskAnalysisApprovalPage extends Page {

    @XPath(".//a[contains(., 'Torna alle analisi del rischio')]")
    Button backToEServicesButton();

    Breadcrumbs breadcrumbs();

    @XPath(".//h1")
    Label pageTitle();

    @XPath(".//div[contains(@class, 'MuiAlert-message')]")
    Label alertMessage();

    @XPath(".//button[contains(., 'Rifiuta ')]")
    Button rejectButton();

    @XPath(".//button[contains(., 'Approva ')]")
    Button approveButton();

    @XPath(".//div[contains(@class, 'MuiAccordion-root')][.//h6[text()='Informazioni generali']]")
    GeneralInfoCard informazioniGeneraliCard();

    @XPath(".//div[contains(@class, 'MuiAccordion-root')][.//h6[text()='Analisi del rischio']]")
    RiskAnalysisCard analisiRischioCard();

    interface GeneralInfoCard extends Component {

        @XPath(".//div[contains(@class, 'MuiAccordionSummary-content')]//h6[contains(@class, 'MuiTypography-h6')]")
        Label accordionTitle();

        @XPath(".//p[normalize-space(.)='Nome della finalità']")
        Label purposeNameLabel();

        @XPath(".//p[normalize-space(.)='Nome della finalità']/parent::div/following-sibling::div[1]")
        Label purposeNameValue();

        @XPath(".//p[normalize-space(.)='Descrizione']")
        Label descriptionLabel();

        @XPath(".//p[normalize-space(.)='Descrizione']/parent::div/following-sibling::div[1]")
        Label descriptionValue();

        @XPath(".//p[normalize-space(.)='E-service associato']")
        Label eServiceLabel();

        @XPath(".//p[normalize-space(.)='E-service associato']/parent::div/following-sibling::div[1]//a")
        Link eServiceLink();

        @XPath(".//p[normalize-space(.)='Erogatore']")
        Label providerLabel();

        @XPath(".//p[normalize-space(.)='Erogatore']/parent::div/following-sibling::div[1]")
        Label providerValue();

        @XPath(".//h3[text()='Stima e soglie di chiamate API']")
        Label thresholdSubtitle();

        @XPath(".//p[normalize-space(.)='Stima richiesta dal fruitore']")
        Label estimatedRequestLabel();

        @XPath(".//p[normalize-space(.)='Stima richiesta dal fruitore']/parent::div/following-sibling::div[1]")
        Label estimatedRequestValue();

        @XPath(".//p[normalize-space(.)=\"Soglia per fruitore prevista dall'erogatore\"]")
        Label thresholdLabel();

        @XPath(".//p[normalize-space(.)=\"Soglia per fruitore prevista dall'erogatore\"]/parent::div/following-sibling::div[1]")
        Label thresholdValue();

        @XPath(".//p[normalize-space(.)=\"Soglia totale prevista dall'erogatore\"]")
        Label totalThresholdLabel();

        @XPath(".//p[normalize-space(.)=\"Soglia totale prevista dall'erogatore\"]/parent::div/following-sibling::div[1]")
        Label totalThresholdValue();

        @XPath(".//h3[text()='Assegnazione']")
        Label assignmentSubtitle();

        @XPath(".//p[normalize-space(.)='Data assegnazione']")
        Label assignmentDateLabel();

        @XPath(".//p[normalize-space(.)='Data assegnazione']/parent::div/following-sibling::div[1]")
        Label assignmentDateValue();

        @XPath(".//p[normalize-space(.)='Assegnata a']")
        Label assignedToLabel();

        @XPath(".//p[normalize-space(.)='Assegnata a']/parent::div/following-sibling::div[1]")
        Label assignedToValue();
    }

    interface RiskAnalysisCard extends Component {
        @XPath(".//div[contains(@class, 'MuiAccordionSummary-content')]//h6[contains(@class, 'MuiTypography-h6')]")
        Label accordionTitle();

        @XPath(".//section//h3[contains(normalize-space(.), 'Analisi del rischio')]")
        Label title();

        @XPath(".//p[contains(normalize-space(.), 'Indicare per quale finalità si intende accedere ai dati')]")
        Label purposeQuestionLabel();

        @XPath(".//li[.//p[contains(normalize-space(.), 'Indicare per quale finalità si intende accedere ai dati')]]//p[contains(normalize-space(.), 'Indicare per quale finalità si intende accedere ai dati')]/following-sibling::p[1]")
        Label purposeAnswerLabel();

        @XPath(".//li[.//p[contains(normalize-space(.), 'Indicare per quale finalità si intende accedere ai dati')]]//p[contains(normalize-space(.), 'Indicare per quale finalità si intende accedere ai dati')]/following-sibling::p[2]")
        Label purposeNoteLabel();

        @XPath(".//p[contains(normalize-space(.), 'Specificare il fine perseguito per fini istituzionali')]")
        Label specificPurposeQuestionLabel();

        @XPath(".//li[.//p[contains(normalize-space(.), 'Specificare il fine perseguito per fini istituzionali')]]//p[contains(normalize-space(.), 'Specificare il fine perseguito per fini istituzionali')]/following-sibling::p[1]")
        Label specificPurposeAnswerLabel();

        @XPath(".//p[contains(normalize-space(.), 'Indicare se si accede a dati personali')]")
        Label personalDataQuestionLabel();

        @XPath(".//li[.//p[contains(normalize-space(.), 'Indicare se si accede a dati personali')]]//p[contains(normalize-space(.), 'Indicare se si accede a dati personali')]/following-sibling::p[1]")
        Label personalDataAnswerLabel();

        @XPath(".//li[.//p[contains(normalize-space(.), 'Indicare se si accede a dati personali')]]//p[contains(normalize-space(.), 'Indicare se si accede a dati personali')]/following-sibling::p[2]")
        Label personalDataNoteLabel();

        @XPath(".//p[contains(normalize-space(.), 'Indicare se si accede a dati non personali di terzi')]")
        Label thirdPartyDataQuestionLabel();

        @XPath(".//li[.//p[contains(normalize-space(.), 'Indicare se si accede a dati non personali di terzi')]]//p[contains(normalize-space(.), 'Indicare se si accede a dati non personali di terzi')]/following-sibling::p[1]")
        Label thirdPartyDataAnswerLabel();
    }

    @Override
    default void assertLoaded() {
        PollingUtils.pollUntil(
                () -> pageTitle().read(),
                text -> text != null && !text.isBlank(),
                "Risk Analysis Approval page is not loaded or its title is absent"
        );
    }
}