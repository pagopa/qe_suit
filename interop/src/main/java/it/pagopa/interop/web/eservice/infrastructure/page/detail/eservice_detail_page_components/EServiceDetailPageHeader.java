package it.pagopa.interop.web.eservice.infrastructure.page.detail.eservice_detail_page_components;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Chip;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;

public interface EServiceDetailPageHeader extends Component {
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
    Chip descriptorStatusChip();

    @XPath(".//button[normalize-space()='Vedi ultima versione']")
    Button viewLatestVersionButton();
}
