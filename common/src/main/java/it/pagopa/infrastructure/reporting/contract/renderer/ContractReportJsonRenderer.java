package it.pagopa.infrastructure.reporting.contract.renderer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import it.pagopa.infrastructure.reporting.contract.model.ContractGroup;
import it.pagopa.infrastructure.reporting.contract.model.ContractReport;
import it.pagopa.infrastructure.reporting.contract.model.ContractScenario;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Versioned machine-readable representation of the SAME ContractReport used by HTML.
 */
public final class ContractReportJsonRenderer {
    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public void render(ContractReport report, Path outputPath) {
        Map<String, Object> document = toDocument(report);
        try {
            Files.createDirectories(outputPath.toAbsolutePath().getParent());
            mapper.writeValue(outputPath.toFile(), document);
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot write contract report JSON: " + outputPath, exception);
        }
    }

    public Map<String, Object> toDocument(ContractReport report) {
        Map<String, Object> document = new LinkedHashMap<>();
        document.put("schemaVersion", 1);
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("total", report.total());
        summary.put("passed", report.passed());
        summary.put("failed", report.failed());
        summary.put("skipped", report.skipped());
        summary.put("aborted", report.aborted());
        summary.put("preconditionFailed", report.preconditionFailedCount());
        summary.put("generatedAt", iso(report.generatedAt()));
        summary.put("startedAt", iso(report.startedAt()));
        summary.put("finishedAt", iso(report.finishedAt()));
        summary.put("effectiveDurationMs", millis(report.effectiveDuration()));
        document.put("summary", summary);

        List<Map<String, Object>> channels = new ArrayList<>();
        report.channels().forEach(channel -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("key", channel.key());
            item.put("label", channel.label());
            channels.add(item);
        });
        document.put("channels", channels);

        List<Map<String, Object>> groups = new ArrayList<>();
        int total = 0;
        int failed = 0;
        int passed = 0;
        int skipped = 0;
        int aborted = 0;
        int preconditions = 0;
        for (ContractGroup group : report.groups()) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("channel", group.channelLabel());
            entry.put("channelKey", group.channelKey());
            entry.put("targetType", group.targetType().name());
            entry.put("target", group.target());
            entry.put("className", group.contractClassName());
            entry.put("methodName", group.methodName());
            entry.put("searchText", group.searchText());
            entry.put("status", group.aggregateStatus().name());
            entry.put("scenarioCount", group.scenarioCount());
            List<Map<String, Object>> scenarios = new ArrayList<>();
            for (ContractScenario scenario : group.scenarios()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("id_test", id(group, scenario));
                item.put("uniqueId", scenario.uniqueId());
                item.put("name", scenario.displayName());
                item.put("status", scenario.status().name());
                item.put("preconditionFailure", scenario.preconditionFailure());
                item.put("failureType", scenario.failureType());
                item.put("failureMessage", scenario.failureMessage());
                item.put("stackTrace", scenario.stackTrace());
                item.put("skipReason", scenario.skipReason());
                item.put("startedAt", iso(scenario.startedAt()));
                item.put("finishedAt", iso(scenario.finishedAt()));
                item.put("durationMs", millis(scenario.duration()));
                item.put("discoveryOrder", scenario.discoveryOrder());
                scenarios.add(item);
                total++;
                if (scenario.preconditionFailure()) preconditions++;
                switch (scenario.status()) {
                    case FAILED -> failed++;
                    case PASSED -> passed++;
                    case SKIPPED -> skipped++;
                    case ABORTED -> aborted++;
                }
            }
            if (group.scenarioCount() != scenarios.size()) {
                throw new IllegalStateException("Contract group count mismatch: " + group.methodName());
            }
            entry.put("scenarios", scenarios);
            groups.add(entry);
        }
        if (total != report.total() || failed != report.failed()
                || passed != report.passed() || skipped != report.skipped()
                || aborted != report.aborted() || preconditions != report.preconditionFailedCount()) {
            throw new IllegalStateException("ContractReport summary disagrees with scenario data");
        }
        document.put("groups", groups);
        return document;
    }

    private String id(ContractGroup group, ContractScenario scenario) {
        String fullClassName = group.contractClassName();
        String className = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
        return className + "::" + group.methodName() + "::"
                + (scenario.preconditionFailure() ? "Precondition failed" : scenario.displayName());
    }

    private static String iso(Instant timestamp) {
        return timestamp == null ? null : timestamp.toString();
    }

    private static Long millis(Duration duration) {
        return duration == null ? null : duration.toMillis();
    }
}
