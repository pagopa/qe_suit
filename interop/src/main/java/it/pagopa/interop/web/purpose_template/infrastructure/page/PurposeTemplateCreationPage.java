package it.pagopa.interop.web.purpose_template.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Switch;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.utils.async.PollingUtils;

import java.time.Duration;
import java.util.Optional;

@Url("${interop.web.purpose-template}/${templateId}/modifica")
public interface PurposeTemplateCreationPage extends Page {

    @XPath(".//h1")
    Label pageTitle();

    @XPath("//*[@id='purposeTitle']")
    TextField nameField();

    @XPath("//*[@id='purposeDescription']")
    TextField descriptionField();

    @XPath("//span[span/input[@name='purposeIsFreeOfCharge']]")
    Switch freeOfChargeSwitch();

    @XPath("//*[@id='purposeFreeOfChargeReason']")
    Optional<TextField> freeOfChargeExplanationField();

    @Override
    default void assertLoaded() {
        PollingUtils.pollUntil(
                () -> pageTitle().read(),
                title -> title.equals("Crea template finalità"),
                Duration.ofSeconds(20),
                Duration.ofSeconds(1),
                "Purpose Template Creation Page is not loaded yet. Page title is blank"
        );
    }
}

