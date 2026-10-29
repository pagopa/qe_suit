package it.pagopa.interop.web.eservice.infrastructure.suit.component.eservice_create_card;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Link;
import it.pagopa.infrastructure.suit.component.TextField;

public interface InformationCard extends EServiceCreatePageCard {
    @XPath(".//a[contains(., 'linee guida')]/ancestor::p[1]")
    Label description();

    @XPath(".//label[@for='name']")
    Label nameLabel();

    @XPath(".//label[@for='description']")
    Label descriptionLabel();

    @XPath(".//a[contains(., 'linee guida')]")
    Link guidelinesLink();

    @XPath(".//input[@id='name' and @name='name']")
    TextField nameInput();

    @XPath(".//*[@id='name-infoLabel']")
    Label nameHelperText();

    @XPath(".//textarea[@id='description' and @name='description']")
    TextField descriptionInput();

    @XPath(".//*[@id='description-infoLabel']")
    Label descriptionHelperText();
}
