package it.pagopa.reporting.writer.html;

import it.pagopa.reporting.dto.ConcreteCaseReport;
import it.pagopa.reporting.dto.EnvironmentProperty;
import it.pagopa.reporting.dto.ExecutionStatus;
import it.pagopa.reporting.dto.ReportDocument;
import it.pagopa.reporting.dto.RunSummary;
import it.pagopa.reporting.dto.StatusCounts;
import it.pagopa.reporting.dto.TestClassReport;
import it.pagopa.reporting.dto.TestFactoryReport;
import it.pagopa.reporting.writer.ReportWriter;
import it.pagopa.reporting.writer.ReportWritingException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class HtmlSidebarReportWriter implements ReportWriter {

    private final boolean includeDtoDump;

    public HtmlSidebarReportWriter() {
        this(true);
    }

    public HtmlSidebarReportWriter(boolean includeDtoDump) {
        this.includeDtoDump = includeDtoDump;
    }

    @Override
    public void write(ReportDocument reportDocument, Path outputPath) throws ReportWritingException {
        if (reportDocument == null) {
            throw new ReportWritingException("Report document is required");
        }

        if (outputPath == null) {
            throw new ReportWritingException("Output path is required");
        }

        try {
            Path parent = outputPath.toAbsolutePath().normalize().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(outputPath, buildHtml(reportDocument), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ReportWritingException("Cannot write HTML report to " + outputPath, e);
        }
    }

    private String buildHtml(ReportDocument report) {
        Slugger slugger = new Slugger();
        StringBuilder html = new StringBuilder(256_000);

        html.append("<!doctype html>")
                .append("<html lang=\"en\">")
                .append("<head>")
                .append("<meta charset=\"UTF-8\">")
                .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">")
                .append("<title>Surefire HTML Report</title>")
                .append("<style>")
                .append(css())
                .append("</style>")
                .append("</head>")
                .append("<body>")
                .append("<main class=\"main\">")
                .append("<header class=\"page-header\">")
                .append("<h1>Surefire HTML Report</h1>")
                .append("<p class=\"generated-at\">Generated at ")
                .append(escape(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .append("</p>")
                .append("</header>");

        appendInPageNavigation(report.testClasses(), slugger, html);
        appendFilters(html);
        appendSummary(report.summary(), html);
        appendEnvironmentProperties(report.environmentProperties(), html);
        appendWarnings(report.warnings(), html);
        appendClassSections(report.testClasses(), slugger, html);

        if (includeDtoDump) {
            html.append("<section class=\"panel\">")
                    .append("<details>")
                    .append("<summary>DTO debug dump (annotation-driven recursive view)</summary>")
                    .append(RecursiveDtoHtmlRenderer.render(report))
                    .append("</details>")
                    .append("</section>");
        }

        html.append("</main>")
                .append("<script>")
                .append(js())
                .append("</script>")
                .append("</body>")
                .append("</html>");

        return html.toString();
    }

    private void appendSidebar(ReportDocument report, Slugger slugger, StringBuilder html) {
        html.append("<aside class=\"sidebar\">")
                .append("<h2>Navigation</h2>")
                .append("<ul class=\"sidebar-tree\">");

        for (TestClassReport classReport : report.testClasses()) {
            String classKey = "class|" + classReport.className();
            String classId = slugger.idFor(classKey, classReport.className());

            html.append("<li class=\"sidebar-class\" data-sidebar-level=\"class\">")
                    .append("<a href=\"#")
                    .append(classId)
                    .append("\">")
                    .append(escape(shortName(classReport.className())))
                    .append("</a>")
                    .append(renderCountsBadge(classReport.counts()))
                    .append("<ul>");

            for (TestFactoryReport factoryReport : classReport.factories()) {
                String factoryKey = "factory|" + classReport.className() + "|" + factoryReport.factoryName();
                String factoryId = slugger.idFor(factoryKey, factoryReport.factoryName());

                html.append("<li class=\"sidebar-factory\" data-sidebar-level=\"factory\">")
                        .append("<a href=\"#")
                        .append(factoryId)
                        .append("\">")
                        .append(escape(factoryReport.factoryName()))
                        .append("</a>")
                        .append(renderCountsBadge(factoryReport.counts()))
                        .append("<ul>");

                for (ConcreteCaseReport concreteCase : factoryReport.concreteCases()) {
                    String caseKey = "case|" + classReport.className() + "|" + factoryReport.factoryName() + "|" + concreteCase.id();
                    String caseId = slugger.idFor(caseKey, concreteCase.displayName());

                    html.append("<li class=\"sidebar-case\" data-sidebar-level=\"case\" data-case-ref=\"")
                            .append(caseId)
                            .append("\">")
                            .append("<a href=\"#")
                            .append(caseId)
                            .append("\">")
                            .append(escape(concreteCase.displayName()))
                            .append("</a>")
                            .append("<span class=\"status-tag status-")
                            .append(concreteCase.status().cssClass())
                            .append("\">")
                            .append(escape(concreteCase.status().name()))
                            .append("</span>")
                            .append("</li>");
                }

                html.append("</ul></li>");
            }

            html.append("</ul></li>");
        }

        html.append("</ul>")
                .append("</aside>");
    }

    private void appendInPageNavigation(List<TestClassReport> classes, Slugger slugger, StringBuilder html) {
        html.append("<section class=\"panel\" id=\"navigation\">")
                .append("<h2>Navigation</h2>")
                .append("<p class=\"muted\">Expand classes and factories to browse concrete cases.</p>")
                .append("<ul class=\"nav-tree\">");

        for (TestClassReport classReport : classes) {
            String classKey = "class|" + classReport.className();
            String classId = slugger.idFor(classKey, classReport.className());
            String classAggregateClass = aggregateCssClass(classReport.counts());

            html.append("<li class=\"nav-class-item ")
                    .append(classAggregateClass)
                    .append("\">")
                    .append("<details>")
                    .append("<summary>")
                    .append("<span class=\"nav-entry\">")
                    .append("<a href=\"#")
                    .append(classId)
                    .append("\">")
                    .append(escape(shortName(classReport.className())))
                    .append("</a>")
                    .append(renderAggregateBadge(classReport.counts()))
                    .append(renderCountsBadge(classReport.counts()))
                    .append("</span>")
                    .append("</summary>")
                    .append("<ul>");

            for (TestFactoryReport factoryReport : classReport.factories()) {
                String factoryKey = "factory|" + classReport.className() + "|" + factoryReport.factoryName();
                String factoryId = slugger.idFor(factoryKey, factoryReport.factoryName());
                String factoryAggregateClass = aggregateCssClass(factoryReport.counts());

                html.append("<li class=\"nav-factory-item ")
                        .append(factoryAggregateClass)
                        .append("\">")
                        .append("<details>")
                        .append("<summary>")
                        .append("<span class=\"nav-entry\">")
                        .append("<a href=\"#")
                        .append(factoryId)
                        .append("\">")
                        .append(escape(factoryReport.factoryName()))
                        .append("</a>")
                        .append(renderAggregateBadge(factoryReport.counts()))
                        .append(renderCountsBadge(factoryReport.counts()))
                        .append("</span>")
                        .append("</summary>")
                        .append("<ul>");

                for (ConcreteCaseReport concreteCase : factoryReport.concreteCases()) {
                    String caseKey = "case|" + classReport.className() + "|" + factoryReport.factoryName() + "|" + concreteCase.id();
                    String caseId = slugger.idFor(caseKey, concreteCase.displayName());

                    html.append("<li class=\"nav-case-item\" data-case-ref=\"")
                            .append(caseId)
                            .append("\">")
                            .append("<a href=\"#")
                            .append(caseId)
                            .append("\">")
                            .append(escape(concreteCase.displayName()))
                            .append("</a>")
                            .append("<span class=\"status-tag status-")
                            .append(concreteCase.status().cssClass())
                            .append("\">")
                            .append(escape(concreteCase.status().name()))
                            .append("</span>")
                            .append("</li>");
                }

                html.append("</ul>")
                        .append("</details>")
                        .append("</li>");
            }

            html.append("</ul>")
                    .append("</details>")
                    .append("</li>");
        }

        html.append("</ul>")
                .append("</section>");
    }

    private void appendFilters(StringBuilder html) {
        html.append("<section class=\"panel filters\">")
                .append("<h2>Filters</h2>")
                .append("<div class=\"filter-row\">")
                .append("<label for=\"search-input\">Search</label>")
                .append("<input id=\"search-input\" type=\"text\" placeholder=\"class, factory, scenario, path, message\">")
                .append("</div>")
                .append("<div class=\"filter-row status-filters\">")
                .append(renderStatusFilterCheckbox(ExecutionStatus.PASSED, true))
                .append(renderStatusFilterCheckbox(ExecutionStatus.FAILED, true))
                .append(renderStatusFilterCheckbox(ExecutionStatus.ERROR, true))
                .append(renderStatusFilterCheckbox(ExecutionStatus.SKIPPED, true))
                .append(renderStatusFilterCheckbox(ExecutionStatus.UNKNOWN, true))
                .append("</div>")
                .append("</section>");
    }

    private String renderStatusFilterCheckbox(ExecutionStatus status, boolean checked) {
        return "<label><input class=\"status-filter\" type=\"checkbox\" value=\""
                + status.name()
                + "\""
                + (checked ? " checked" : "")
                + ">"
                + status.name()
                + "</label>";
    }

    private void appendSummary(RunSummary summary, StringBuilder html) {
        html.append("<section class=\"panel\" id=\"summary\">")
                .append("<h2>Run summary</h2>")
                .append("<div class=\"summary-grid\">")
                .append(summaryCard("Tests", String.valueOf(summary.tests())))
                .append(summaryCard("Passed", String.valueOf(summary.passed())))
                .append(summaryCard("Failures", String.valueOf(summary.failures())))
                .append(summaryCard("Errors", String.valueOf(summary.errors())))
                .append(summaryCard("Skipped", String.valueOf(summary.skipped())))
                .append(summaryCard("Duration", formatSeconds(summary.durationSeconds()) + " s"))
                .append("</div>")
                .append("</section>");
    }

    private void appendEnvironmentProperties(List<EnvironmentProperty> properties, StringBuilder html) {
        html.append("<section class=\"panel\" id=\"properties\">")
                .append("<h2>Environment properties (safe subset)</h2>");

        if (properties.isEmpty()) {
            html.append("<p>No safe property available.</p>")
                    .append("</section>");
            return;
        }

        html.append("<table>")
                .append("<thead><tr><th>Key</th><th>Value</th></tr></thead>")
                .append("<tbody>");

        for (EnvironmentProperty property : properties) {
            html.append("<tr><td>")
                    .append(escape(property.key()))
                    .append("</td><td>")
                    .append(escape(property.value()))
                    .append("</td></tr>");
        }

        html.append("</tbody></table>")
                .append("</section>");
    }

    private void appendWarnings(List<String> warnings, StringBuilder html) {
        if (warnings.isEmpty()) {
            return;
        }

        html.append("<section class=\"panel\" id=\"warnings\">")
                .append("<h2>Warnings</h2>")
                .append("<ul>");

        for (String warning : warnings) {
            html.append("<li>").append(escape(warning)).append("</li>");
        }

        html.append("</ul>")
                .append("</section>");
    }

    private void appendClassSections(List<TestClassReport> classes, Slugger slugger, StringBuilder html) {
        html.append("<section class=\"panel\" id=\"classes\">")
                .append("<h2>Class -> Factory -> Concrete case</h2>");

        for (TestClassReport classReport : classes) {
            String classKey = "class|" + classReport.className();
            String classId = slugger.idFor(classKey, classReport.className());
            String classAggregateClass = aggregateCssClass(classReport.counts());

            html.append("<section class=\"class-section\" id=\"")
                    .append(classId)
                    .append("\">")
                    .append("<div class=\"section-header\">")
                    .append("<h3>")
                    .append(escape(classReport.className()))
                    .append("</h3>")
                    .append("<div class=\"section-header-badges\">")
                    .append(renderAggregateBadge(classReport.counts()))
                    .append(renderCountsBadge(classReport.counts()))
                    .append("</div>")
                    .append("</div>")
                    .append("<p class=\"aggregate-hint ")
                    .append(classAggregateClass)
                    .append("\">Overall status: ")
                    .append(escape(aggregateLabel(classReport.counts())))
                    .append("</p>")
                    .append(renderCounts(classReport.counts(), classReport.totalDurationSeconds()));

            for (TestFactoryReport factoryReport : classReport.factories()) {
                String factoryKey = "factory|" + classReport.className() + "|" + factoryReport.factoryName();
                String factoryId = slugger.idFor(factoryKey, factoryReport.factoryName());
                String factoryAggregateClass = aggregateCssClass(factoryReport.counts());

                html.append("<section class=\"factory-block\" id=\"")
                        .append(factoryId)
                        .append("\">")
                        .append("<div class=\"section-header\">")
                        .append("<h4>")
                        .append(escape(factoryReport.factoryName()))
                        .append("</h4>")
                        .append("<div class=\"section-header-badges\">")
                        .append(renderAggregateBadge(factoryReport.counts()))
                        .append(renderCountsBadge(factoryReport.counts()))
                        .append("</div>")
                        .append("</div>")
                        .append("<p class=\"aggregate-hint ")
                        .append(factoryAggregateClass)
                        .append("\">Factory status: ")
                        .append(escape(aggregateLabel(factoryReport.counts())))
                        .append("</p>")
                        .append(renderCounts(factoryReport.counts(), factoryReport.totalDurationSeconds()));

                html.append("<table class=\"cases-table\">")
                        .append("<thead><tr><th>Test case</th><th>Status</th></tr></thead>")
                        .append("<tbody>");

                for (ConcreteCaseReport concreteCase : factoryReport.concreteCases()) {
                    String caseKey = "case|" + classReport.className() + "|" + factoryReport.factoryName() + "|" + concreteCase.id();
                    String caseId = slugger.idFor(caseKey, concreteCase.displayName());
                    String detailsRowId = caseId + "-details";

                    html.append("<tr class=\"case-row status-")
                            .append(concreteCase.status().cssClass())
                            .append("\" id=\"")
                            .append(caseId)
                            .append("\" data-status=\"")
                            .append(concreteCase.status().name())
                            .append("\" data-search=\"")
                            .append(escape(caseSearchText(classReport, factoryReport, concreteCase)))
                            .append("\" data-details-row-id=\"")
                            .append(detailsRowId)
                            .append("\">")
                            .append("<td class=\"case-name-cell\">")
                            .append("<button type=\"button\" class=\"case-toggle\" data-target=\"")
                            .append(detailsRowId)
                            .append("\" aria-expanded=\"false\" aria-label=\"Toggle details\">+</button>")
                            .append("<span class=\"case-name\">")
                            .append(escape(concreteCase.displayName()))
                            .append("</span>")
                            .append("</td>")
                            .append("<td>")
                            .append("<span class=\"status-tag status-")
                            .append(concreteCase.status().cssClass())
                            .append("\">")
                            .append(escape(concreteCase.status().name()))
                            .append("</span>")
                            .append("</td>")
                            .append("</tr>")
                            .append("<tr class=\"case-details-row hidden-by-toggle\" id=\"")
                            .append(detailsRowId)
                            .append("\">")
                            .append("<td colspan=\"2\">")
                            .append(renderCaseDetails(concreteCase))
                            .append("</td>")
                            .append("</tr>");
                }

                html.append("</tbody></table>");
                html.append("</section>");
            }

            html.append("</section>");
        }

        html.append("</section>");
    }

    private String caseSearchText(TestClassReport classReport, TestFactoryReport factoryReport, ConcreteCaseReport concreteCase) {
        return (
                classReport.className() + " "
                        + factoryReport.factoryName() + " "
                        + concreteCase.searchableText()
        ).toLowerCase(Locale.ROOT);
    }

    private String renderCaseDetails(ConcreteCaseReport concreteCase) {
        StringBuilder details = new StringBuilder(2048);
        details.append("<div class=\"case-details\">")
                .append("<p class=\"meta\">Testcase: ")
                .append(escape(concreteCase.sourceTestcaseName()))
                .append(" | index: ")
                .append(concreteCase.sourceIndex() == null ? "n/a" : concreteCase.sourceIndex())
                .append(" | duration: ")
                .append(formatSeconds(concreteCase.durationSeconds()))
                .append(" s</p>");

        if (concreteCase.failureMessage() != null || concreteCase.stackTrace() != null) {
            details.append("<div class=\"detail-block\">")
                    .append("<h5>Failure/Error details</h5>");

            if (concreteCase.failureType() != null) {
                details.append("<p><strong>Type:</strong> ")
                        .append(escape(concreteCase.failureType()))
                        .append("</p>");
            }
            if (concreteCase.failureMessage() != null) {
                details.append("<p><strong>Message:</strong></p><pre>")
                        .append(escape(concreteCase.failureMessage()))
                        .append("</pre>");
            }
            if (concreteCase.stackTrace() != null) {
                details.append("<p><strong>Stacktrace:</strong></p><pre>")
                        .append(escape(concreteCase.stackTrace()))
                        .append("</pre>");
            }

            details.append("</div>");
        }

        if (concreteCase.caseLog() != null && !concreteCase.caseLog().isBlank()) {
            details.append("<div class=\"detail-block\">")
                    .append("<h5>Case log</h5>")
                    .append("<pre>")
                    .append(escape(concreteCase.caseLog()))
                    .append("</pre>")
                    .append("</div>");
        }

        details.append("</div>");
        return details.toString();
    }

    private String aggregateCssClass(StatusCounts counts) {
        if (counts.errors() > 0) {
            return "aggregate-error";
        }
        if (counts.failed() > 0) {
            return "aggregate-failed";
        }
        if (counts.unknown() > 0 || counts.skipped() > 0) {
            return "aggregate-warning";
        }
        return "aggregate-passed";
    }

    private String aggregateLabel(StatusCounts counts) {
        String cssClass = aggregateCssClass(counts);
        return switch (cssClass) {
            case "aggregate-error" -> "ERROR";
            case "aggregate-failed" -> "FAILED";
            case "aggregate-warning" -> "WARNING";
            default -> "PASSED";
        };
    }

    private String renderAggregateBadge(StatusCounts counts) {
        return " <span class=\"aggregate-tag "
                + aggregateCssClass(counts)
                + "\">"
                + aggregateLabel(counts)
                + "</span>";
    }

    private String renderCounts(StatusCounts counts, double durationSeconds) {
        return "<p class=\"counts\">"
                + "Total: " + counts.total()
                + " | Passed: " + counts.passed()
                + " | Failed: " + counts.failed()
                + " | Errors: " + counts.errors()
                + " | Skipped: " + counts.skipped()
                + " | Unknown: " + counts.unknown()
                + " | Duration: " + formatSeconds(durationSeconds) + " s"
                + "</p>";
    }

    private String renderCountsBadge(StatusCounts counts) {
        return " <span class=\"mini-counts\">"
                + "F:" + counts.failed() + " "
                + "E:" + counts.errors() + " "
                + "U:" + counts.unknown() + " "
                + "S:" + counts.skipped()
                + "</span>";
    }

    private String summaryCard(String label, String value) {
        return "<article class=\"summary-card\"><h3>"
                + escape(label)
                + "</h3><p>"
                + escape(value)
                + "</p></article>";
    }

    private String shortName(String className) {
        int dot = className.lastIndexOf('.');
        return dot < 0 ? className : className.substring(dot + 1);
    }

    private String formatSeconds(double seconds) {
        return String.format(Locale.ROOT, "%.3f", seconds);
    }

    private String escape(String raw) {
        return RecursiveDtoHtmlRenderer.escapeHtml(raw);
    }

    private String css() {
        return """
                :root {
                    --bg: #f4f6f8;
                    --panel: #ffffff;
                    --text: #1f2937;
                    --muted: #667085;
                    --border: #d5dce5;
                    --link: #355e8a;
                    --pass: #4f8a5b;
                    --pass-soft: #eaf3ec;
                    --failed: #c85a5a;
                    --failed-soft: #fbeeee;
                    --error: #8f2d2d;
                    --error-soft: #f8eaea;
                    --warning: #b08a2e;
                    --warning-soft: #fbf6ea;
                    --skipped: #7f8794;
                }

                * {
                    box-sizing: border-box;
                }

                body {
                    margin: 0;
                    font-family: Arial, Helvetica, sans-serif;
                    background: var(--bg);
                    color: var(--text);
                }

                .layout {
                    min-height: 100vh;
                }

                /* Keep the old sidebar markup for fallback, but hide it by default. */
                .sidebar {
                    display: none;
                }

                .mini-counts {
                    font-size: 0.75rem;
                    color: var(--muted);
                    border: 1px solid var(--border);
                    background: #eef2f6;
                    border-radius: 999px;
                    padding: 0.1rem 0.45rem;
                    font-weight: 600;
                }

                .main {
                    padding: 1rem 1.5rem 2rem;
                    max-width: 1600px;
                    margin: 0 auto;
                }

                a {
                    color: var(--link);
                    text-decoration: none;
                }

                a:hover {
                    text-decoration: underline;
                }

                .page-header h1 {
                    margin: 0;
                }

                .generated-at {
                    color: var(--muted);
                    margin-top: 0.35rem;
                }

                .muted {
                    color: var(--muted);
                    margin: 0.2rem 0 0.8rem;
                }

                .panel {
                    background: var(--panel);
                    border-radius: 8px;
                    border: 1px solid var(--border);
                    margin: 1rem 0;
                    padding: 1rem;
                }

                .summary-grid {
                    display: grid;
                    grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
                    gap: 0.7rem;
                }

                .summary-card {
                    border: 1px solid var(--border);
                    border-radius: 8px;
                    padding: 0.6rem;
                    background: #f6f8fa;
                }

                .summary-card h3 {
                    margin: 0;
                    font-size: 0.9rem;
                    color: var(--muted);
                }

                .summary-card p {
                    margin: 0.45rem 0 0;
                    font-size: 1.1rem;
                    font-weight: 700;
                }

                .filters .filter-row {
                    margin: 0.5rem 0;
                    display: flex;
                    align-items: center;
                    gap: 0.7rem;
                    flex-wrap: wrap;
                }

                .filters input[type="text"] {
                    width: min(700px, 100%);
                    padding: 0.5rem;
                    border: 1px solid var(--border);
                    border-radius: 6px;
                }

                table {
                    width: 100%;
                    border-collapse: collapse;
                }

                th,
                td {
                    border: 1px solid var(--border);
                    padding: 0.4rem 0.5rem;
                    text-align: left;
                    vertical-align: top;
                    word-break: break-word;
                }

                .nav-tree,
                .nav-tree ul {
                    list-style: none;
                    margin: 0.35rem 0;
                    padding-left: 1rem;
                }

                .nav-tree {
                    padding-left: 0;
                }

                .nav-tree details > summary {
                    cursor: pointer;
                    padding: 0.42rem 0.55rem;
                    border-radius: 8px;
                    border: 1px solid var(--border);
                    background: #f8fafc;
                }

                .nav-tree details > summary:hover {
                    background: #f2f5f9;
                }

                .nav-entry {
                    display: inline-flex;
                    align-items: center;
                    flex-wrap: wrap;
                    gap: 0.45rem;
                }

                .nav-case-item {
                    display: flex;
                    align-items: center;
                    justify-content: space-between;
                    gap: 0.6rem;
                    border: 1px solid #e9edf3;
                    border-radius: 8px;
                    padding: 0.25rem 0.45rem;
                    margin: 0.3rem 0;
                    background: #ffffff;
                }

                .nav-case-item a {
                    flex: 1;
                    min-width: 0;
                    word-break: break-word;
                }

                .aggregate-tag {
                    border-radius: 999px;
                    padding: 0.13rem 0.48rem;
                    font-size: 0.71rem;
                    font-weight: 700;
                    letter-spacing: 0.01em;
                }

                .aggregate-passed {
                    color: var(--pass);
                }

                .aggregate-failed {
                    color: var(--failed);
                }

                .aggregate-error {
                    color: var(--error);
                }

                .aggregate-warning {
                    color: #7a6021;
                }

                .aggregate-tag.aggregate-passed {
                    background: var(--pass);
                    color: #ffffff;
                }

                .aggregate-tag.aggregate-failed {
                    background: var(--failed);
                    color: #ffffff;
                }

                .aggregate-tag.aggregate-error {
                    background: var(--error);
                    color: #ffffff;
                }

                .aggregate-tag.aggregate-warning {
                    background: var(--warning-soft);
                    color: #7a6021;
                    border: 1px solid #d7bf82;
                }

                .nav-class-item.aggregate-passed > details > summary,
                .nav-factory-item.aggregate-passed > details > summary {
                    background: var(--pass-soft);
                    border-color: #bfd6c3;
                }

                .nav-class-item.aggregate-failed > details > summary,
                .nav-factory-item.aggregate-failed > details > summary {
                    background: var(--failed-soft);
                    border-color: #e9bebe;
                }

                .nav-class-item.aggregate-error > details > summary,
                .nav-factory-item.aggregate-error > details > summary {
                    background: var(--error-soft);
                    border-color: #ddb2b2;
                }

                .nav-class-item.aggregate-warning > details > summary,
                .nav-factory-item.aggregate-warning > details > summary {
                    background: var(--warning-soft);
                    border-color: #e5d19c;
                }

                .class-section {
                    border-top: 2px solid #d8dee4;
                    padding-top: 0.7rem;
                    margin-top: 0.7rem;
                }

                .factory-block {
                    border: 1px solid var(--border);
                    border-radius: 8px;
                    padding: 0.6rem;
                    margin: 0.8rem 0;
                    background: #fcfcfd;
                }

                .section-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: flex-start;
                    gap: 0.7rem;
                    flex-wrap: wrap;
                }

                .section-header-badges {
                    display: flex;
                    align-items: center;
                    flex-wrap: wrap;
                    gap: 0.35rem;
                }

                .aggregate-hint {
                    margin: 0.2rem 0 0.4rem;
                    font-size: 0.84rem;
                    font-weight: 600;
                }

                .factory-block h4,
                .class-section h3,
                .detail-block h5 {
                    margin: 0 0 0.35rem 0;
                }

                .counts,
                .meta {
                    color: var(--muted);
                    font-size: 0.88rem;
                }

                .cases-table {
                    margin-top: 0.6rem;
                }

                .cases-table th {
                    background: #f5f8fb;
                    font-size: 0.87rem;
                }

                .case-row td {
                    background: #ffffff;
                }

                .case-row.status-passed td:first-child {
                    border-left: 4px solid var(--pass);
                }

                .case-row.status-failed td:first-child {
                    border-left: 4px solid var(--failed);
                }

                .case-row.status-error td:first-child {
                    border-left: 4px solid var(--error);
                }

                .case-row.status-skipped td:first-child {
                    border-left: 4px solid var(--skipped);
                }

                .case-row.status-unknown td:first-child {
                    border-left: 4px solid var(--warning);
                }

                .case-name-cell {
                    display: flex;
                    align-items: center;
                    gap: 0.55rem;
                }

                .case-name {
                    font-weight: 600;
                }

                .case-toggle {
                    width: 1.45rem;
                    height: 1.45rem;
                    border-radius: 4px;
                    border: 1px solid var(--border);
                    background: #f4f7fb;
                    color: #3b4e68;
                    font-weight: 700;
                    line-height: 1;
                    cursor: pointer;
                }

                .case-toggle:hover {
                    background: #eaf0f7;
                }

                .case-details-row td {
                    background: #f9fbfc;
                }

                .case-details {
                    padding: 0.2rem 0;
                }

                .detail-block {
                    margin-top: 0.65rem;
                }

                .status-tag {
                    border-radius: 999px;
                    padding: 0.15rem 0.5rem;
                    font-size: 0.72rem;
                    font-weight: 700;
                    display: inline-block;
                }

                .status-passed {
                    background: var(--pass);
                    color: #ffffff;
                }

                .status-failed {
                    background: var(--failed);
                    color: #ffffff;
                }

                .status-error {
                    background: var(--error);
                    color: #ffffff;
                }

                .status-skipped {
                    background: #e9edf2;
                    color: #4b5563;
                }

                .status-unknown {
                    background: var(--warning-soft);
                    color: #6f5515;
                    border: 1px solid #d7bf82;
                }

                pre {
                    white-space: pre-wrap;
                    background: #eef3f8;
                    color: #1f2937;
                    border: 1px solid var(--border);
                    border-radius: 6px;
                    padding: 0.6rem;
                    overflow-x: auto;
                }

                .hidden-by-filter,
                .hidden-by-toggle {
                    display: none !important;
                }

                .dto-dump ul,
                .dto-dump ol {
                    padding-left: 1.2rem;
                }

                .dto-key {
                    color: #356674;
                    font-weight: 600;
                }

                .null,
                .truncated,
                .circular,
                .error {
                    color: #b91c1c;
                }

                @media (max-width: 900px) {
                    .main {
                        padding: 0.8rem;
                    }

                    .nav-entry,
                    .section-header,
                    .section-header-badges {
                        align-items: flex-start;
                    }
                }
                """;
    }

    private String js() {
        return """
                (() => {
                    const searchInput = document.getElementById('search-input');
                    const statusCheckboxes = Array.from(document.querySelectorAll('.status-filter'));

                    const caseRows = Array.from(document.querySelectorAll('.case-row'));
                    const caseToggles = Array.from(document.querySelectorAll('.case-toggle'));
                    const factoryBlocks = Array.from(document.querySelectorAll('.factory-block'));
                    const classSections = Array.from(document.querySelectorAll('.class-section'));

                    const navCaseItems = Array.from(document.querySelectorAll('.nav-case-item'));
                    const navFactoryItems = Array.from(document.querySelectorAll('.nav-factory-item'));
                    const navClassItems = Array.from(document.querySelectorAll('.nav-class-item'));

                    const selectedStatuses = () => new Set(
                        statusCheckboxes.filter(input => input.checked).map(input => input.value)
                    );

                    const normalize = value => (value || '').toLowerCase();

                    function collapseDetailsForRow(row) {
                        const detailsId = row.dataset.detailsRowId;
                        if (!detailsId) {
                            return;
                        }

                        const detailsRow = document.getElementById(detailsId);
                        if (detailsRow) {
                            detailsRow.classList.add('hidden-by-toggle');
                        }

                        const toggle = row.querySelector('.case-toggle');
                        if (toggle) {
                            toggle.setAttribute('aria-expanded', 'false');
                            toggle.textContent = '+';
                        }
                    }

                    caseToggles.forEach(toggle => {
                        toggle.addEventListener('click', () => {
                            const detailsId = toggle.dataset.target;
                            if (!detailsId) {
                                return;
                            }

                            const detailsRow = document.getElementById(detailsId);
                            if (!detailsRow || detailsRow.classList.contains('hidden-by-filter')) {
                                return;
                            }

                            const expanded = toggle.getAttribute('aria-expanded') === 'true';
                            toggle.setAttribute('aria-expanded', expanded ? 'false' : 'true');
                            toggle.textContent = expanded ? '+' : '-';
                            detailsRow.classList.toggle('hidden-by-toggle', expanded);
                        });
                    });

                    function applyFilters() {
                        const text = normalize(searchInput.value.trim());
                        const statuses = selectedStatuses();
                        const visibleCaseIds = new Set();

                        caseRows.forEach(row => {
                            const status = row.dataset.status;
                            const searchable = normalize(row.dataset.search);
                            const statusMatch = statuses.has(status);
                            const textMatch = !text || searchable.includes(text);
                            const visible = statusMatch && textMatch;

                            row.classList.toggle('hidden-by-filter', !visible);

                            const detailsRowId = row.dataset.detailsRowId;
                            const detailsRow = detailsRowId ? document.getElementById(detailsRowId) : null;
                            if (detailsRow) {
                                detailsRow.classList.toggle('hidden-by-filter', !visible);
                            }

                            if (!visible) {
                                collapseDetailsForRow(row);
                            }

                            if (visible) {
                                visibleCaseIds.add(row.id);
                            }
                        });

                        factoryBlocks.forEach(factory => {
                            const hasVisibleCase = !!factory.querySelector('.case-row:not(.hidden-by-filter)');
                            factory.classList.toggle('hidden-by-filter', !hasVisibleCase);
                        });

                        classSections.forEach(section => {
                            const hasVisibleFactory = !!section.querySelector('.factory-block:not(.hidden-by-filter)');
                            section.classList.toggle('hidden-by-filter', !hasVisibleFactory);
                        });

                        navCaseItems.forEach(item => {
                            const ref = item.dataset.caseRef;
                            const visible = visibleCaseIds.has(ref);
                            item.classList.toggle('hidden-by-filter', !visible);
                        });

                        navFactoryItems.forEach(factory => {
                            const hasVisibleCase = !!factory.querySelector('.nav-case-item:not(.hidden-by-filter)');
                            factory.classList.toggle('hidden-by-filter', !hasVisibleCase);
                        });

                        navClassItems.forEach(clazz => {
                            const hasVisibleFactory = !!clazz.querySelector('.nav-factory-item:not(.hidden-by-filter)');
                            clazz.classList.toggle('hidden-by-filter', !hasVisibleFactory);
                        });
                    }

                    searchInput.addEventListener('input', applyFilters);
                    statusCheckboxes.forEach(input => input.addEventListener('change', applyFilters));

                    applyFilters();
                })();
                """;
    }

    private static final class Slugger {
        private final Map<String, String> idsByKey = new LinkedHashMap<>();
        private final Map<String, Integer> countersByBase = new LinkedHashMap<>();

        String idFor(String key, String suggestion) {
            return idsByKey.computeIfAbsent(key, ignored -> {
                String base = toSlug(suggestion);
                int counter = countersByBase.getOrDefault(base, 0);
                countersByBase.put(base, counter + 1);
                if (counter == 0) {
                    return base;
                }
                return base + "-" + counter;
            });
        }

        private String toSlug(String raw) {
            if (raw == null || raw.isBlank()) {
                return "node";
            }

            String lower = raw.toLowerCase(Locale.ROOT);
            StringBuilder slug = new StringBuilder(lower.length());
            boolean previousDash = false;
            for (int i = 0; i < lower.length(); i++) {
                char c = lower.charAt(i);
                boolean alnum = (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9');
                if (alnum) {
                    slug.append(c);
                    previousDash = false;
                } else if (!previousDash) {
                    slug.append('-');
                    previousDash = true;
                }
            }

            String cleaned = slug.toString().replaceAll("^-+", "").replaceAll("-+$", "");
            return cleaned.isBlank() ? "node" : cleaned;
        }
    }
}

