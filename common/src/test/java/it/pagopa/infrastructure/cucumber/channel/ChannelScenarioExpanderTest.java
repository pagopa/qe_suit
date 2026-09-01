package it.pagopa.infrastructure.cucumber.channel;

import it.pagopa.application.ChannelKind;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ChannelScenarioExpanderTest {

    private static final Path DUMMY_PATH = Path.of("test.feature");

    private ChannelScenarioExpander<TestChannel> expander;

    @BeforeEach
    void setUp() {
        ChannelGherkinMapping<TestChannel> mapping =
                new ChannelGherkinMapping<>(
                        Map.of(
                                "BFF", TestChannel.BFF,
                                "WEB", TestChannel.WEB_BROWSER,
                                "WEB_BROWSER", TestChannel.WEB_BROWSER
                        ),
                        Map.of(
                                TestChannel.BFF, "BFF",
                                TestChannel.WEB_BROWSER, "WEB"
                        )
                );

        ChannelConfig<TestChannel> defaultConfig =
                new ChannelConfig<>(
                        TestChannel.BFF,
                        TestChannel.BFF,
                        TestChannel.BFF
                );

        expander = new ChannelScenarioExpander<>(
                mapping,
                defaultConfig
        );
    }

    @Test
    void noChannelTag_materializesDefaultChannelConfig() {
        String source = """
                @eservice
                Feature: F
                  Scenario: X
                    Given a
                """;

        String expanded = expander.expand(DUMMY_PATH, source);

        assertThat(expanded).contains(
                "@channel:Given=BFF,When=BFF,Then=BFF"
        );
        assertThat(expanded).contains(
                "Scenario: X [Given=BFF,When=BFF,Then=BFF]"
        );
    }

    @Test
    void featureSingleChannel_isInheritedByAllScenarios() {
        String source = """
                @channel:Given=BFF,When=WEB,Then=WEB
                Feature: F
                  Scenario: A
                    Given a

                  Scenario: B
                    Given b
                """;

        String expanded = expander.expand(DUMMY_PATH, source);

        assertThat(countOccurrences(expanded, "Scenario: A")).isEqualTo(1);
        assertThat(countOccurrences(expanded, "Scenario: B")).isEqualTo(1);
        assertThat(expanded)
                .contains("Scenario: A [Given=BFF,When=WEB,Then=WEB]")
                .contains("Scenario: B [Given=BFF,When=WEB,Then=WEB]");
    }

    @Test
    void twoChannelTags_produceTwoCopies() {
        String source = """
                Feature: F
                  @channel:Given=BFF,When=WEB,Then=WEB
                  @channel:Given=BFF,When=BFF,Then=BFF
                  Scenario: X
                    Given a
                    When b
                    Then c
                """;

        String expanded = expander.expand(DUMMY_PATH, source);

        assertThat(countOccurrences(expanded, "Scenario:")).isEqualTo(2);
        assertThat(expanded)
                .contains("X [Given=BFF,When=WEB,Then=WEB]")
                .contains("X [Given=BFF,When=BFF,Then=BFF]");
    }

    @Test
    void scenarioChannelOverridesFeatureChannels() {
        String source = """
                @channel:Given=BFF,When=WEB,Then=WEB
                Feature: F
                  @channel:Given=BFF,When=WEB,Then=BFF
                  Scenario: A
                    Given a
                """;

        String expanded = expander.expand(DUMMY_PATH, source);

        assertThat(countOccurrences(expanded, "Scenario: A")).isEqualTo(1);
        assertThat(expanded)
                .contains("A [Given=BFF,When=WEB,Then=BFF]");
    }

    @Test
    void twoChannelTags_eachCopyHasOnlyOneChannelTag() {
        String source = """
                Feature: F
                  @channel:Given=BFF,When=WEB,Then=WEB
                  @channel:Given=BFF,When=BFF,Then=BFF
                  Scenario: X
                    Given a
                """;

        String expanded = expander.expand(DUMMY_PATH, source);

        assertThat(countOccurrences(expanded, "@channel:")).isEqualTo(2);
    }

    @Test
    void functionalTagsArePreservedInBothCopies() {
        String source = """
                Feature: F
                  @eservice
                  @smoke
                  @channel:Given=BFF,When=WEB,Then=WEB
                  @channel:Given=BFF,When=BFF,Then=BFF
                  Scenario: X
                    Given a
                """;

        String expanded = expander.expand(DUMMY_PATH, source);

        assertThat(countOccurrences(expanded, "@eservice")).isEqualTo(2);
        assertThat(countOccurrences(expanded, "@smoke")).isEqualTo(2);
    }

    @Test
    void outlineWithoutChannel_materializesDefaultChannelConfig() {
        String source = """
                Feature: F
                  Scenario Outline: Test <value>
                    Given <value>

                  Examples:
                    | value |
                    | 1     |
                """;

        String expanded = expander.expand(DUMMY_PATH, source);

        assertThat(countOccurrences(expanded, "Scenario Outline:")).isEqualTo(1);
        assertThat(expanded).contains(
                "Test <value> [Given=BFF,When=BFF,Then=BFF]"
        );
    }

    @Test
    void twoChannelTagsOnOutline_produceTwoOutlines() {
        String source = """
                Feature: F
                  @channel:Given=BFF,When=WEB,Then=WEB
                  @channel:Given=BFF,When=BFF,Then=BFF
                  Scenario Outline: X
                    Given <value>

                  Examples:
                    | value |
                    | 1     |
                    | 2     |
                """;

        String expanded = expander.expand(DUMMY_PATH, source);

        assertThat(countOccurrences(expanded, "Scenario Outline:")).isEqualTo(2);
        assertThat(countOccurrences(expanded, "Examples:")).isEqualTo(2);
    }

    private static int countOccurrences(
            String text,
            String substring
    ) {
        int count = 0;
        int index = 0;

        while ((index = text.indexOf(substring, index)) != -1) {
            count++;
            index += substring.length();
        }

        return count;
    }

    private enum TestChannel implements ChannelKind {
        BFF,
        WEB_BROWSER
    }
}
