package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Alert;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Chip;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.interop.web.eservice.infrastructure.page.component.PurposeDetailsPageClientsTab;
import it.pagopa.interop.web.eservice.infrastructure.page.component.PurposeDetailsPageDetailsTab;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import org.assertj.core.api.Assertions;

import java.util.List;

@Url("${interop.web.base-url}/fruizione/finalita/${purposeId}")
public interface ConsumerPurposeDetailsPage extends Page {

    Breadcrumbs breadcrumbs();

    @XPath(".//h1")
    Readable<String> purposeTitle();

    @XPath(".//h1/following-sibling::p[1]")
    Label purposeDescription();

    @XPath(".//a[contains(.,'Torna alle finalità')]")
    Button backToPurposesButton();

    @XPath(".//div[contains(@class,'MuiChip-root')]")
    Chip statusChip();

    @XPath(".//button[normalize-space()='Archivia']")
    Button archiveButton();

    @XPath(".//button[normalize-space()='Duplica']")
    Button duplicateButton();

    @XPath(".//button[normalize-space()='Sospendi']")
    Button suspendButton();

    @XPath(".//*[@role='alert']")
    List<Alert> alerts();

    @XPath(".//button[@role='tab' and contains(.,'Dettagli finalità')]")
    Button purposeDetailsTab();

    @XPath(".//button[@role='tab' and contains(.,'Client associati')]")
    Button associatedClientsTab();

    @XPath(".//div[@role='tabpanel' and contains(@id,'P-details')]")
    PurposeDetailsPageDetailsTab detailsTabPanel();

    @XPath(".//div[@role='tabpanel' and contains(@id,'P-clients')]")
    PurposeDetailsPageClientsTab clientsTabPanel();

    @Override
    default void assertLoaded() {
        Assertions.assertThat(breadcrumbs().items().stream().map(Readable::read).toList())
                .as("Breadcrumbs")
                .containsExactly("Fruizione", "Finalità inoltrate", "Gestisci singola finalità");
    }
}