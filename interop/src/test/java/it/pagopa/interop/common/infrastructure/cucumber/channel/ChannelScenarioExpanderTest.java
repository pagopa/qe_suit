package it.pagopa.interop.common.infrastructure.cucumber.channel;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class ChannelScenarioExpanderTest {

    private static final Path DUMMY_PATH = Path.of("test.feature");

    // ------------------------------------------------------------------
    // No expansion needed
    // ------------------------------------------------------------------

    @Test
    void noChannelTag_returnsSourceUnchanged() {
        String source = """
                @eservice
                Feature: F
                  Scenario: X
                    Given a
                """;
        assertThat(ChannelScenarioExpander.expand(DUMMY_PATH, source)).isEqualTo(source);
    }

    @Test
    void singleChannelTag_returnsSourceUnchanged() {
        String source = """
                @eservice
                Feature: F
                  @channel:Given=BFF,When=WEB,Then=WEB
                  Scenario: X
                    Given a
                """;
        assertThat(ChannelScenarioExpander.expand(DUMMY_PATH, source)).isEqualTo(source);
    }

    // ------------------------------------------------------------------
    // Basic expansion: 2 @channel tags → 2 Scenario copies
    // ------------------------------------------------------------------

    @Test
    void twoChannelTags_produceTwoCopies() {
        String source = """
                @eservice
                Feature: F
                  @eservice
                  @channel:Given=BFF,When=WEB,Then=WEB
                  @channel:Given=BFF,When=BFF,Then=BFF
                  Scenario: X
                    Given a
                    When b
                    Then c
                """;

        String expanded = ChannelScenarioExpander.expand(DUMMY_PATH, source);

        // Two scenario blocks
        assertThat(countOccurrences(expanded, "Scenario:")).isEqualTo(2);
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

        String expanded = ChannelScenarioExpander.expand(DUMMY_PATH, source);

        // Total @channel: tags in expanded = exactly 2 (one per copy)
        assertThat(countOccurrences(expanded, "@channel:")).isEqualTo(2);

        // No scenario copy has more than one @channel: tag in its surrounding block
        // Split expanded by "Scenario:" keyword – each part is preceded by tags
        String[] parts = expanded.split("(?=\\bScenario:)");
        assertThat(parts.length).isGreaterThanOrEqualTo(2);
        for (String part : parts) {
            long channelTagCount = part.lines()
                    .filter(l -> l.trim().startsWith("@channel:"))
                    .count();
            assertThat(channelTagCount)
                    .as("Each scenario block must have at most 1 @channel: tag")
                    .isLessThanOrEqualTo(1);
        }
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

        String expanded = ChannelScenarioExpander.expand(DUMMY_PATH, source);

        assertThat(countOccurrences(expanded, "@eservice")).isEqualTo(2);
        assertThat(countOccurrences(expanded, "@smoke")).isEqualTo(2);
    }

    @Test
    void scenarioNameHasSuffix() {
        String source = """
                Feature: F
                  @channel:Given=BFF,When=WEB,Then=WEB
                  @channel:Given=BFF,When=BFF,Then=BFF
                  Scenario: MyScenario
                    Given a
                """;

        String expanded = ChannelScenarioExpander.expand(DUMMY_PATH, source);

        assertThat(expanded).contains("MyScenario [Given=BFF,When=WEB,Then=WEB]");
        assertThat(expanded).contains("MyScenario [Given=BFF,When=BFF,Then=BFF]");
    }

    // ------------------------------------------------------------------
    // Scenario Outline expansion
    // ------------------------------------------------------------------

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
                    | 3     |
                """;

        String expanded = ChannelScenarioExpander.expand(DUMMY_PATH, source);

        // Two outline copies
        assertThat(countOccurrences(expanded, "Scenario Outline:")).isEqualTo(2);
        // Each outline retains its Examples
        assertThat(countOccurrences(expanded, "Examples:")).isEqualTo(2);
        // Examples rows are NOT expanded (3 rows per outline = 6 rows total but as source)
        assertThat(countOccurrences(expanded, "| 1     |")).isEqualTo(2);
        assertThat(countOccurrences(expanded, "| 2     |")).isEqualTo(2);
        assertThat(countOccurrences(expanded, "| 3     |")).isEqualTo(2);
    }

    // ------------------------------------------------------------------
    // Multiple scenarios in one file
    // ------------------------------------------------------------------

    @Test
    void multipleScenarios_onlyMultiChannelOnesExpanded() {
        String source = """
                Feature: F
                  @eservice
                  Scenario: NormalScenario
                    Given a

                  @channel:Given=BFF,When=WEB,Then=WEB
                  @channel:Given=BFF,When=BFF,Then=BFF
                  Scenario: MultiChannel
                    Given b
                """;

        String expanded = ChannelScenarioExpander.expand(DUMMY_PATH, source);

        // NormalScenario appears once, MultiChannel appears twice
        assertThat(countOccurrences(expanded, "NormalScenario")).isEqualTo(1);
        assertThat(countOccurrences(expanded, "MultiChannel")).isEqualTo(2);
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private static int countOccurrences(String text, String substring) {
        int count = 0;
        int idx = 0;
        while ((idx = text.indexOf(substring, idx)) != -1) {
            count++;
            idx += substring.length();
        }
        return count;
    }
}
