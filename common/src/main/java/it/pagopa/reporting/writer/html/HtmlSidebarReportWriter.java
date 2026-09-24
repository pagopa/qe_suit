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
                .append("<div class=\"layout\">");

        appendSidebar(report, slugger, html);

        html.append("<main class=\"main\">")
                .append("<header class=\"page-header\">")
                .append("<h1>Surefire HTML Report</h1>")
                .append("<p class=\"generated-at\">Generated at ")
                .append(escape(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .append("</p>")
                .append("</header>");

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
                .append("</div>")
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

            html.append("<section class=\"class-section\" id=\"")
                    .append(classId)
                    .append("\">")
                    .append("<h3>")
                    .append(escape(classReport.className()))
                    .append("</h3>")
                    .append(renderCounts(classReport.counts(), classReport.totalDurationSeconds()));

            for (TestFactoryReport factoryReport : classReport.factories()) {
                String factoryKey = "factory|" + classReport.className() + "|" + factoryReport.factoryName();
                String factoryId = slugger.idFor(factoryKey, factoryReport.factoryName());

                html.append("<section class=\"factory-block\" id=\"")
                        .append(factoryId)
                        .append("\">")
                        .append("<h4>")
                        .append(escape(factoryReport.factoryName()))
                        .append("</h4>")
                        .append(renderCounts(factoryReport.counts(), factoryReport.totalDurationSeconds()));

                for (ConcreteCaseReport concreteCase : factoryReport.concreteCases()) {
                    String caseKey = "case|" + classReport.className() + "|" + factoryReport.factoryName() + "|" + concreteCase.id();
                    String caseId = slugger.idFor(caseKey, concreteCase.displayName());

                    html.append("<article class=\"case-card status-")
                            .append(concreteCase.status().cssClass())
                            .append("\" id=\"")
                            .append(caseId)
                            .append("\" data-status=\"")
                            .append(concreteCase.status().name())
                            .append("\" data-search=\"")
                            .append(escape(caseSearchText(classReport, factoryReport, concreteCase)))
                            .append("\">")
                            .append("<div class=\"case-header\">")
                            .append("<h5>")
                            .append(escape(concreteCase.displayName()))
                            .append("</h5>")
                            .append("<span class=\"status-tag status-")
                            .append(concreteCase.status().cssClass())
                            .append("\">")
                            .append(escape(concreteCase.status().name()))
                            .append("</span>")
                            .append("</div>")
                            .append("<p class=\"meta\">Testcase: ")
                            .append(escape(concreteCase.sourceTestcaseName()))
                            .append(" | index: ")
                            .append(concreteCase.sourceIndex() == null ? "n/a" : concreteCase.sourceIndex())
                            .append(" | duration: ")
                            .append(formatSeconds(concreteCase.durationSeconds()))
                            .append(" s")
                            .append("</p>");

                    if (concreteCase.failureMessage() != null || concreteCase.stackTrace() != null) {
                        html.append("<details>")
                                .append("<summary>Failure/Error details</summary>");

                        if (concreteCase.failureType() != null) {
                            html.append("<p><strong>Type:</strong> ")
                                    .append(escape(concreteCase.failureType()))
                                    .append("</p>");
                        }
                        if (concreteCase.failureMessage() != null) {
                            html.append("<p><strong>Message:</strong> ")
                                    .append(escape(concreteCase.failureMessage()))
                                    .append("</p>");
                        }
                        if (concreteCase.stackTrace() != null) {
                            html.append("<pre>")
                                    .append(escape(concreteCase.stackTrace()))
                                    .append("</pre>");
                        }
                        html.append("</details>");
                    }

                    if (concreteCase.caseLog() != null && !concreteCase.caseLog().isBlank()) {
                        html.append("<details>")
                                .append("<summary>Case log</summary>")
                                .append("<pre>")
                                .append(escape(concreteCase.caseLog()))
                                .append("</pre>")
                                .append("</details>");
                    }

                    html.append("</article>");
                }

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
                + counts.total() + "T/"
                + counts.failed() + "F/"
                + counts.errors() + "E"
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
                    --bg: #f7f7f9;
                    --panel: #ffffff;
                    --text: #1f2328;
                    --muted: #5b6470;
                    --accent: #0969da;
                    --pass: #1a7f37;
                    --fail: #cf222e;
                    --error: #bc4c00;
                    --skip: #6e7781;
                    --unknown: #8250df;
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
                    display: grid;
                    grid-template-columns: 360px 1fr;
                    min-height: 100vh;
                }

                .sidebar {
                    background: #111827;
                    color: #f9fafb;
                    padding: 1rem;
                    overflow-y: auto;
                }

                .sidebar h2 {
                    margin-top: 0;
                    font-size: 1.1rem;
                }

                .sidebar a {
                    color: #dbeafe;
                    text-decoration: none;
                }

                .sidebar a:hover {
                    text-decoration: underline;
                }

                .sidebar ul {
                    padding-left: 1rem;
                    margin: 0.3rem 0;
                }

                .sidebar-tree {
                    list-style: none;
                    padding-left: 0;
                }

                .sidebar-tree li {
                    margin: 0.3rem 0;
                }

                .mini-counts {
                    color: #93c5fd;
                    font-size: 0.75rem;
                }

                .main {
                    padding: 1rem 1.5rem 2rem;
                }

                .page-header h1 {
                    margin: 0;
                }

                .generated-at {
                    color: var(--muted);
                    margin-top: 0.35rem;
                }

                .panel {
                    background: var(--panel);
                    border-radius: 8px;
                    border: 1px solid #d0d7de;
                    margin: 1rem 0;
                    padding: 1rem;
                }

                .summary-grid {
                    display: grid;
                    grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
                    gap: 0.7rem;
                }

                .summary-card {
                    border: 1px solid #d0d7de;
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
                    border: 1px solid #d0d7de;
                    border-radius: 6px;
                }

                table {
                    width: 100%;
                    border-collapse: collapse;
                }

                th,
                td {
                    border: 1px solid #d0d7de;
                    padding: 0.4rem 0.5rem;
                    text-align: left;
                    vertical-align: top;
                    word-break: break-word;
                }

                .class-section {
                    border-top: 2px solid #d8dee4;
                    padding-top: 0.7rem;
                    margin-top: 0.7rem;
                }

                .factory-block {
                    border: 1px solid #d0d7de;
                    border-radius: 8px;
                    padding: 0.6rem;
                    margin: 0.8rem 0;
                    background: #fcfcfd;
                }

                .factory-block h4,
                .class-section h3,
                .case-card h5 {
                    margin: 0 0 0.35rem 0;
                }

                .counts,
                .meta {
                    color: var(--muted);
                    font-size: 0.88rem;
                }

                .case-card {
                    border: 1px solid #d0d7de;
                    border-left: 5px solid #9ca3af;
                    border-radius: 8px;
                    padding: 0.6rem;
                    margin: 0.7rem 0;
                    background: #fff;
                }

                .case-card.status-passed {
                    border-left-color: var(--pass);
                }

                .case-card.status-failed {
                    border-left-color: var(--fail);
                }

                .case-card.status-error {
                    border-left-color: var(--error);
                }

                .case-card.status-skipped {
                    border-left-color: var(--skip);
                }

                .case-card.status-unknown {
                    border-left-color: var(--unknown);
                }

                .case-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: baseline;
                    gap: 0.8rem;
                }

                .status-tag {
                    border-radius: 999px;
                    padding: 0.15rem 0.5rem;
                    font-size: 0.72rem;
                    font-weight: 700;
                    color: #fff;
                }

                .status-passed {
                    background: var(--pass);
                }

                .status-failed {
                    background: var(--fail);
                }

                .status-error {
                    background: var(--error);
                }

                .status-skipped {
                    background: var(--skip);
                }

                .status-unknown {
                    background: var(--unknown);
                }

                pre {
                    white-space: pre-wrap;
                    background: #0f172a;
                    color: #e2e8f0;
                    border-radius: 6px;
                    padding: 0.6rem;
                    overflow-x: auto;
                }

                .hidden-by-filter {
                    display: none !important;
                }

                .dto-dump ul,
                .dto-dump ol {
                    padding-left: 1.2rem;
                }

                .dto-key {
                    color: #0f766e;
                    font-weight: 600;
                }

                .null,
                .truncated,
                .circular,
                .error {
                    color: #b91c1c;
                }

                @media (max-width: 1100px) {
                    .layout {
                        grid-template-columns: 1fr;
                    }

                    .sidebar {
                        max-height: 320px;
                    }
                }
                """;
    }

    private String js() {
        return """
                (() => {
                    const searchInput = document.getElementById('search-input');
                    const statusCheckboxes = Array.from(document.querySelectorAll('.status-filter'));

                    const caseCards = Array.from(document.querySelectorAll('.case-card'));
                    const factoryBlocks = Array.from(document.querySelectorAll('.factory-block'));
                    const classSections = Array.from(document.querySelectorAll('.class-section'));

                    const sidebarCaseItems = Array.from(document.querySelectorAll('.sidebar-case'));
                    const sidebarFactoryItems = Array.from(document.querySelectorAll('.sidebar-factory'));
                    const sidebarClassItems = Array.from(document.querySelectorAll('.sidebar-class'));

                    const selectedStatuses = () => new Set(
                        statusCheckboxes.filter(input => input.checked).map(input => input.value)
                    );

                    const normalize = value => (value || '').toLowerCase();

                    function applyFilters() {
                        const text = normalize(searchInput.value.trim());
                        const statuses = selectedStatuses();
                        const visibleCaseIds = new Set();

                        caseCards.forEach(card => {
                            const status = card.dataset.status;
                            const searchable = normalize(card.dataset.search);
                            const statusMatch = statuses.has(status);
                            const textMatch = !text || searchable.includes(text);
                            const visible = statusMatch && textMatch;
                            card.classList.toggle('hidden-by-filter', !visible);
                            if (visible) {
                                visibleCaseIds.add(card.id);
                            }
                        });

                        factoryBlocks.forEach(factory => {
                            const hasVisibleCase = !!factory.querySelector('.case-card:not(.hidden-by-filter)');
                            factory.classList.toggle('hidden-by-filter', !hasVisibleCase);
                        });

                        classSections.forEach(section => {
                            const hasVisibleFactory = !!section.querySelector('.factory-block:not(.hidden-by-filter)');
                            section.classList.toggle('hidden-by-filter', !hasVisibleFactory);
                        });

                        sidebarCaseItems.forEach(item => {
                            const ref = item.dataset.caseRef;
                            const visible = visibleCaseIds.has(ref);
                            item.classList.toggle('hidden-by-filter', !visible);
                        });

                        sidebarFactoryItems.forEach(factory => {
                            const hasVisibleCase = !!factory.querySelector('.sidebar-case:not(.hidden-by-filter)');
                            factory.classList.toggle('hidden-by-filter', !hasVisibleCase);
                        });

                        sidebarClassItems.forEach(clazz => {
                            const hasVisibleFactory = !!clazz.querySelector('.sidebar-factory:not(.hidden-by-filter)');
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

