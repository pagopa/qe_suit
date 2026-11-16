package it.pagopa.interop.web.agreement.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import it.pagopa.interop.web.eservice.infrastructure.page.component.AgreementDetailsGeneralInfoSection;
import org.assertj.core.api.Assertions;

@Url("${interop.web.producer-agreement-list}/${agreementId}")
public interface ProducerAgreementDetailPage extends Page {

    @XPath(".//h1[normalize-space(.)='Gestisci richiesta di fruizione']")
    Readable<String> pageTitle();

    Breadcrumbs breadcrumbs();

    @XPath(".//section[.//h2[normalize-space()='Informazioni generali']]")
    AgreementDetailsGeneralInfoSection generalInfoSection();

    @XPath(".//button[normalize-space()='Attiva']")
    Button activeButton();

    @XPath(".//button[normalize-space()='Conferma']")
    Button confirmButton();

    @Override
    default void assertLoaded() {
        pageTitle().readAndAssert("Gestisci richiesta di fruizione");

        Assertions.assertThat(breadcrumbs().getPath())
                .as("Breadcrumbs")
                .isEqualTo("Erogazione/Richieste ricevute/Gestisci richiesta");
    }
}
