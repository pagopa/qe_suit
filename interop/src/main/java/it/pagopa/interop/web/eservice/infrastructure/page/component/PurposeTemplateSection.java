package it.pagopa.interop.web.eservice.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Alert;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Switch;
import it.pagopa.infrastructure.suit.component.TextField;

public interface PurposeTemplateSection extends Component {

    @XPath(".//h2")
    Label sectionTitle();

    @XPath(".//h2/ancestor::div[3]/p[1]")
    Label sectionDescription();

    @XPath(".//*[@id='usePurposeTemplate-label']")
    Label usePurposeTemplateLabel();

    @XPath(".//label[contains(@class, 'MuiFormControlLabel-root') and .//input[@name='usePurposeTemplate']]")
    Switch usePurposeTemplateSwitch();

    @XPath(".//*[@id='showOnlyLinkedPurposeTemplates-label']")
    Label showOnlySuggestedTemplatesLabel();

    @XPath(".//label[contains(@class, 'MuiFormControlLabel-root') and .//input[@name='showOnlyLinkedPurposeTemplates']]")
    Switch showOnlySuggestedTemplatesSwitch();

    @XPath(".//label[starts-with(normalize-space(),'Template per la compilazione agevolata della finalità')]")
    Label purposeTemplateLabel();

    @XPath(".//label[starts-with(normalize-space(),'Template per la compilazione agevolata della finalità')]/following::input[@role='combobox'][1]")
    TextField purposeTemplateInput();

    @XPath(".//*[@id='acknowledgeInfoResponsibility-label']")
    Label acknowledgeResponsibilityLabel();

    @XPath(".//label[contains(@class, 'MuiFormControlLabel-root') and .//input[@name='acknowledgeInfoResponsibility']]")
    Switch acknowledgeResponsibilitySwitch();

    Alert warningAlert();
}