package it.pagopa.interop.common.infrastructure.reporting.contract.renderer;

import it.pagopa.interop.common.infrastructure.reporting.contract.config.ContractTargetType;
import it.pagopa.interop.common.infrastructure.reporting.contract.model.ContractGroup;
import it.pagopa.interop.common.infrastructure.reporting.contract.model.ContractReport;
import it.pagopa.interop.common.infrastructure.reporting.contract.model.ContractReportChannel;
import it.pagopa.interop.common.infrastructure.reporting.contract.model.ContractScenario;
import it.pagopa.interop.common.infrastructure.reporting.contract.model.ContractScenarioStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ContractReportRendererTest {

    @TempDir
    Path tempDir;

    @Test
    void rendersPrototypeSectionsAndEscapesRuntimeValues() throws Exception {
        ContractScenario scenario = new ContractScenario(
                "[engine:x]/[dynamic-test:#1]",
                "scenario <>&\"'",
                ContractScenarioStatus.FAILED,
                Instant.parse("2026-09-25T08:00:00Z"),
                Instant.parse("2026-09-25T08:00:01Z"),
                Duration.ofSeconds(1),
                false,
                "java.lang.AssertionError",
                "message <>&\"'",
                "stack <>&\"'",
                null,
                0
        );
        ContractGroup group = new ContractGroup(
                "bff",
                "BFF",
                ContractTargetType.OPENAPI,
                "POST /agreements <>&\"'",
                "it.pagopa.interop.suite.contract.BffAgreementContractTest",
                "createAgreement",
                "bff post /agreements createagreement scenario",
                ContractScenarioStatus.FAILED,
                1,
                List.of(scenario)
        );
        ContractReport report = new ContractReport(
                Instant.parse("2026-09-25T08:10:00Z"),
                Instant.parse("2026-09-25T08:00:00Z"),
                Instant.parse("2026-09-25T08:00:01Z"),
                Duration.ofSeconds(1),
                1,
                0,
                1,
                0,
                0,
                0,
                List.of(new ContractReportChannel("bff", "BFF")),
                List.of(group)
        );

        Path output = tempDir.resolve("report.html");
        new ContractReportRenderer().render(report, output);
        String html = Files.readString(output);

        assertTrue(html.contains("QA · Contract testing"));
        assertTrue(html.contains("Contract Test Report"));
        assertTrue(html.contains("Target sotto test"));
        assertTrue(html.contains("Dettagli tecnici"));
        assertTrue(html.contains("Nessun risultato corrisponde ai filtri selezionati."));
        assertTrue(html.contains("uniqueId"));
        assertTrue(html.contains("&lt;"));
        assertTrue(html.contains("&gt;"));
        assertTrue(html.contains("&amp;"));
    }
}
