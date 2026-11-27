package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Gettable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.config.WebSuiteContext;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.utils.async.PollingUtils;
import org.assertj.core.api.Assertions;

@Url("${interop.web.producer-e-service-template-list}/${eserviceTemplateId}/${versionId}/feedback")
public interface ProducerEServiceTemplatePublishThankYouPage extends Page {

    //    @XPath("//svg[@data-testid='CheckIcon']")
    @XPath(".//main//div[*[local-name()='svg' and @data-testid='CheckIcon']]")
    Component checkIcon();

    @XPath(".//main//h1")
    Readable<String> title();

    @XPath(".//main//p[contains(@class, 'MuiTypography-body1')]")
    Readable<String> description();

    @XPath(".//main//button[normalize-space()='Vai al template']")
    Button goToTemplateButton();

    default String currentUrl() {
        return WebSuiteContext.getConfiguration()
                .getPresentationApiAdapters()
                .get(0)
                .getLocation()
                .getUrl();
    }

    @Override
    default void assertLoaded() {
        PollingUtils.pollUntil(
                () -> title().read(),
                text -> text != null && !text.isBlank(),
                "Producer E-Service Template Publish Thank You Page is not loaded yet. Title is blank"
        );
        Assertions.assertThat(goToTemplateButton().get(Gettable.FindPolicy.PRESENT))
                .as("Go to template button should be present")
                .isPresent();
    }
}

