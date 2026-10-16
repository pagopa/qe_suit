package it.pagopa.interop.common.infrastructure.cucumber.hooks;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ScenarioMdcSupportTest {

    private static final Path OUTLINE_FEATURE =
            Path.of("src/main/resources/feature/dev-tools/debug-interop-client-assertion-dpop.feature");

    @Test
    @DisplayName("Numera le righe di Examples partendo da 1, ignorando l'header della tabella")
    void numbersExampleRows() throws IOException {
        List<String> lines = readFeature();

        assertThat(ScenarioMdcSupport.exampleIndex(lines, 68)).isEmpty();
        assertThat(ScenarioMdcSupport.exampleIndex(lines, 69)).contains(1);
        assertThat(ScenarioMdcSupport.exampleIndex(lines, 70)).contains(2);
        assertThat(ScenarioMdcSupport.exampleIndex(lines, 71)).contains(3);
        assertThat(ScenarioMdcSupport.exampleIndex(lines, 72)).contains(4);
    }

    @Test
    @DisplayName("Non numera le tabelle di dati degli step, che non appartengono a un blocco Examples")
    void ignoresStepDataTables() throws IOException {
        List<String> lines = readFeature();

        assertThat(ScenarioMdcSupport.exampleIndex(lines, 42)).isEmpty();
        assertThat(ScenarioMdcSupport.exampleIndex(lines, 65)).isEmpty();
    }

    @Test
    @DisplayName("Non numera righe che non sono righe di tabella o che sono fuori range")
    void ignoresNonTableLines() throws IOException {
        List<String> lines = readFeature();

        assertThat(ScenarioMdcSupport.exampleIndex(lines, 47)).isEmpty();
        assertThat(ScenarioMdcSupport.exampleIndex(lines, 0)).isEmpty();
        assertThat(ScenarioMdcSupport.exampleIndex(lines, lines.size() + 1)).isEmpty();
    }

    private static List<String> readFeature() throws IOException {
        return Files.readAllLines(OUTLINE_FEATURE, StandardCharsets.UTF_8);
    }
}
