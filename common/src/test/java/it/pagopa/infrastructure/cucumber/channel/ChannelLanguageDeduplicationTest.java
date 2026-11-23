package it.pagopa.infrastructure.cucumber.channel;

import it.pagopa.application.ChannelKind;
import it.pagopa.infrastructure.context.CucumberCurrentChannel;
import it.pagopa.infrastructure.context.InMemoryCurrentChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ChannelLanguageDeduplicationTest {
    private ChannelTagParser<TestChannel> parser;
    private ChannelScenarioExpander<TestChannel> expander;
    private ChannelGherkinMapping<TestChannel> mapping;
    private final ChannelConfig<TestChannel> defaultConfig =
            new ChannelConfig<>(TestChannel.BFF, TestChannel.BFF, TestChannel.BFF);

    @BeforeEach
    void setUp() {
        mapping = new ChannelGherkinMapping<>(
                Map.of("BFF", TestChannel.BFF, "WEB", TestChannel.WEB),
                Map.of(TestChannel.BFF, "BFF", TestChannel.WEB, "WEB"));
        parser = new ChannelTagParser<>(mapping);
        expander = new ChannelScenarioExpander<>(mapping, defaultConfig);
    }

    @Test
    void missingLanguageDefaultsToItalian() {
        ChannelConfig<TestChannel> config = parser.parseSingleTag(tag("BFF", "BFF", "BFF"));
        assertThat(config.givenLanguage()).isEqualTo(Locale.ITALIAN);
        assertThat(config.whenLanguage()).isEqualTo(Locale.ITALIAN);
        assertThat(config.thenLanguage()).isEqualTo(Locale.ITALIAN);
    }

    @Test
    void parsesIndependentLanguageForEachPhase() {
        ChannelConfig<TestChannel> config = parser.parseSingleTag(tag("BFF:it", "BFF:en", "WEB:it"));
        assertThat(config.givenLanguage()).isEqualTo(Locale.ITALIAN);
        assertThat(config.whenLanguage()).isEqualTo(Locale.ENGLISH);
        assertThat(config.thenLanguage()).isEqualTo(Locale.ITALIAN);
        assertThat(config.then()).isEqualTo(TestChannel.WEB);
    }

    @Test
    void missingAndExplicitItalianAreEquivalent() {
        assertThat(parser.parseSingleTag(tag("BFF", "BFF", "BFF")))
                .isEqualTo(parser.parseSingleTag(tag("BFF:it", "BFF:it", "BFF:it")));
    }

    @Test
    void repeatedIdenticalConfigurationGeneratesOneScenario() {
        assertExpandedCount(List.of(tag("BFF:it", "BFF:en", "BFF:it"),
                tag("BFF:it", "BFF:en", "BFF:it")), 1);
    }

    @Test
    void implicitAndExplicitItalianGenerateOneScenario() {
        String expanded = expand(List.of(tag("BFF", "BFF", "BFF"),
                tag("BFF:it", "BFF:it", "BFF:it")));
        assertThat(count(expanded, "Scenario: X")).isEqualTo(1);
        assertThat(expanded).contains("Scenario: X [Given=BFF,When=BFF,Then=BFF]");
    }

    @Test
    void whenLanguageDifferenceGeneratesTwoIndependentScenarios() {
        String expanded = expand(List.of(tag("BFF:it", "BFF:it", "BFF:it"),
                tag("BFF:it", "BFF:en", "BFF:it")));
        assertThat(count(expanded, "Scenario: X")).isEqualTo(2);
        assertThat(expanded).contains("Scenario: X [Given=BFF,When=BFF,Then=BFF]");
        assertThat(expanded).contains("Scenario: X [Given=BFF:it,When=BFF:en,Then=BFF:it]");
        assertThat(count(expanded, "@channel:Given=")).isEqualTo(2);
    }

    @Test
    void channelDifferenceGeneratesTwoScenarios() {
        assertExpandedCount(List.of(tag("BFF", "BFF", "BFF"),
                tag("BFF", "WEB", "BFF")), 2);
    }

    @Test
    void duplicateAmongThreeConfigsGeneratesTwoScenariosInOriginalOrder() {
        String expanded = expand(List.of(tag("BFF", "BFF", "BFF"),
                tag("BFF:it", "BFF:en", "BFF:it"),
                tag("BFF:it", "BFF:it", "BFF:it")));
        assertThat(count(expanded, "Scenario: X")).isEqualTo(2);
        assertThat(expanded.indexOf("X [Given=BFF,When=BFF,Then=BFF]"))
                .isLessThan(expanded.indexOf("X [Given=BFF:it,When=BFF:en,Then=BFF:it]"));
    }

    @Test
    void featureLevelDuplicatesAlsoGenerateOneScenario() {
        String source = tag("BFF", "BFF", "BFF") + "\n"
                + tag("BFF:it", "BFF:it", "BFF:it") + "\n"
                + "Feature: F\n  Scenario: X\n    Given a\n";
        assertThat(count(expander.expand(Path.of("test.feature"), source), "Scenario: X")).isEqualTo(1);
    }

    @Test
    void invalidLanguageIsRejected() {
        assertThatThrownBy(() -> parser.parseSingleTag(tag("BFF:it", "BFF:", "BFF:it")))
                .isInstanceOf(ChannelTagParser.ChannelTagParseException.class);
        assertThatThrownBy(() -> parser.parseSingleTag(tag("BFF:it", "BFF:en:us", "BFF:it")))
                .isInstanceOf(ChannelTagParser.ChannelTagParseException.class);
    }

    @Test
    void engineChangesLanguageWithStepAndPreservesItOnAnd() {
        CucumberCurrentChannel<TestChannel> state = new CucumberCurrentChannel<>();
        GherkinChannelEngine<TestChannel> engine = GherkinChannelEngineConfig.of(mapping, defaultConfig)
                .newRuntime(state);
        engine.initializeScenario(List.of(tag("BFF:it", "BFF:en", "WEB:it")));
        assertThat(state.getCurrentLanguage()).isEqualTo(Locale.ITALIAN);
        engine.initializeStep("When ");
        assertThat(state.getCurrentLanguage()).isEqualTo(Locale.ENGLISH);
        engine.initializeStep("And ");
        assertThat(state.getCurrentLanguage()).isEqualTo(Locale.ENGLISH);
        engine.initializeStep("Then ");
        assertThat(state.getCurrentLanguage()).isEqualTo(Locale.ITALIAN);
        assertThat(state.getCurrentChannel()).isEqualTo(TestChannel.WEB);
    }

    @Test
    void scenariosMaintainIndependentLanguageContext() {
        CucumberCurrentChannel<TestChannel> first = new CucumberCurrentChannel<>();
        CucumberCurrentChannel<TestChannel> second = new CucumberCurrentChannel<>();
        var config = GherkinChannelEngineConfig.of(mapping, defaultConfig);
        var one = config.newRuntime(first);
        var two = config.newRuntime(second);
        one.initializeScenario(List.of(tag("BFF:it", "BFF:en", "BFF:it")));
        two.initializeScenario(List.of(tag("BFF", "BFF", "BFF")));
        one.initializeStep("When ");
        two.initializeStep("When ");
        assertThat(first.getCurrentLanguage()).isEqualTo(Locale.ENGLISH);
        assertThat(second.getCurrentLanguage()).isEqualTo(Locale.ITALIAN);
    }

    @Test
    void inMemoryLanguagesAreThreadLocal() throws Exception {
        InMemoryCurrentChannel<TestChannel> state = new InMemoryCurrentChannel<>();
        state.setCurrentLanguage(Locale.ENGLISH);
        AtomicReference<Locale> other = new AtomicReference<>();
        Thread thread = new Thread(() -> {
            other.set(state.getCurrentLanguage());
            state.setCurrentLanguage(Locale.FRENCH);
        });
        thread.start();
        thread.join();
        assertThat(other.get()).isEqualTo(Locale.ITALIAN);
        assertThat(state.getCurrentLanguage()).isEqualTo(Locale.ENGLISH);
    }

    @Test
    void reversedTagOrderStillDeduplicatesEquivalentConfigurations() {
        assertExpandedCount(List.of(tag("BFF:it", "BFF:it", "BFF:it"),
                tag("BFF", "BFF", "BFF")), 1);
    }

    @Test
    void generatedScenarioConfigurationOverridesInheritedFeatureTag() {
        String source = """
                @smoke @channel:Given=BFF,When=BFF,Then=BFF
                Feature: F
                  @channel:Given=BFF:it,When=BFF:en,Then=BFF:it
                  Scenario: X
                    Given a
                    When b
                    Then c
                """;
        String expanded = expander.expand(Path.of("test.feature"), source);
        assertThat(expanded).contains("@smoke");
        assertThat(count(expanded, "@channel:")).isEqualTo(1);
        assertThat(expanded).contains("@channel:Given=BFF:it,When=BFF:en,Then=BFF:it");

        CucumberCurrentChannel<TestChannel> state = new CucumberCurrentChannel<>();
        var engine = GherkinChannelEngineConfig.of(mapping, defaultConfig).newRuntime(state);
        engine.initializeScenario(List.of(tag("BFF:it", "BFF:en", "BFF:it")));
        engine.initializeStep("When ");
        assertThat(state.getCurrentLanguage()).isEqualTo(Locale.ENGLISH);
    }

    private void assertExpandedCount(List<String> tags, int expected) {
        assertThat(count(expand(tags), "Scenario: X")).isEqualTo(expected);
    }

    private String expand(List<String> tags) {
        return expander.expand(Path.of("test.feature"),
                "Feature: F\n  " + String.join("\n  ", tags)
                        + "\n  Scenario: X\n    Given a\n    When b\n    Then c\n");
    }

    private static String tag(String given, String when, String then) {
        return "@channel:Given=" + given + ",When=" + when + ",Then=" + then;
    }

    private static int count(String haystack, String needle) {
        return (haystack.length() - haystack.replace(needle, "").length()) / needle.length();
    }

    private enum TestChannel implements ChannelKind { BFF, WEB }
}
