package it.pagopa.interop.web.riskanalysis.infrastructure.suit.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Pagination;
import it.pagopa.infrastructure.suit.component.Table;
import it.pagopa.infrastructure.suit.component.TableCell;
import it.pagopa.infrastructure.suit.component.Tabs;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.utils.async.PollingUtils;

import java.util.List;

/**
 * Page object for the risk-analysis workspace and its unauthorized-access view.
 */
@Url("${interop.web.base-url}/analisi-del-rischio")
public interface RiskAnalysisPage extends Page {

    String PAGE_TITLE = "Analisi del rischio";

    /** Returns the page title. */
    @XPath(".//h1")
    Label title();

    /** Returns the explanatory text displayed below the page title. */
    @XPath(".//h1/following-sibling::*[1]")
    Label subtitle();

    /** Returns the tabs used to switch between pending and completed analyses. */
    @XPath(".//*[@role='tablist']")
    Tabs tabs();

    /** Returns the tab containing analyses awaiting review. */
    @XPath(".//button[@role='tab' and normalize-space()='Da fare']")
    Button todoTab();

    /** Returns the tab containing completed analyses. */
    @XPath(".//button[@role='tab' and normalize-space()='Concluse']")
    Button completedTab();

    /** Returns the e-service search filter. */
    @XPath(".//div[contains(@class, 'MuiInputBase-root')][.//fieldset/legend/span[normalize-space()='Cerca per e-service']]//input[@role='combobox']")
    TextField eserviceFilter();

    /** Returns the risk-analysis status filter. */
    @XPath(".//div[contains(@class, 'MuiInputBase-root')][.//fieldset/legend/span[normalize-space()='Stato analisi del rischio']]//input[@role='combobox']")
    TextField statusFilter();

    /** Returns the table displayed in the currently selected tab. */
    @XPath(".//*[@role='tabpanel' and @aria-labelledby = //button[@role='tab' and @aria-selected='true']/@id]//table")
    Table riskAnalysisTable();

    /** Returns the pagination controls for the currently selected tab. */
    @XPath(".//*[@role='tabpanel' and @aria-labelledby = //button[@role='tab' and @aria-selected='true']/@id]//nav[contains(@class,'MuiPagination-root')]")
    Pagination pagination();

    /**
     * Returns the assignment date in the first table row.
     */
    @XPath("//table/tbody/tr[1]/td[1]")
    Label firstRowDate();

    /**
     * Returns the e-service name in the first table row.
     */
    @XPath(".//table/tbody/tr[1]/td[2]")
    Label firstRowEservice();

    /**
     * Returns the status chip text in the first table row.
     */
    @XPath(".//table/tbody/tr[1]/td[5]//span[contains(@class, 'MuiChip-label')]")
    Label firstRowStatus();

    /** Returns the title shown when access to the page is unauthorized. */
    @XPath(".//h1")
    Label unauthorizedTitle();

    /** Returns the explanation shown when access to the page is unauthorized. */
    @XPath(".//h1/following-sibling::*[1]")
    Label unauthorizedSubtitle();

    /** Returns the button that navigates back to the home page. */
    @XPath(".//a[@href='/ui/it/' and normalize-space()='Torna alla home']")
    Button backToHomeBtn();

    /** Returns the empty-state message shown when no analyses are assigned. */
    @XPath(".//p[normalize-space()='Per ora non hai analisi del rischio assegnate']")
    Label noAssignedRiskAnalysisMessage();

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

    /**
     * Snapshot of the e-service name and status shown for a risk analysis.
     *
     * @param eserviceName name of the associated e-service
     * @param status displayed risk-analysis status
     */
    record RiskAnalysisRow(String eserviceName, String status) {}

    /**
     * Reads the e-service name and status from each row on the currently selected
     * tab and page.
     *
     * @return row snapshots containing the e-service name and risk-analysis status
     */
    default List<RiskAnalysisRow> readRecordsEserviceStatus() {
        return riskAnalysisTable().tBody().rows().stream()
                .map(row -> {
                    List<TableCell> cells = row.cells();
                    return new RiskAnalysisRow(
                            cells.get(1).read().trim(),
                            cells.get(4).read().trim()
                    );
                })
                .toList();
    }
}