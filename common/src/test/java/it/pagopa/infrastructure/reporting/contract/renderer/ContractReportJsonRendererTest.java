package it.pagopa.infrastructure.reporting.contract.renderer;

import it.pagopa.infrastructure.reporting.contract.config.ContractTargetType;
import it.pagopa.infrastructure.reporting.contract.model.*;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ContractReportJsonRendererTest {
    @Test
    void exportsAllScenarioDetailsAndStableIdentifiers() {
        ContractScenario failed = new ContractScenario("[engine:junit-jupiter]/[dynamic-test:#1]",
                "[payload] REPLACED_WITH_NULL @ /name", ContractScenarioStatus.FAILED,
                null, null, Duration.ofMillis(120), false,
                "AssertionError", "expected 400", "AssertionError: expected 400", null, 0);
        ContractScenario passed = new ContractScenario("[engine:junit-jupiter]/[dynamic-test:#2]",
                "valid payload", ContractScenarioStatus.PASSED,
                null, null, Duration.ofMillis(100), false,
                null, null, null, null, 1);
        ContractGroup group = new ContractGroup("bff", "BFF", ContractTargetType.OPENAPI,
                "POST /agreements", "it.pagopa.contract.BffAgreementContractTest", "createAgreement",
                "search", ContractScenarioStatus.FAILED, 2, List.of(failed, passed));
        ContractReport report = new ContractReport(Instant.parse("2026-11-16T20:38:59Z"), null, null,
                Duration.ofMillis(220), 2, 1, 1, 0, 0, 0,
                List.of(new ContractReportChannel("bff", "BFF")), List.of(group));
        Map<String, Object> json = new ContractReportJsonRenderer().toDocument(report);
        assertEquals(1, json.get("schemaVersion"));
        @SuppressWarnings("unchecked")
        Map<String, Object> summary = (Map<String, Object>) json.get("summary");
        assertEquals(1, summary.get("failed"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> groups = (List<Map<String, Object>>) json.get("groups");
        assertEquals("POST /agreements", groups.get(0).get("target"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> scenarios = (List<Map<String, Object>>) groups.get(0).get("scenarios");
        assertEquals("BffAgreementContractTest::createAgreement::[payload] REPLACED_WITH_NULL @ /name",
                scenarios.get(0).get("id_test"));
        assertEquals(2, scenarios.size());
    }

    @Test
    void rejectsDisagreeingSummary() {
        ContractReport report = new ContractReport(null, null, null, Duration.ZERO,
                1, 0, 1, 0, 0, 0, List.of(), List.of());
        assertThrows(IllegalStateException.class,
                () -> new ContractReportJsonRenderer().toDocument(report));
    }
}
