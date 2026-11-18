package it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.TextField;

@XPath(".//*[@id='name']/ancestor::*[contains(@class, 'MuiPaper-root')][1]")
public interface AboutEServiceTemplateSection extends Component {

    @XPath(".//h2")
    Label title();

    @XPath(".//label[@for='name']")
    Label nameLabel();

    @XPath(".//*[@id='name']")
    TextField nameInput();

    @XPath(".//*[@id='name-infoLabel']")
    Label nameHelperText();

    @XPath(".//label[@for='intendedTarget']")
    Label intendedTargetLabel();

    @XPath(".//*[@id='intendedTarget']")
    TextField intendedTargetInput();

    @XPath(".//*[@id='intendedTarget-infoLabel']")
    Label intendedTargetHelperText();

    @XPath(".//label[@for='description']")
    Label descriptionLabel();

    @XPath(".//*[@id='description']")
    TextField descriptionInput();

    @XPath(".//*[@id='description-infoLabel']")
    Label descriptionHelperText();
}

