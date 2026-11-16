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

    @XPath(".//h1")
    Label title();

    @XPath(".//h1/following-sibling::*[1]")
    Label subtitle();

    @XPath(".//*[@role='tablist']")
    Tabs tabs();

    @XPath(".//button[@role='tab' and normalize-space()='Da fare']")
    Button todoTab();

    @XPath(".//button[@role='tab' and normalize-space()='Concluse']")
    Button completedTab();

    @XPath(".//div[contains(@class, 'MuiInputBase-root')][.//fieldset/legend/span[normalize-space()='Cerca per e-service']]//input[@role='combobox']")
    TextField eserviceFilter();

    @XPath(".//div[contains(@class, 'MuiInputBase-root')][.//fieldset/legend/span[normalize-space()='Stato analisi del rischio']]//input[@role='combobox']")
    TextField statusFilter();

    /** Returns the table displayed in the currently selected tab. */
    @XPath(".//*[@role='tabpanel' and @aria-labelledby = //button[@role='tab' and @aria-selected='true']/@id]//table")
    Table riskAnalysisTable();

    /** Returns the pagination controls for the currently selected tab. */
    @XPath(".//*[@role='tabpanel' and @aria-labelledby = //button[@role='tab' and @aria-selected='true']/@id]//nav[contains(@class,'MuiPagination-root')]")
    Pagination pagination();

    @XPath(".//h1")
    Label unauthorizedTitle();

    @XPath(".//h1/following-sibling::*[1]")
    Label unauthorizedSubtitle();

    @XPath(".//a[@href='/ui/it/' and normalize-space()='Torna alla home']")
    Button backToHomeBtn();

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
     * Snapshot of the e-service name, reviewer or total number of reviewers and status shown for a risk analysis.
     *
     * @param eserviceName name of the associated e-service
     * @param reviewerOrTotReviewer reviewer or total number of reviewers
     * @param status displayed risk-analysis status
     */
    record RiskAnalysisRow(String eserviceName, String reviewerOrTotReviewer, String status) {}

    /**
     * Reads the e-service name and status from each row on the currently selected
     * tab and pagination page, preserving display order without navigating.
     *
     * @return row snapshots containing the e-service name and risk-analysis status
     */
    default List<RiskAnalysisRow> readRecordsEserviceStatus() {
        return riskAnalysisTable().tBody().rows().stream()
                .map(row -> {
                    List<TableCell> cells = row.cells();
                    return new RiskAnalysisRow(
                            cells.get(1).read().trim(),
                            cells.get(3).read().trim(),
                            cells.get(4).read().trim()
                    );
                })
                .toList();
    }
}