package it.pagopa.interop.web.eservice.infrastructure.page.component.template_eservice_stepper;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.*;

@XPath(".//form")
public interface ThresholdsAndAttributesStep extends Component {

    ApiCallThresholdCard apiCallThresholdCard();

    AccessRequirementsCard accessRequirementsCard();

    @XPath(".//button[@type='button' and normalize-space()='Indietro' and .//*[@data-testid='ArrowBackIcon']]")
    Button previousStepButton();

    @XPath(".//button[@type='submit' and normalize-space()='Salva bozza e prosegui' and .//*[@data-testid='SaveIcon']]")
    Button saveDraftButton();

    @XPath(".//div[contains(@class, 'MuiPaper-root') and .//h2[normalize-space()='Soglie di chiamate API']]")
    interface ApiCallThresholdCard extends Card {
        @XPath(".//h2[contains(@class, 'MuiTypography-root') and normalize-space()='Soglie di chiamate API']")
        Label apiCallThresholdCardTitle();

        @XPath(".//input[@name='thresholdsSection']")
        Switch apiCallThresholdSwitch();

        @XPath(".//span[@id='thresholdsSection-label']")
        Label apiCallSwitchLabel();

        @XPath(".//label[@id='dailyCallsPerConsumer-label']")
        Label dailyCallsPerConsumerLabel();

        @XPath(".//input[@id='dailyCallsPerConsumer']")
        TextField dailyCallsPerConsumerInput();

        @XPath(".//span[@id='dailyCallsPerConsumer-infoLabel']")
        Label dailyCallsPerConsumerInfoLabel();

        @XPath(".//label[@id='dailyCallsTotal-label']")
        Label dailyCallsTotalLabel();

        @XPath(".//input[@id='dailyCallsTotal']")
        TextField dailyCallsTotalInput();

        @XPath(".//span[@id='dailyCallsTotal-infoLabel']")
        Label dailyCallsTotalInfoLabel();

        Alert alert();
    }

    @XPath(".//div[contains(@class, 'MuiPaper-root') and .//h2[normalize-space()='Requisiti di accesso']]")
    interface AccessRequirementsCard extends Card {
        @XPath(".//h2[contains(@class, 'MuiTypography-root') and normalize-space()='Requisiti di accesso']")
        Label cardTitle();

        @XPath(".//p[contains(normalize-space(), 'Definisci chi può accedere a questo e-service.')]")
        Label subtitle();

        @XPath(".//a[normalize-space()='Scopri di più sugli attributi']")
        Link discoverMoreAttributesLink();

        Tabs attributeTabs();

        @XPath(".//div[@role='tabpanel' and not(@hidden)]//p[contains(@class, 'MuiTypography-body2')]")
        Label certifiedAttributesDescription();

        @XPath(".//div[@role='tabpanel' and not(@hidden)]//a[normalize-space()='Scopri di più su quali attributi usare']")
        Link discoverMoreWhichAttributesLink();

        @XPath(".//div[@role='tabpanel' and not(@hidden)]//button[normalize-space()='Crea requisito']")
        Button createRequirementButton();
    }
}
