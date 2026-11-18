package it.pagopa.interop.web.eservice_template.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;

public interface EServiceTemplateGeneralInfoSection extends Component {

    @XPath(".//h2")
    Label sectionTitle();

    @XPath(".//p[normalize-space()='Stai vedendo la versione']")
    Label versionLabel();

    @XPath(".//p[normalize-space()='Stai vedendo la versione']/ancestor::div[1]/following-sibling::div//*[normalize-space()][1]")
    Label versionValue();

    @XPath(".//p[normalize-space()='Eroga dati personali' or " +
                "normalize-space()='Riceve dati personali']")
    Label personalDataLabel();

    @XPath(".//p[normalize-space()='Eroga dati personali' or " +
                "normalize-space()='Riceve dati personali']/ancestor::div[1]/following-sibling::div//*[normalize-space()][1]")
    Label personalDataValue();

    @XPath(".//*[self::h3 or self::h4 or self::p][normalize-space()='Nome del template di e-service']")
    Label nameHeading();

    @XPath(".//*[(self::section or self::div) and contains(@class, 'MuiPaper-root') and .//*[normalize-space()='Nome del template di e-service']]//p")
//    @XPath(".//*[self::h3 or self::h4 or self::p][normalize-space()='Nome del template di e-service']/ancestor::div[1]/following-sibling::div[1]")
    Label nameValue();

    @XPath(".//*[self::h3 or self::h4 or self::p][normalize-space()='Nome del template di e-service']/ancestor::div[2]//button[normalize-space()='Modifica']")
    Button editNameButton();

    @XPath(".//*[self::h3 or self::h4 or self::p][normalize-space()='A chi è rivolto il template']")
    Label intendedTargetHeading();

    @XPath(".//*[(self::section or self::div) and contains(@class, 'MuiPaper-root') and .//*[normalize-space()='A chi è rivolto il template']]//p")
//    @XPath(".//*[self::h3 or self::h4 or self::p][normalize-space()='A chi è rivolto il template']/ancestor::div[1]/following-sibling::div[1]")
    Label intendedTargetValue();

    @XPath(".//*[self::h3 or self::h4 or self::p][normalize-space()='A chi è rivolto il template']/ancestor::div[2]//button[normalize-space()='Modifica']")
    Button editIntendedTargetButton();

    @XPath(".//*[self::h3 or self::h4 or self::p][normalize-space()='Descrizione dell’e-service']")
    Label descriptionHeading();

    @XPath(".//*[(self::section or self::div) and contains(@class, 'MuiPaper-root') and .//*[normalize-space()='Descrizione dell’e-service']]//p")
    Label descriptionValue();

    @XPath(".//*[self::h3 or self::h4 or self::p][normalize-space()='Descrizione dell’e-service']/ancestor::div[2]//button[normalize-space()='Modifica']")
    Button editDescriptionButton();

    @XPath(".//*[self::h3 or self::h4 or self::p][normalize-space()='Descrizione della versione']")
    Label versionDescriptionHeading();

    @XPath(".//*[(self::section or self::div) and contains(@class, 'MuiPaper-root') and .//*[normalize-space()='Descrizione della versione']]//p")
    Label versionDescriptionValue();

    @XPath(".//button[normalize-space()='Naviga a un’altra versione del template']")
    Button navigateToOtherVersionButton();
}

