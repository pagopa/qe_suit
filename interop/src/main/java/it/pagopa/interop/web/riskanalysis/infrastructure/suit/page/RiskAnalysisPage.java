package it.pagopa.interop.web.riskanalysis.infrastructure.suit.page;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Table;
import it.pagopa.infrastructure.suit.component.Tabs;
import it.pagopa.utils.async.PollingUtils;

/**
 * Page object for the risk-analysis workspace and its unauthorized-access view.
 */
@Url("${interop.web.base-url}/analisi-del-rischio")
public interface RiskAnalysisPage extends Page {

    String PAGE_TITLE = "Analisi del rischio";

    @XPath("//h1")
    Label title();

    @XPath("//h1/following-sibling::*[1]")
    Label subtitle();

    @XPath("//*[@role='tablist']")
    Tabs tabs();

    @XPath("//button[@role='tab' and normalize-space()='Da fare']")
    Button todoTab();

    @XPath("//button[@role='tab' and normalize-space()='Concluse']")
    Button completedTab();

    @XPath("//div[contains(@class, 'MuiInputBase-root')][.//fieldset/legend/span[normalize-space()='Cerca per e-service']]//input[@role='combobox']")
    TextField eserviceFilter();

    @XPath("//div[contains(@class, 'MuiInputBase-root')][.//fieldset/legend/span[normalize-space()='Stato analisi del rischio']]//input[@role='combobox']")
    TextField statusFilter();

    @XPath("//table")
    Table riskAnalysisTable();

    /** Returns the assignment date in the first table row. */
    @XPath("//table/tbody/tr[1]/td[1]")
    Label firstRowDate();

    /** Returns the e-service name in the first table row. */
    @XPath("//table/tbody/tr[1]/td[2]")
    Label firstRowEservice();

    /** Returns the status chip text in the first table row. */
    @XPath("//table/tbody/tr[1]/td[5]//span[contains(@class, 'MuiChip-label')]")
    Label firstRowStatus();

    @XPath("//h1")
    Label unauthorizedTitle();

    @XPath("//h1/following-sibling::*[1]")
    Label unauthorizedSubtitle();

    @XPath("//a[@href='/ui/it/' and normalize-space()='Torna alla home']")
    Button backToHomeBtn();

    /**
     * Waits until the page title is non-blank, which is common to both the
     * authorized workspace and the unauthorized-access view.
     */
    @Override
    default void assertLoaded() {
        PollingUtils.pollUntil(
                () -> title().read(),
                text -> text != null && !text.isBlank(),
                "Risk Analysis page is not loaded or its title is absent"
        );
    }
}