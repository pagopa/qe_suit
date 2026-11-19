package it.pagopa.interop.web.eservice.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Alert;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.TextField;

public interface PurposeTemplateGeneralStepPanel extends Component {

    @XPath(".//h2[normalize-space()='Informazioni sulla finalità']")
    Label sectionTitle();

    @XPath(".//h2[normalize-space()='Informazioni sulla finalità']/ancestor::div[3]/p[1]")
    Label sectionDescription();

    @XPath(".//h3[normalize-space()='Descrizione']")
    Label descriptionSectionTitle();

    @XPath(".//p[normalize-space()='Nome della finalità']")
    Label purposeNameLabel();

    @XPath(".//p[normalize-space()='Nome della finalità']/ancestor::div[1]/following-sibling::div//span")
    Label purposeNameValue();

    @XPath(".//p[normalize-space()='Descrizione della finalità']")
    Label purposeDescriptionLabel();

    @XPath(".//p[normalize-space()='Descrizione della finalità']/ancestor::div[1]/following-sibling::div//span")
    Label purposeDescriptionValue();

    @XPath(".//p[normalize-space()='Accesso a titolo gratuito']")
    Label freeAccessLabel();

    @XPath(".//p[normalize-space()='Accesso a titolo gratuito']/ancestor::div[1]/following-sibling::div//span")
    Label freeAccessValue();

    @XPath(".//p[normalize-space()='Motivazione del titolo gratuito']")
    Label freeAccessReasonLabel();

    @XPath(".//p[normalize-space()='Motivazione del titolo gratuito']/ancestor::div[1]/following-sibling::div//span")
    Label freeAccessReasonValue();

    @XPath(".//h2[normalize-space()='Informazioni tecniche']")
    Label technicalInformationSectionTitle();

    @XPath(".//label[starts-with(normalize-space(),\"Nome dell'istanza\")]")
    Label instanceNameLabel();

    @XPath(".//input[@name='purposeTitle']")
    TextField instanceNameInput();

    @XPath(".//h2[normalize-space()='Stima chiamate API']")
    Label apiCallsSectionTitle();

    @XPath(".//h2[normalize-space()='Stima chiamate API']/ancestor::div[3]/p[1]")
    Label apiCallsSectionDescription();

    @XPath(".//label[starts-with(normalize-space(),'Stima numero chiamate API/giorno')]")
    Label dailyCallsLabel();

    @XPath(".//input[@name='dailyCalls']")
    TextField dailyCallsInput();

    @XPath(".//*[@id='dailyCalls-infoLabel']")
    Label dailyCallsHelpText();

    @XPath(".//*[@role='alert']")
    Alert thresholdsAlert();

    @XPath(".//div[contains(@class,'MuiAlertTitle-root')]")
    Label thresholdsAlertTitle();

    @XPath(".//p[normalize-space()='Per fruitore']")
    Label consumerThresholdLabel();

    @XPath(".//p[normalize-space()='Per fruitore']/following-sibling::p[1]")
    Label consumerThresholdValue();

    @XPath(".//p[normalize-space()='Totali']")
    Label totalThresholdLabel();

    @XPath(".//p[normalize-space()='Totali']/following-sibling::p[1]")
    Label totalThresholdValue();
}