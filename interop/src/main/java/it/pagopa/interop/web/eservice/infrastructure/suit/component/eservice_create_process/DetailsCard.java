package it.pagopa.interop.web.eservice.infrastructure.suit.component.eservice_create_process;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.RadioButton;

public interface DetailsCard extends EServiceCreatePageCard {
    @XPath(".//div[contains(@class, 'MuiAlert-standardWarning')]//div[contains(@class, 'MuiAlert-message')]")
    Label warningAlert();

    @XPath(".//fieldset[.//input[@name='asyncExchange']]/legend")
    Label exchangeModeQuestion();

    @XPath(".//label[.//input[@name='asyncExchange' and @value='false']]")
    RadioButton syncRadio();

    @XPath(".//label[.//input[@name='asyncExchange' and @value='true']]")
    RadioButton asyncRadio();

    @XPath(".//fieldset[.//input[@name='technology']]/legend")
    Label technologyQuestion();

    @XPath(".//label[.//input[@name='technology' and @value='REST']]")
    RadioButton restRadio();

    @XPath(".//label[.//input[@name='technology' and @value='SOAP']]")
    RadioButton soapRadio();

    @XPath(".//fieldset[.//input[@name='mode']]/legend")
    Label dataFlowQuestion();

    @XPath(".//label[.//input[@name='mode' and @value='DELIVER']]")
    RadioButton deliverRadio();

    @XPath(".//label[.//input[@name='mode' and @value='RECEIVE']]")
    RadioButton receiveRadio();

    @XPath(".//fieldset[.//input[@name='personalData']]/legend")
    Label personalDataQuestion();

    @XPath(".//label[.//input[@name='personalData' and @value='true']]")
    RadioButton personalDataYesRadio();

    @XPath(".//label[.//input[@name='personalData' and @value='false']]")
    RadioButton personalDataNoRadio();
}
