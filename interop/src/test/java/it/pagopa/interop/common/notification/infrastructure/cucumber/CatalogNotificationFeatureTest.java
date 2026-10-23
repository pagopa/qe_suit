package it.pagopa.interop.common.notification.infrastructure.cucumber;

import io.cucumber.junit.platform.engine.CucumberTestEngine;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.infrastructure.cucumber.channel.InteropChannelEngineConfigs;
import it.pagopa.infrastructure.cucumber.channel.ChannelScenarioExpander;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.hierarchical.ExclusiveResource;
import org.junit.platform.engine.support.hierarchical.Node;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectFile;

class CatalogNotificationFeatureTest {
    private static final String TAG = "catalog-notification-settings";
    private static final String RESOURCE = "tenant-notification-settings";
    private Path feature() {
        return Path.of("src/main/resources/feature/catalog/private-catalog-visibility-and-roles.feature");
    }

    @Test
    void notificationScenariosDeclareExplicitConsumerAndNewPublishedDescriptor() throws Exception {
        var config = InteropChannelEngineConfigs.interopChannelModule();
        var expander = new ChannelScenarioExpander<Channel>(config.mapping(), config.defaultConfig());
        var expanded = expander.expand(feature(), Files.readString(feature()));
        var pickles = io.cucumber.gherkin.GherkinParser.builder().build()
                .parse(feature().toUri().toString(), expanded.getBytes(java.nio.charset.StandardCharsets.UTF_8))
                .flatMap(envelope -> envelope.getPickle().stream()).toList();
        var notifications = pickles.stream().filter(pickle -> pickle.getName().contains("PRIVATE_CATALOG_VISIBILITY_7")
                || pickle.getName().contains("PRIVATE_CATALOG_VISIBILITY_8")).toList();
        assertThat(notifications).hasSize(2);
        for (var pickle : notifications) {
            var steps = pickle.getSteps().stream().map(step -> step.getText()).toList();
            assertThat(steps).contains("il fruitore PagoPA è abilitato alle notifiche in-app",
                    "una notifica non letta generata per il fruitore PagoPA sull'e-service \"ES-1\"",
                    "la versione 2 dell'e-service \"ES-1\" del Comune di Milano pubblicata");
        }
    }

    @Test
    void cucumberEngineAssignsTheExclusiveReadWriteResourceOnlyToNotificationScenarios() throws Exception {
        var properties = new Properties();
        try (var input = Files.newInputStream(Path.of("src/main/resources/junit-platform.properties"))) {
            properties.load(input);
        }
        var builder = LauncherDiscoveryRequestBuilder.request();
        properties.forEach((key, value) -> builder.configurationParameter(key.toString(), value.toString()));
        builder.selectors(selectFile(feature().toFile()));
        TestDescriptor root = new CucumberTestEngine().discover(builder.build(), UniqueId.forEngine("cucumber"));
        Set<ExclusiveResource> expected = Set.of(
                new ExclusiveResource(RESOURCE, ExclusiveResource.LockMode.READ_WRITE));
        Set<ExclusiveResource> producerSettings = Set.of(
                new ExclusiveResource("tenant-delegation-settings", ExclusiveResource.LockMode.READ_WRITE));
        var tests = root.getDescendants().stream().filter(TestDescriptor::isTest).toList();
        int locked = 0;
        for (var test : tests) {
            boolean tagged = test.getTags().stream().anyMatch(tag -> tag.getName().equals(TAG));
            boolean producerTagged = test.getTags().stream()
                    .anyMatch(tag -> tag.getName().equals("producer-delegation-settings"));
            assertThat(((Node<?>) test).getExclusiveResources())
                    .isEqualTo(tagged ? expected : producerTagged ? producerSettings : Set.of());
            if (tagged) locked++;
        }
        assertThat(locked).isEqualTo(2);
        assertThat(tests.size()).isGreaterThan(locked);
    }
}
