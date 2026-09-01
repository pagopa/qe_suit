package it.pagopa.infrastructure.cucumber.hook.channel;

import it.pagopa.application.ChannelKind;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ChannelTagParserTest {

    private ChannelTagParser<TestChannel> parser;

    @BeforeEach
    void setUp() {
        parser = new ChannelTagParser<>(mapping());
    }

    @Test
    void zeroChannelTags_returnsEmptyList() {
        assertThat(
                parser.parse(List.of(
                        "@eservice",
                        "@smoke",
                        "@ignore"
                ))
        ).isEmpty();
    }

    @Test
    void oneChannelTag_returnsOneConfig() {
        List<ChannelConfig<TestChannel>> result =
                parser.parse(List.of(
                        "@eservice",
                        "@channel:Given=BFF,When=WEB,Then=WEB"
                ));

        assertThat(result).containsExactly(
                new ChannelConfig<>(
                        TestChannel.BFF,
                        TestChannel.WEB_BROWSER,
                        TestChannel.WEB_BROWSER
                )
        );
    }

    @Test
    void twoChannelTags_returnsTwoConfigsInOrder() {
        List<ChannelConfig<TestChannel>> result =
                parser.parse(List.of(
                        "@eservice",
                        "@channel:Given=BFF,When=WEB,Then=WEB",
                        "@channel:Given=BFF,When=WEB_BROWSER,Then=BFF"
                ));

        assertThat(result).containsExactly(
                new ChannelConfig<>(
                        TestChannel.BFF,
                        TestChannel.WEB_BROWSER,
                        TestChannel.WEB_BROWSER
                ),
                new ChannelConfig<>(
                        TestChannel.BFF,
                        TestChannel.WEB_BROWSER,
                        TestChannel.BFF
                )
        );
    }

    @Test
    void functionalTagsAreIgnored() {
        assertThat(
                parser.parse(List.of(
                        "@eservice",
                        "@smoke",
                        "@debug",
                        "@wait_for_fix",
                        "@channel:Given=BFF,When=BFF,Then=BFF",
                        "@ignore"
                ))
        ).hasSize(1);
    }

    @Test
    void webBrowserAliasIsAccepted() {
        List<ChannelConfig<TestChannel>> result =
                parser.parse(List.of(
                        "@channel:Given=WEB_BROWSER,When=WEB_BROWSER,Then=WEB_BROWSER"
                ));

        assertThat(result.get(0).given())
                .isEqualTo(TestChannel.WEB_BROWSER);
    }

    @Test
    void missingGivenKey_throwsException() {
        assertThatThrownBy(() ->
                parser.parse(List.of(
                        "@channel:When=WEB,Then=WEB"
                ))
        )
                .isInstanceOf(ChannelTagParser.ChannelTagParseException.class)
                .hasMessageContaining("Given");
    }

    @Test
    void missingWhenKey_throwsException() {
        assertThatThrownBy(() ->
                parser.parse(List.of(
                        "@channel:Given=BFF,Then=WEB"
                ))
        )
                .isInstanceOf(ChannelTagParser.ChannelTagParseException.class)
                .hasMessageContaining("When");
    }

    @Test
    void missingThenKey_throwsException() {
        assertThatThrownBy(() ->
                parser.parse(List.of(
                        "@channel:Given=BFF,When=WEB"
                ))
        )
                .isInstanceOf(ChannelTagParser.ChannelTagParseException.class)
                .hasMessageContaining("Then");
    }

    @Test
    void unknownKey_throwsException() {
        assertThatThrownBy(() ->
                parser.parse(List.of(
                        "@channel:Given=BFF,When=WEB,Then=WEB,Extra=BFF"
                ))
        )
                .isInstanceOf(ChannelTagParser.ChannelTagParseException.class)
                .hasMessageContaining("Extra");
    }

    @Test
    void duplicateKey_throwsException() {
        assertThatThrownBy(() ->
                parser.parse(List.of(
                        "@channel:Given=BFF,When=WEB,Then=WEB,Given=BFF"
                ))
        )
                .isInstanceOf(ChannelTagParser.ChannelTagParseException.class)
                .hasMessageContaining("Duplicate")
                .hasMessageContaining("Given");
    }

    @Test
    void unknownChannelValue_throwsException() {
        assertThatThrownBy(() ->
                parser.parse(List.of(
                        "@channel:Given=BFF,When=WEB_V2,Then=WEB"
                ))
        )
                .isInstanceOf(ChannelTagParser.ChannelTagParseException.class)
                .hasMessageContaining("WEB_V2");
    }

    @Test
    void missingValue_throwsException() {
        assertThatThrownBy(() ->
                parser.parse(List.of(
                        "@channel:Given=,When=WEB,Then=WEB"
                ))
        )
                .isInstanceOf(ChannelTagParser.ChannelTagParseException.class);
    }

    @Test
    void missingEquals_throwsException() {
        assertThatThrownBy(() ->
                parser.parse(List.of(
                        "@channel:GivenBFF,When=WEB,Then=WEB"
                ))
        )
                .isInstanceOf(ChannelTagParser.ChannelTagParseException.class);
    }

    @Test
    void emptyBody_throwsException() {
        assertThatThrownBy(() ->
                parser.parse(List.of("@channel:"))
        )
                .isInstanceOf(ChannelTagParser.ChannelTagParseException.class)
                .hasMessageContaining("Empty");
    }

    private static ChannelGherkinMapping<TestChannel> mapping() {
        return new ChannelGherkinMapping<>(
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
    }

    private enum TestChannel implements ChannelKind {
        BFF,
        WEB_BROWSER
    }
}
