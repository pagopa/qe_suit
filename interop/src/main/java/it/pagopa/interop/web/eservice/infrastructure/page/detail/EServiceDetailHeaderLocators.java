package it.pagopa.interop.web.eservice.infrastructure.page.detail;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.pagopa.infrastructure.suit.component.Alert;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;

import java.util.List;

/**
 * 
Header, primary actions, alerts and tabs of the catalog e-service detail page.
 */
public interface 
EServiceDetailHeaderLocators
 {

    // ---------------------------------------------------------------- Header

    @XPath(".//h1")
    Label pageTitle();

    Breadcrumbs breadcrumbs();

    @XPath(".//*[(self::a or self::button)][normalize-space()='Torna al catalogo']")
    Button backToCatalogAction();

    // h2 rendered with text-transform: uppercase (observed text may differ in case from the DOM text)
    @XPath(".//h2[normalize-space()='Versione']")
    Label versionHeaderLabel();

    @XPath(".//h2[normalize-space()='Versione']/following-sibling::button[1]")
    Button versionShortcutButton();

    // Only one chip is rendered in the page header (statusChip of the version info section)
    @XPath("(.//*[contains(@class, 'MuiChip-root')])[1]")
    Label descriptorStatusChip();

    @XPath(".//button[normalize-space()='Vedi ultima versione']")
    Button viewLatestVersionButton();

    // ------------------------------------------------------- Primary actions

    @XPath(".//button[normalize-space()='Richiedi fruizione']")
    Button agreementButton();

    @XPath(".//button[normalize-space()='Visualizza richiesta']")
    Button inspectAgreementButton();

    @XPath(".//button[normalize-space()='Completa richiesta']")
    Button completeAgreementButton();

    @XPath(".//button[normalize-space()='Aggiorna a nuova versione']")
    Button upgradeToNewVersionButton();

    // ---------------------------------------------------------------- Alerts

    List<Alert> alerts();

    // ------------------------------------------------------------------ Tabs

    @XPath(".//*[@role='tab'][normalize-space()='Dettaglio e-service']")
    Label eserviceDetailTab();

    @XPath(".//*[@role='tab'][normalize-space()='Template finalità collegati']")
    Label purposeTemplatesTab();

    // MUI lab TabPanel: a not selected panel stays in the DOM with the hidden attribute and no children
    @XPath("(.//*[@role='tabpanel'])[1]")
    Label eserviceDetailTabPanel();

    @XPath("(.//*[@role='tabpanel'])[2]")
    Label purposeTemplatesTabPanel();
}

