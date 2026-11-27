package it.pagopa.interop.web.purpose.infrastructure.page.purpose_edit_steps;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.*;

@XPath("//form[.//section[contains(@class, 'MuiPaper-root') and .//h2[normalize-space()='Analisi del rischio']]]")
public interface RiskAnalysisStep extends Component {

    @XPath(".//section[contains(@class, 'MuiPaper-root') and .//h2[normalize-space()='Analisi del rischio']]")
    RiskAnalysisHeaderCard headerCard();

    @XPath(".//div[@role='alert' and contains(@class, 'MuiAlert-colorWarning')]")
    Alert noPersonalDataWarningAlert();

    //Finalità di accesso ai dati ---
    @XPath(".//section[contains(@class, 'MuiPaper-root') and .//*[@id='answers.purpose-label']]")
    PurposeQuestionCard purposeQuestionCard();

    // Optional Specificare il fine perseguito ---
    @XPath(".//section[contains(@class, 'MuiPaper-root') and .//*[@id='answers.otherPurpose-label']]")
    OtherPurposeQuestionCard otherPurposeQuestionCard();

    //Accesso a dati personali ---
    @XPath(".//section[contains(@class, 'MuiPaper-root') and .//*[@id='answers.usesPersonalData-label']]")
    UsesPersonalDataQuestionCard usesPersonalDataQuestionCard();

    //Accesso a dati non personali di terzi ---
    @XPath(".//section[contains(@class, 'MuiPaper-root') and .//*[@id='answers.usesThirdPartyPersonalData-label']]")
    UsesThirdPartyPersonalDataQuestionCard usesThirdPartyPersonalDataQuestionCard();

    //Fruizione per conto di terzi soggetti ---
    @XPath(".//section[contains(@class, 'MuiPaper-root') and .//*[@id='answers.isRequestOnBehalfOfThirdParties-label']]")
    RequestOnBehalfOfThirdPartiesQuestionCard requestOnBehalfOfThirdPartiesQuestionCard();

    interface RiskAnalysisHeaderCard extends Card {

        @XPath(".//h2[normalize-space()='Analisi del rischio']")
        Label title();

        @XPath(".//h2[normalize-space()='Analisi del rischio']/following::p[contains(@class, 'MuiTypography-body2')][1]")
        Label description();

        @XPath(".//p[contains(@class, 'MuiTypography-body2') and normalize-space()='Trattamento dati personali']")
        Label personalDataProcessingLabel();

        @XPath(".//p[contains(@class, 'MuiTypography-body2') and normalize-space()='Trattamento dati personali']/ancestor::div[contains(@class, 'MuiStack-root')][1]/div[contains(@class, 'MuiBox-root')][2]//*[(self::div or self::p) and contains(@class, 'MuiTypography-body2')][not(.//*[contains(@class, 'MuiTypography-body2')])]")
        Label personalDataProcessingValue();
    }

    interface PurposeQuestionCard extends Card {

        @XPath(".//*[@id='answers.purpose-label']")
        Label questionLabel();

        @XPath(".//*[@id='answers.purpose-infoLabel']")
        Label infoLabel();

        @XPath(".//label[contains(@class, 'MuiFormControlLabel-root') and .//input[@type='radio' and @name='answers.purpose' and @value='INSTITUTIONAL']]")
        RadioButton institutionalPurposeRadio();

        @XPath(".//label[contains(@class, 'MuiFormControlLabel-root') and .//input[@type='radio' and @name='answers.purpose' and @value='OTHER']]")
        RadioButton otherPurposeRadio();
    }

    interface OtherPurposeQuestionCard extends Card {

        @XPath(".//label[@id='answers.otherPurpose-label']")
        Label questionLabel();

        @XPath(".//div[contains(@class, 'MuiFormControl-root') and .//input[@id='answers.otherPurpose']]")
        TextField otherPurposeTextField();
    }

    interface UsesPersonalDataQuestionCard extends Card {

        @XPath(".//*[@id='answers.usesPersonalData-label']")
        Label questionLabel();

        @XPath(".//*[@id='answers.usesPersonalData-infoLabel']")
        Label infoLabel();

        @XPath(".//label[contains(@class, 'MuiFormControlLabel-root') and .//input[@type='radio' and @name='answers.usesPersonalData' and @value='YES']]")
        RadioButton yesRadio();

        @XPath(".//label[contains(@class, 'MuiFormControlLabel-root') and .//input[@type='radio' and @name='answers.usesPersonalData' and @value='NO']]")
        RadioButton noRadio();
    }

    interface UsesThirdPartyPersonalDataQuestionCard extends Card {

        @XPath(".//*[@id='answers.usesThirdPartyPersonalData-label']")
        Label questionLabel();

        @XPath(".//label[contains(@class, 'MuiFormControlLabel-root') and .//input[@type='radio' and @name='answers.usesThirdPartyPersonalData' and @value='YES']]")
        RadioButton yesRadio();

        @XPath(".//label[contains(@class, 'MuiFormControlLabel-root') and .//input[@type='radio' and @name='answers.usesThirdPartyPersonalData' and @value='NO']]")
        RadioButton noRadio();
    }

    interface RequestOnBehalfOfThirdPartiesQuestionCard extends Card {

        @XPath(".//*[@id='answers.isRequestOnBehalfOfThirdParties-label']")
        Label questionLabel();

        @XPath(".//label[contains(@class, 'MuiFormControlLabel-root') and .//input[@type='radio' and @name='answers.isRequestOnBehalfOfThirdParties' and @value='YES']]")
        RadioButton yesRadio();

        @XPath(".//label[contains(@class, 'MuiFormControlLabel-root') and .//input[@type='radio' and @name='answers.isRequestOnBehalfOfThirdParties' and @value='NO']]")
        RadioButton noRadio();
    }
}
