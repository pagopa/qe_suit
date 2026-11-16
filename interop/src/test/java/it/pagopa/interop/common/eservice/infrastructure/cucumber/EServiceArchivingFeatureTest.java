package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import it.pagopa.infrastructure.cucumber.channel.ChannelScenarioExpander;
import it.pagopa.interop.common.infrastructure.cucumber.channel.InteropChannelEngineConfigs;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class EServiceArchivingFeatureTest {
    @Test
    void distinguishesWholeServiceArchivingFromAnOlderVersionStillLeavingTheServiceVisible() throws Exception {
        var feature = Path.of("src/main/resources/feature/catalog/private-catalog-visibility-and-roles.feature");
        var config = InteropChannelEngineConfigs.interopChannelModule();
        var expander = new ChannelScenarioExpander<Channel>(config.mapping(), config.defaultConfig());
        var expanded = expander.expand(feature, Files.readString(feature));
        var pickles = io.cucumber.gherkin.GherkinParser.builder().build()
                .parse(feature.toUri().toString(), expanded.getBytes(StandardCharsets.UTF_8))
                .flatMap(envelope -> envelope.getPickle().stream()).toList();

        var whole = pickles.stream()
                .filter(pickle -> pickle.getName().contains("PRIVATE_CATALOG_VISIBILITY_1_ARCHIVING")).toList();
        assertThat(whole).hasSize(2);
        for (var pickle : whole) {
            assertThat(pickle.getSteps()).extracting(step -> step.getText()).contains(
                    "l'e-service \"ES-1\" del Comune di Milano interamente in archiviazione",
                    "l'e-service \"ES-1\" non è presente tra i risultati della ricerca");
        }
        var partial = pickles.stream()
                .filter(pickle -> pickle.getName().contains("PRIVATE_CATALOG_VISIBILITY_1_SINGLE_ARCHIVING")).toList();
        assertThat(partial).hasSize(1);
        assertThat(partial.get(0).getSteps()).extracting(step -> step.getText()).contains(
                "la versione 1 dell'e-service \"ES-1\" del Comune di Milano deprecata",
                "la versione 1 dell'e-service \"ES-1\" del Comune di Milano in archiviazione",
                "l'e-service \"ES-1\" è presente tra i risultati della ricerca");
    }
}
