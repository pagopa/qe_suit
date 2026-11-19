package it.pagopa.interop.web.reviewer_riskanalysis.infrastructure.suit.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Alert;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Checkbox;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Link;
import it.pagopa.infrastructure.suit.component.RadioGroup;
import it.pagopa.infrastructure.suit.component.Switch;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import it.pagopa.utils.async.PollingUtils;

import java.util.List;

/**
 * Reviewer questionnaire organized by its rendered question cards.
 * Conditional cards are resolved only when accessed.
 */
@Url("${interop.web.base-url}/analisi-del-rischio/${riskAnalysisId}/compilazione")
public interface RiskAnalysisCompilationPage extends Page {

    @XPath(".//h1[normalize-space()='Compilazione analisi del rischio']")
    Label title();

    @XPath(".//a[normalize-space()='Torna alle analisi del rischio']")
    Link backToRiskAnalysesLink();

    Breadcrumbs breadcrumbs();

    @XPath(".//p[normalize-space()='*Campi obbligatori']")
    Label requiredFieldsNotice();

    @XPath(".//form//div[@role='alert' and contains(@class, 'MuiAlert-standardWarning')]")
    Alert personalDataWarning();

    @XPath(".//form//section[.//h2[normalize-space()='Analisi del rischio']]")
    IntroductionCard introductionCard();

    @XPath(".//form//section[.//*[@id='answers.purpose-label']]")
    RadioQuestionCard purposeCard();

    @XPath(".//form//section[.//*[@id='answers.institutionalPurpose-label']]")
    TextBoxQuestionCard institutionalPurposeCard();

    @XPath(".//form//section[.//*[@id='answers.usesPersonalData-label']]")
    RadioQuestionCard usesPersonalDataCard();

    @XPath(".//form//section[.//*[@id='answers.personalDataTypes-label']]")
    CheckboxQuestionCard personalDataTypesCard();

    @XPath(".//form//section[.//*[@id='answers.otherPersonalDataTypes-label']]")
    TextBoxQuestionCard otherPersonalDataTypesCard();

    @XPath(".//form//section[.//*[@id='answers.legalBasis-label']]")
    CheckboxQuestionCard legalBasesCard();

    @XPath(".//form//section[.//*[@id='answers.legalObligationReference-label']]")
    TextBoxQuestionCard legalObligationReferenceCard();

    @XPath(".//form//section[.//*[@id='answers.legitimateInterestReference-label']]")
    TextBoxQuestionCard legitimateInterestReferenceCard();

    @XPath(".//form//section[.//*[@id='answers.legalBasisPublicInterest-label']]")
    RadioQuestionCard publicInterestCard();

    @XPath(".//form//section[.//*[@id='answers.ruleOfLawText-label']]")
    TextBoxQuestionCard ruleOfLawCard();

    @XPath(".//form//section[.//*[@id='answers.knowsDataQuantity-label']]")
    RadioQuestionCard dataQuantityCard();

    @XPath(".//form//section[.//*[@id='answers.deliveryMethod-label']]")
    DeliveryMethodCard deliveryMethodCard();

    @XPath(".//form//section[.//*[@id='answers.policyProvided-label']]")
    RadioQuestionCard policyProvidedCard();

    @XPath(".//form//section[.//*[@id='answers.policyProvidedMedium-label']]")
    RadioQuestionCard policyMediumCard();

    @XPath(".//form//section[.//*[@id='answers.confirmPricipleIntegrityAndDiscretion-label']]")
    SwitchQuestionCard integrityAndDiscretionCard();

    @XPath(".//form//section[.//*[@id='answers.doneDpia-label']]")
    RadioQuestionCard dpiaCard();

    @XPath(".//form//section[.//*[@id='answers.dataDownload-label']]")
    RadioQuestionCard dataDownloadCard();

    @XPath(".//form//section[.//*[@id='answers.purposePursuit-label']]")
    RadioQuestionCard purposePursuitCard();

    @XPath(".//form//section[.//*[@id='answers.checkedExistenceMereCorrectnessInteropCatalogue-label']]")
    SwitchQuestionCard catalogueCheckCard();

    @XPath(".//form//section[.//*[@id='answers.declarationConfirmGDPR-label']]")
    SwitchQuestionCard gdprDeclarationCard();

    @XPath(".//form//button[normalize-space()='Indietro']")
    Button backButton();

    @XPath(".//form//button[@type='submit' and normalize-space()='Salva bozza e prosegui']")
    Button saveDraftAndContinueButton();

    @Override
    default void assertLoaded() {
        PollingUtils.pollUntil(
                () -> title().read(),
                text -> text != null && !text.isBlank(),
                "Risk Analysis compilation page is not loaded or its title is absent"
        );
    }

    interface IntroductionCard extends Component {

        @XPath(".//h2")
        Label title();

        @XPath(".//p[starts-with(normalize-space(), 'Le domande del questionario varieranno')]")
        Label description();

        @XPath(".//p[normalize-space()='Trattamento dati personali']")
        Label personalDataTreatmentLabel();

        @XPath(".//p[normalize-space()='Trattamento dati personali']/parent::div/following-sibling::div")
        Label personalDataTreatmentValue();

    }

    interface QuestionCard extends Component {

        @XPath(".//*[@id and substring(@id, string-length(@id) - 5) = '-label']")
        Label question();
    }

    interface RadioQuestionCard extends QuestionCard {

        @XPath(".//div[@role='radiogroup']")
        RadioGroup answers();
    }

    interface TextBoxQuestionCard extends QuestionCard {

        @XPath(".//input[@type='text']")
        TextField field();
    }

    interface CheckboxQuestionCard extends QuestionCard {

        @XPath(".//span[contains(@class, 'MuiCheckbox-root')]")
        List<LabelledCheckbox> answers();
    }

    interface SwitchQuestionCard extends QuestionCard {

        @XPath(".//span[contains(@class, 'MuiSwitch-root')]")
        Switch confirmation();
    }

    interface DeliveryMethodCard extends QuestionCard {

        @XPath(".//div[@role='combobox']")
        Button select();

        @XPath("//ul[@role='listbox']//li[@role='option']")
        List<Button> options();
    }

    interface LabelledCheckbox extends Checkbox {

        @Override
        @XPath("./ancestor::label[1]//span[contains(@class, 'MuiFormControlLabel-label')]")
        Label description();
    }
}
