package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.web.infrastructure.config.suit.component.Button;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;

@Url("${interop.web.catalog}/${eserviceId}/${descriptorId}")
public interface EServiceDetailPage extends Page {

    @XPath(".//h1")
    Readable<String> pageTitle();

    @XPath(".//button[contains(text(),'Richiedi fruizione')]")
    Button agreementButton();

    @Override
    default void assertLoaded() {
        SoftAssertions.assertSoftly(softly -> {
            pageTitle().readAndAssert(eServiceName -> Assertions.assertThat(eServiceName).isNotBlank());
            agreementButton().assertLoaded();
        });
    }
}
