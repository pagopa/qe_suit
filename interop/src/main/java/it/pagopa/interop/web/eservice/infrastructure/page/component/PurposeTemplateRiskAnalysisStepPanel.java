package it.pagopa.interop.web.eservice.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Alert;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.RadioButton;

public interface PurposeTemplateRiskAnalysisStepPanel extends Component {

    @XPath(".//h2[normalize-space()='Analisi del rischio']")
    Label sectionTitle();

    @XPath(".//h2[normalize-space()='Analisi del rischio']/ancestor::div[3]/p[1]")
    Label sectionDescription();

    @XPath(".//*[@role='alert']")
    Alert warningAlert();

    // -------------------------------------------------
    // QUESTION 1
    // -------------------------------------------------

    @XPath(".//*[@id='answers.purpose-label']")
    Label purposeQuestionLabel();

    @XPath(".//*[@id='answers.purpose-label']/ancestor::div[contains(@class,'css-1o2orqd')]//span[normalize-space()='Non modificabile']")
    Label purposeLockedChip();

    @XPath(".//*[@id='answers.purpose-infoLabel']")
    Label purposeQuestionHelpText();

    @XPath(".//label[.//input[@name='answers.purpose' and @value='INSTITUTIONAL']]")
    RadioButton institutionalPurposeRadio();

    @XPath(".//label[.//input[@name='answers.purpose' and @value='OTHER']]")
    RadioButton otherPurposeRadio();

    // -------------------------------------------------
    // QUESTION 2
    // -------------------------------------------------

    @XPath(".//label[contains(.,'Specificare il fine perseguito')]")
    Label institutionalPurposeDetailLabel();

    @XPath(".//input[@name='suggestedValueConsumer.institutionalPurpose']/preceding-sibling::*[@role='combobox']")
    Component suggestedAnswersSelector();

    // -------------------------------------------------
    // QUESTION 3
    // -------------------------------------------------

    @XPath(".//*[@id='answers.usesPersonalData-label']")
    Label usesPersonalDataLabel();

    @XPath(".//*[@id='answers.usesPersonalData-label']/ancestor::div[contains(@class,'css-1o2orqd')]//span[normalize-space()='Non modificabile']")
    Label usesPersonalDataLockedChip();

    @XPath(".//*[@id='answers.usesPersonalData-infoLabel']")
    Label usesPersonalDataHelpText();

    @XPath(".//label[.//input[@name='answers.usesPersonalData' and @value='YES']]")
    RadioButton usesPersonalDataYesRadio();

    @XPath(".//label[.//input[@name='answers.usesPersonalData' and @value='NO']]")
    RadioButton usesPersonalDataNoRadio();

    // -------------------------------------------------
    // QUESTION 4
    // -------------------------------------------------

    @XPath(".//*[@id='answers.usesThirdPartyPersonalData-label']")
    Label usesThirdPartyDataLabel();

    @XPath(".//*[@id='answers.usesThirdPartyPersonalData-label']/ancestor::div[contains(@class,'css-1o2orqd')]//span[normalize-space()='Non modificabile']")
    Label usesThirdPartyDataLockedChip();

    @XPath(".//label[.//input[@name='answers.usesThirdPartyPersonalData' and @value='YES']]")
    RadioButton usesThirdPartyDataYesRadio();

    @XPath(".//label[.//input[@name='answers.usesThirdPartyPersonalData' and @value='NO']]")
    RadioButton usesThirdPartyDataNoRadio();

    // -------------------------------------------------
    // QUESTION 5
    // -------------------------------------------------

    @XPath(".//*[@id='answers.isRequestOnBehalfOfThirdParties-label']")
    Label onBehalfOfThirdPartiesLabel();

    @XPath(".//*[@id='answers.isRequestOnBehalfOfThirdParties-label']/ancestor::div[contains(@class,'css-1o2orqd')]//span[normalize-space()='Non modificabile']")
    Label onBehalfOfThirdPartiesLockedChip();

    @XPath(".//label[.//input[@name='answers.isRequestOnBehalfOfThirdParties' and @value='YES']]")
    RadioButton onBehalfOfThirdPartiesYesRadio();

    @XPath(".//label[.//input[@name='answers.isRequestOnBehalfOfThirdParties' and @value='NO']]")
    RadioButton onBehalfOfThirdPartiesNoRadio();

    // -------------------------------------------------
    // ACTIONS
    // -------------------------------------------------

    @XPath(".//button[normalize-space()='Indietro']")
    Button backButton();

    @XPath(".//button[normalize-space()='Salva bozza e prosegui']")
    Button saveDraftAndContinueButton();
}