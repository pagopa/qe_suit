package it.pagopa.interop.common.tenant.infrastructure.cucumber;

import io.cucumber.gherkin.GherkinParser;
import io.cucumber.junit.platform.engine.CucumberTestEngine;
import io.cucumber.messages.types.Envelope;
import it.pagopa.infrastructure.cucumber.channel.ChannelScenarioExpander;
import it.pagopa.interop.common.infrastructure.cucumber.channel.InteropChannelEngineConfigs;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.hierarchical.ExclusiveResource;
import org.junit.platform.engine.support.hierarchical.Node;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectFile;

class TenantDelegationSettingsFeatureTest {
    private static final String TAG = "producer-delegation-settings";
    private static final String PREREQUISITE = TenantDelegationSettingsStepsTest.PREREQUISITE;
    private static final String[] FEATURES = {
            "private-catalog-delegations.feature",
            "private-catalog-visibility-and-roles.feature",
            "private-catalog-filter-combinations.feature"
    };

    @Test
    void expandedFeaturesKeepExplicitSetupAndTagOnlyScenariosUsingDelegations() throws Exception {
        var config = InteropChannelEngineConfigs.interopChannelModule();
        var expander = new ChannelScenarioExpander<Channel>(config.mapping(), config.defaultConfig());
        int relevant = 0;
        for (String feature : FEATURES) {
            Path path = feature(feature);
            String expanded = expander.expand(path, Files.readString(path));
            var envelopes = GherkinParser.builder().build()
                    .parse(path.toUri().toString(), expanded.getBytes(StandardCharsets.UTF_8)).toList();
            assertThat(envelopes.stream().filter(envelope -> envelope.getParseError().isPresent())).isEmpty();
            var pickles = envelopes.stream().map(Envelope::getPickle).flatMap(java.util.Optional::stream).toList();
            assertThat(pickles).isNotEmpty();
            for (var pickle : pickles) {
                boolean delegates = pickle.getSteps().stream()
                        .anyMatch(step -> step.getText().contains("delegato in erogazione"));
                var prerequisites = pickle.getSteps().stream()
                        .filter(step -> step.getText().equals(PREREQUISITE)).toList();
                assertThat(pickle.getTags().stream().anyMatch(tag -> tag.getName().equals("@" + TAG)))
                        .as(pickle.getName()).isEqualTo(delegates);
                if (delegates) {
                    relevant++;
                    assertThat(prerequisites).hasSize(1);
                    assertThat(pickle.getSteps().get(0).getText()).isEqualTo(PREREQUISITE);
                    var rows = prerequisites.get(0).getArgument().orElseThrow().getDataTable().orElseThrow().getRows();
                    assertThat(rows.get(0).getCells().get(0).getValue()).isEqualTo("ente");
                    assertThat(rows.stream().skip(1).map(row -> row.getCells().get(0).getValue()))
                            .contains("Comune di Pozzallo");
                } else {
                    assertThat(prerequisites).isEmpty();
                }
            }
        }
        assertThat(relevant).isEqualTo(17);
    }

    @Test
    void installedCucumberEngineDiscoversTheActualReadWriteResourceWithoutExecutingScenarios() throws Exception {
        var properties = new Properties();
        try (var input = Files.newInputStream(Path.of("src/main/resources/junit-platform.properties"))) {
            properties.load(input);
        }
        var builder = LauncherDiscoveryRequestBuilder.request();
        properties.forEach((key, value) -> builder.configurationParameter(key.toString(), value.toString()));
        for (String feature : FEATURES) {
            builder.selectors(selectFile(feature(feature).toFile()));
        }
        TestDescriptor root = new CucumberTestEngine().discover(builder.build(), UniqueId.forEngine("cucumber"));
        Set<ExclusiveResource> expected = Set.of(new ExclusiveResource(
                "tenant-delegation-settings", ExclusiveResource.LockMode.READ_WRITE));
        var tests = root.getDescendants().stream().filter(TestDescriptor::isTest).collect(Collectors.toList());
        assertThat(tests).isNotEmpty();
        int locked = 0;
        for (var test : tests) {
            boolean tagged = test.getTags().stream().anyMatch(tag -> tag.getName().equals(TAG));
            assertThat(((Node<?>) test).getExclusiveResources()).isEqualTo(tagged ? expected : Set.of());
            if (tagged) {
                locked++;
            }
        }
        assertThat(locked).isEqualTo(17);
        assertThat(tests.size()).isGreaterThan(locked);
    }

    private Path feature(String filename) {
        return Path.of("src/main/resources/feature/catalog", filename);
    }
}
