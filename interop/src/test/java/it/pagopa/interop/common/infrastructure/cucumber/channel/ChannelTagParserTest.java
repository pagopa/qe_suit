package it.pagopa.interop.common.infrastructure.cucumber.channel;

import it.pagopa.interop.common.kernel.domain.Channel;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ChannelTagParserTest {

    // ------------------------------------------------------------------
    // Happy-path: 0 / 1 / N configs
    // ------------------------------------------------------------------

    @Test
    void zeroChannelTags_returnsEmptyList() {
        List<ChannelConfig> result = ChannelTagParser.parse(List.of(
                "@eservice", "@smoke", "@ignore"));
        assertThat(result).isEmpty();
    }

    @Test
    void oneChannelTag_returnsOneConfig() {
        List<ChannelConfig> result = ChannelTagParser.parse(List.of(
                "@eservice",
                "@channel:Given=BFF,When=WEB,Then=WEB"));

        assertThat(result).hasSize(1);
        ChannelConfig cfg = result.get(0);
        assertThat(cfg.given()).isEqualTo(Channel.BFF);
        assertThat(cfg.when()).isEqualTo(Channel.WEB_BROWSER);
        assertThat(cfg.then()).isEqualTo(Channel.WEB_BROWSER);
    }

    @Test
    void twoChannelTags_returnsTwoConfigsInOrder() {
        List<ChannelConfig> result = ChannelTagParser.parse(List.of(
                "@eservice",
                "@channel:Given=BFF,When=WEB,Then=WEB",
                "@channel:Given=BFF,When=WEB_BROWSER,Then=BFF"));

        assertThat(result).hasSize(2);

        ChannelConfig first = result.get(0);
        assertThat(first.given()).isEqualTo(Channel.BFF);
        assertThat(first.when()).isEqualTo(Channel.WEB_BROWSER);
        assertThat(first.then()).isEqualTo(Channel.WEB_BROWSER);

        ChannelConfig second = result.get(1);
        assertThat(second.given()).isEqualTo(Channel.BFF);
        assertThat(second.when()).isEqualTo(Channel.WEB_BROWSER);
        assertThat(second.then()).isEqualTo(Channel.BFF);
    }

    @Test
    void functionalTagsAreIgnored() {
        List<ChannelConfig> result = ChannelTagParser.parse(List.of(
                "@eservice", "@smoke", "@debug", "@wait_for_fix",
                "@channel:Given=BFF,When=BFF,Then=BFF",
                "@ignore"));
        assertThat(result).hasSize(1);
    }

    @Test
    void webBrowserAliasIsAccepted() {
        List<ChannelConfig> result = ChannelTagParser.parse(List.of(
                "@channel:Given=WEB_BROWSER,When=WEB_BROWSER,Then=WEB_BROWSER"));
        assertThat(result).hasSize(1);
        ChannelConfig cfg = result.get(0);
        assertThat(cfg.given()).isEqualTo(Channel.WEB_BROWSER);
    }

    // ------------------------------------------------------------------
    // Error cases
    // ------------------------------------------------------------------

    @Test
    void missingGivenKey_throwsException() {
        assertThatThrownBy(() -> ChannelTagParser.parse(List.of(
                "@channel:When=WEB,Then=WEB")))
                .isInstanceOf(ChannelTagParser.ChannelTagParseException.class)
                .hasMessageContaining("Given");
    }

    @Test
    void missingWhenKey_throwsException() {
        assertThatThrownBy(() -> ChannelTagParser.parse(List.of(
                "@channel:Given=BFF,Then=WEB")))
                .isInstanceOf(ChannelTagParser.ChannelTagParseException.class)
                .hasMessageContaining("When");
    }

    @Test
    void missingThenKey_throwsException() {
        assertThatThrownBy(() -> ChannelTagParser.parse(List.of(
                "@channel:Given=BFF,When=WEB")))
                .isInstanceOf(ChannelTagParser.ChannelTagParseException.class)
                .hasMessageContaining("Then");
    }

    @Test
    void unknownKey_throwsException() {
        assertThatThrownBy(() -> ChannelTagParser.parse(List.of(
                "@channel:Given=BFF,When=WEB,Then=WEB,Extra=BFF")))
                .isInstanceOf(ChannelTagParser.ChannelTagParseException.class)
                .hasMessageContaining("Extra");
    }

    @Test
    void duplicateKey_throwsException() {
        assertThatThrownBy(() -> ChannelTagParser.parse(List.of(
                "@channel:Given=BFF,When=WEB,Then=WEB,Given=BFF")))
                .isInstanceOf(ChannelTagParser.ChannelTagParseException.class)
                .hasMessageContaining("Duplicate")
                .hasMessageContaining("Given");
    }

    @Test
    void unknownChannelValue_throwsException() {
        assertThatThrownBy(() -> ChannelTagParser.parse(List.of(
                "@channel:Given=BFF,When=WEB_V2,Then=WEB")))
                .isInstanceOf(ChannelTagParser.ChannelTagParseException.class)
                .hasMessageContaining("WEB_V2");
    }

    @Test
    void missingValue_throwsException() {
        assertThatThrownBy(() -> ChannelTagParser.parse(List.of(
                "@channel:Given=,When=WEB,Then=WEB")))
                .isInstanceOf(ChannelTagParser.ChannelTagParseException.class);
    }

    @Test
    void missingEquals_throwsException() {
        assertThatThrownBy(() -> ChannelTagParser.parse(List.of(
                "@channel:GivenBFF,When=WEB,Then=WEB")))
                .isInstanceOf(ChannelTagParser.ChannelTagParseException.class);
    }

    @Test
    void emptyBody_throwsException() {
        assertThatThrownBy(() -> ChannelTagParser.parse(List.of("@channel:")))
                .isInstanceOf(ChannelTagParser.ChannelTagParseException.class)
                .hasMessageContaining("Empty");
    }
}
