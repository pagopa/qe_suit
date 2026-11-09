package it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Link;
import it.pagopa.infrastructure.suit.component.TextField;

@XPath(".//*[@id='name']/ancestor::*[contains(@class, 'MuiPaper-root')][1]")
public interface AboutEServiceSection extends Component {
    @XPath(".//h2")
    Label title();

    @XPath(".//a[contains(., 'linee guida')]/ancestor::p[1]")
    Label description();

    @XPath(".//label[@for='name']")
    Label nameLabel();

    @XPath(".//label[@for='description']")
    Label descriptionLabel();

    @XPath(".//a[contains(., 'linee guida')]")
    Link guidelinesLink();

    @XPath(".//*[@id='name']")
    TextField eServiceNameInput();

    @XPath(".//*[@id='name-infoLabel']")
    Label nameHelperText();

    @XPath(".//*[@id='description']")
    TextField eServiceDescriptionInput();

    @XPath(".//*[@id='description-infoLabel']")
    Label descriptionHelperText();
}
