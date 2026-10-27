package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.gherkin.GherkinParser;
import io.cucumber.messages.types.Envelope;
import it.pagopa.infrastructure.cucumber.channel.ChannelScenarioExpander;
import it.pagopa.interop.common.infrastructure.cucumber.channel.InteropChannelEngineConfigs;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class PrivateCatalogAttributeFixtureFeatureTest {
    private static final Path FEATURE =
            Path.of("src/main/resources/feature/catalog/private-catalog-high-impact-filters.feature");

    @Test
    void certifiedAttributeScenariosDeclareTheirOwnAliasesAndAssignments() throws Exception {
        var config = InteropChannelEngineConfigs.interopChannelModule();
        var expander = new ChannelScenarioExpander<Channel>(config.mapping(), config.defaultConfig());
        String expanded = expander.expand(FEATURE, Files.readString(FEATURE));
        var envelopes = GherkinParser.builder().build()
                .parse(FEATURE.toUri().toString(), expanded.getBytes(StandardCharsets.UTF_8)).toList();
        assertThat(envelopes.stream().filter(envelope -> envelope.getParseError().isPresent())).isEmpty();

        var scenarios = envelopes.stream().map(Envelope::getPickle).flatMap(java.util.Optional::stream)
                .filter(pickle -> pickle.getName().matches(
                        ".*PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_(7[A-C]|8[A-B]).*"))
                .toList();
        assertThat(scenarios).hasSize(5);
        for (var scenario : scenarios) {
            var steps = scenario.getSteps().stream().map(step -> step.getText()).toList();
            assertThat(steps).anyMatch(step -> step.contains("crea i seguenti attributi certificati"));
            if (scenario.getName().contains("FILTERS_7B")) {
                assertThat(steps).noneMatch(step -> step.contains("assegna a "));
            } else {
                assertThat(steps).anyMatch(step -> step.contains("assegna a "));
            }
            assertThat(steps).anyMatch(step -> step.contains("configurata con i seguenti attributi richiesti"));
            assertThat(steps).noneMatch(step -> step.contains("un attributo posseduto da PagoPA")
                    || step.contains("un attributo non posseduto da PagoPA"));
        }
    }

    @Test
    void otherKindsUseFreshExplicitAliasesWithoutRequesterAssignmentsOrLegacySelectors() throws Exception {
        String feature = Files.readString(FEATURE);
        var envelopes = GherkinParser.builder().build()
                .parse(FEATURE.toUri().toString(), feature.getBytes(StandardCharsets.UTF_8)).toList();
        assertThat(envelopes.stream().filter(envelope -> envelope.getParseError().isPresent())).isEmpty();
        var scenarios = envelopes.stream().map(Envelope::getPickle).flatMap(java.util.Optional::stream)
                .filter(pickle -> pickle.getName().matches(".*PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_(9|10)\\].*"))
                .toList();
        assertThat(scenarios).hasSize(2);
        for (var scenario : scenarios) {
            var steps = scenario.getSteps().stream().map(step -> step.getText()).toList();
            assertThat(steps).noneMatch(step -> step.contains("assegna"));
            if (scenario.getName().contains("FILTERS_9]")) {
                assertThat(steps).anyMatch(step -> step.endsWith("crea i seguenti attributi dichiarati:"));
                assertThat(steps).anyMatch(step -> step.endsWith("crea i seguenti attributi verificati:"));
            } else {
                assertThat(steps).anyMatch(step -> step.endsWith("crea i seguenti attributi discreti:"));
                assertThat(scenario.getTags()).extracting(tag -> tag.getName()).contains("@flag-discrete-attribute-on");
            }
            for (var step : scenario.getSteps()) {
                step.getArgument().flatMap(argument -> argument.getDataTable()).ifPresent(table ->
                        assertThat(table.getRows().stream().flatMap(row -> row.getCells().stream())
                                .map(cell -> cell.getValue()))
                                .noneMatch(value -> value.contains("un attributo")));
            }
        }
    }
}
