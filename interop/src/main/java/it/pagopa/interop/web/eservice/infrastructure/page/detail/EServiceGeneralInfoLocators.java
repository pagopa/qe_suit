package it.pagopa.interop.web.eservice.infrastructure.page.detail;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.pagopa.infrastructure.suit.component.Label;

/**
 * 
General information section (including delegation, bottom actions and drawer).
 */
public interface 
EServiceGeneralInfoLocators
 {

    // SectionContainer renders <section><div(Stack)><div(row)>...<h2|h3 title/></div><p description/></div><div(Box) children/>...
    String GENERAL_SECTION = ".//section[./div[1]//h2[normalize-space()='Informazioni generali']]";

    // ------------------------------------------------- General information

    @XPath(GENERAL_SECTION + "/div[1]//h2")
    Label generalInfoTitle();

    @XPath(GENERAL_SECTION + "//*[normalize-space()='Erogatore' and not(*)]")
    Label producerLabel();


    @XPath(GENERAL_SECTION + "//p[normalize-space()='Erogatore']/parent::div/following-sibling::div[1]//span")
    Label producerValue();

    @XPath(GENERAL_SECTION + "//*[normalize-space()='Template in uso' and not(*)]")
    Label templateInUseLabel();

    @XPath(GENERAL_SECTION + "//*[normalize-space()='Stai vedendo la versione' and not(*)]")
    Label descriptorVersionLabel();

    @XPath(GENERAL_SECTION + "//div[./div/p[normalize-space()='Stai vedendo la versione']]/div[2]//span")
    Label descriptorVersionValue();

    @XPath(GENERAL_SECTION + "//*[normalize-space()='Eroga dati personali' and not(*)]")
    Label personalDataLabel();

    @XPath(GENERAL_SECTION + "//div[./div/p[normalize-space()='Eroga dati personali']]/div[2]//span")
    Label personalDataValue();

    @XPath(GENERAL_SECTION + "//*[normalize-space()='Scambio dati' and not(*)]")
    Label exchangeTypeLabel();

    @XPath(GENERAL_SECTION + "//div[./div/p[normalize-space()='Scambio dati']]/div[2]//span")
    Label exchangeTypeValue();

    @XPath(GENERAL_SECTION + "//*[normalize-space()=\"Descrizione dell'e-service\" and not(*)]")
    Label eserviceDescriptionLabel();

    @XPath(GENERAL_SECTION + "//div[./div/p[normalize-space()=\"Descrizione dell'e-service\"]]/div[2]//span")
    Label eserviceDescriptionValue();

    @XPath(GENERAL_SECTION + "//*[normalize-space()='Descrizione della versione' and not(*)]")
    Label descriptorDescriptionLabel();

    @XPath(GENERAL_SECTION + "//div[./div/p[normalize-space()='Descrizione della versione']]/div[2]//span")
    Label descriptorDescriptionValue();

    @XPath(GENERAL_SECTION + "//h3[normalize-space()='Fruizione tramite delega']")
    Label delegationSectionTitle();

    @XPath(GENERAL_SECTION + "//*[normalize-space()='Autorizzazione' and not(*)]")
    Label consumerDelegableLabel();

    @XPath(GENERAL_SECTION + "//div[./div/p[normalize-space()='Autorizzazione']]/div[2]//span")
    Label consumerDelegableValue();

    @XPath(GENERAL_SECTION + "//*[normalize-space()='Autorizzazione all’associazione dei client' and not(*)]")
    Label clientAccessDelegableLabel();

    @XPath(GENERAL_SECTION + "//div[./div/p[normalize-space()=\"Autorizzazione all’associazione dei client\"]]/div[2]//span")
    Label clientAccessDelegableValue();

    // Bottom actions are IconLink (MUI Link, rendered as <button> when component='button')
    @XPath(GENERAL_SECTION + "//*[(self::a or self::button)][normalize-space()='Vedi i dettagli tecnici dell’e-service']")
    Label showTechnicalDetailsAction();

    @XPath(GENERAL_SECTION + "//*[(self::a or self::button)][normalize-space()='Vedi i dettagli sullo scambio asincrono']")
    Label showAsyncExchangeDetailsAction();

    @XPath(GENERAL_SECTION + "//*[(self::a or self::button)][normalize-space()='Visualizza i contatti dell’erogatore']")
    Label showProducerContactsAction();

    // Closed MUI drawers are not mounted: any mounted drawer is an open one
    @XPath(".//*[contains(@class, 'MuiDrawer-root')]")
    Label openDrawer();
}

