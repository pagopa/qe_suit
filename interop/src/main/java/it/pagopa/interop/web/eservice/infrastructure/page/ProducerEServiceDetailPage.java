package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Dialog;
import it.pagopa.interop.web.eservice.infrastructure.page.component.EServiceArchivingDialog;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import org.assertj.core.api.Assertions;

@Url("${interop.web.producer-e-service-list}/${eserviceId}/${descriptorId}")
public interface ProducerEServiceDetailPage extends Page {

    @XPath(".//h1")
    Readable<String> pageTitle();

    Breadcrumbs breadcrumbs();

    @XPath(".//button[@aria-label='Apri men\u00f9 delle azioni']")
    Button actionsMenuButton();

    default Button getActionsMenuButton() {
        return actionsMenuButton();
    }

    @XPath("//ul[@role='menu']")
    Component actionsMenu();

    @XPath("//ul[@role='menu']/li[@role='menuitem']")
    Component actionsMenuItems();

    @XPath("//li[@role='menuitem' and normalize-space(.)='Duplica e-service']")
    Button duplicateEServiceButton();

    @XPath("//li[@role='menuitem' and normalize-space(.)='Archivia e-service']")
    Button getArchiveEServiceButton();

    @XPath("//div[@role='dialog' and .//*[normalize-space(.)='Archivia e-service']]")
    EServiceArchivingDialog getArchivingDialog();

    @XPath("//div[@role='dialog' and .//*[normalize-space(.)='Richiedi archiviazione e-service']]")
    EServiceArchivingDialog getArchivingRequestDialog();

    @XPath("//div[@role='dialog' and .//*[normalize-space(.)='Conferma clonazione e-service']]")
    Dialog duplicationConfirmationDialog();

    EServiceDraftEditPage duplicatedEServicePage();

    default void duplicateEService() {
        actionsMenuButton().click();
        Assertions.assertThat(duplicateEServiceButton().get(FindPolicy.CLICKABLE))
                .as("Duplica e-service is visible and enabled")
                .isPresent();
        duplicateEServiceButton().click();
        duplicationConfirmationDialog().confirmBtn().click();
    }

    @Override
    default void assertLoaded() {
        Assertions.assertThat(breadcrumbs().items().stream().map(Readable::read).toList())
                .as("Breadcrumbs")
                .containsExactly("Erogazione", "I miei e-service", "Visualizza e-service");
        pageTitle().readAndAssert(name -> Assertions.assertThat(name)
                .as("Page title is not blank")
                .isNotBlank());
    }
}
