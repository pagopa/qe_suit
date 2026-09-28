package it.pagopa.interop.common.infrastructure.cucumber.hooks;

import io.cucumber.java.Scenario;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Deriva dai metadati di uno {@link Scenario} i valori che finiscono nell'MDC.
 * <p>
 * Serve a rendere i log raggruppabili per caso di test senza dover parsare
 * il testo libero dei messaggi.
 */
final class ScenarioMdcSupport {

    private static final Pattern TEST_CASE_ID = Pattern.compile("\\[([^\\[\\]]+)]");
    private static final Map<URI, List<String>> FEATURE_CACHE = new ConcurrentHashMap<>();

    private ScenarioMdcSupport() {
    }

    /**
     * Identificativo racchiuso tra parentesi quadre nel titolo dello scenario
     * (convenzione dei feature file del progetto). In assenza, ripiega sul titolo.
     */
    static String testCaseId(Scenario scenario) {
        Matcher matcher = TEST_CASE_ID.matcher(scenario.getName());
        return matcher.find() ? matcher.group(1) : scenario.getName();
    }

    static String executionId(Scenario scenario) {
        String testCaseId = testCaseId(scenario);

        return exampleIndex(scenario)
                .map(index -> testCaseId + " - Example " + index)
                .orElse(testCaseId);
    }

    /** Nome del feature file, senza path, per identificare l'origine del test. */
    static String sourceFile(Scenario scenario) {
        String path = scenario.getUri().getSchemeSpecificPart();
        int lastSeparator = path.lastIndexOf('/');
        return lastSeparator < 0 ? path : path.substring(lastSeparator + 1);
    }

    /**
     * Titolo dello scenario, arricchito con il numero della riga di Examples
     * quando si tratta di uno Scenario Outline.
     */
    static String scenarioName(Scenario scenario) {
        return exampleIndex(scenario)
                .map(index -> scenario.getName() + " - Example " + index)
                .orElseGet(scenario::getName);
    }

    /**
     * Posizione (1-based) della riga di Examples che ha generato lo scenario.
     * Vuoto per gli scenari semplici o se il feature file non e' leggibile.
     */
    private static java.util.Optional<Integer> exampleIndex(Scenario scenario) {
        Integer line = scenario.getLine();
        return line == null
                ? java.util.Optional.empty()
                : exampleIndex(featureLines(scenario.getUri()), line);
    }

    /** Logica pura, isolata per poter essere verificata sui feature file reali. */
    static java.util.Optional<Integer> exampleIndex(List<String> lines, int lineNumber) {
        if (lineNumber <= 0 || lineNumber > lines.size()) {
            return java.util.Optional.empty();
        }
        if (!lines.get(lineNumber - 1).trim().startsWith("|")) {
            return java.util.Optional.empty();
        }
        if (!lines.get(lineNumber - 1).trim().startsWith("|")) {
            return java.util.Optional.empty();
        }

        // Risale fino all'header "Examples:" contando le righe di tabella incontrate:
        // la prima e' l'intestazione, quindi il contatore coincide con il numero di Example.
        int tableRows = 0;
        for (int cursor = lineNumber - 2; cursor >= 0; cursor--) {
            String current = lines.get(cursor).trim();
            if (current.startsWith("|")) {
                tableRows++;
            } else if (current.startsWith("Examples:") || current.startsWith("Scenari:")) {
                return tableRows == 0 ? java.util.Optional.empty() : java.util.Optional.of(tableRows);
            } else if (!current.isEmpty() && !current.startsWith("#") && !current.startsWith("@")) {
                return java.util.Optional.empty();
            }
        }
        return java.util.Optional.empty();
    }

    private static List<String> featureLines(URI uri) {
        return FEATURE_CACHE.computeIfAbsent(uri, ScenarioMdcSupport::readLines);
    }

    private static List<String> readLines(URI uri) {
        try {
            if ("classpath".equals(uri.getScheme())) {
                String resource = uri.getSchemeSpecificPart();
                try (InputStream stream = Thread.currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream(resource.startsWith("/") ? resource.substring(1) : resource)) {
                    if (stream == null) {
                        return List.of();
                    }
                    return new String(stream.readAllBytes(), StandardCharsets.UTF_8).lines().toList();
                }
            }
            return Files.readAllLines(Paths.get(uri), StandardCharsets.UTF_8);
        } catch (IOException | RuntimeException exception) {
            return List.of();
        }
    }
}
