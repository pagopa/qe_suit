package it.pagopa.infrastructure.cucumber.hook.channel;

import it.pagopa.application.ChannelKind;
import it.pagopa.infrastructure.channel.CurrentChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ChannelRuntimeTest {

    private static final ChannelConfig<TestChannel> DEFAULT_CONFIG =
            new ChannelConfig<>(
                    TestChannel.BFF,
                    TestChannel.BFF,
                    TestChannel.BFF
            );

    private ChannelModule<TestChannel> module;
    private TestCurrentChannel currentChannel;
    private ChannelRuntime<TestChannel> runtime;

    @BeforeEach
    void setUp() {
        module = ChannelModule.of(
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
                ),
                DEFAULT_CONFIG
        );
        currentChannel = new TestCurrentChannel();
        runtime = module.newRuntime(currentChannel);
    }

    @Test
    void givenUsesConfigGiven() {
        runtime.initializeScenario(List.of("@channel:Given=BFF,When=WEB,Then=WEB"));
        runtime.initializeStep("Given ");
        assertThat(currentChannel.getCurrentChannel()).isEqualTo(TestChannel.BFF);
    }

    @Test
    void whenUsesConfigWhen() {
        runtime.initializeScenario(List.of("@channel:Given=BFF,When=WEB,Then=WEB"));
        runtime.initializeStep("When ");
        assertThat(currentChannel.getCurrentChannel()).isEqualTo(TestChannel.WEB_BROWSER);
    }

    @Test
    void thenUsesConfigThen() {
        runtime.initializeScenario(List.of("@channel:Given=BFF,When=WEB,Then=WEB"));
        runtime.initializeStep("Then ");
        assertThat(currentChannel.getCurrentChannel()).isEqualTo(TestChannel.WEB_BROWSER);
    }

    @Test
    void andAfterGivenInheritsGiven() {
        runtime.initializeScenario(List.of("@channel:Given=BFF,When=WEB,Then=WEB"));
        runtime.initializeStep("Given ");
        runtime.initializeStep("And ");
        assertThat(currentChannel.getCurrentChannel()).isEqualTo(TestChannel.BFF);
    }

    @Test
    void andAfterWhenInheritsWhen() {
        runtime.initializeScenario(List.of("@channel:Given=BFF,When=WEB,Then=WEB"));
        runtime.initializeStep("When ");
        runtime.initializeStep("And ");
        assertThat(currentChannel.getCurrentChannel()).isEqualTo(TestChannel.WEB_BROWSER);
    }

    @Test
    void andAfterThenInheritsThen() {
        runtime.initializeScenario(List.of("@channel:Given=BFF,When=WEB,Then=WEB"));
        runtime.initializeStep("Then ");
        runtime.initializeStep("And ");
        assertThat(currentChannel.getCurrentChannel()).isEqualTo(TestChannel.WEB_BROWSER);
    }

    @Test
    void initializeScenarioResetsLastSemanticTypeToGiven() {
        runtime.initializeScenario(List.of("@channel:Given=BFF,When=WEB,Then=WEB"));
        runtime.initializeStep("When ");
        runtime.initializeScenario(List.of("@channel:Given=BFF,When=BFF,Then=BFF"));
        runtime.initializeStep("And ");
        assertThat(currentChannel.getCurrentChannel()).isEqualTo(TestChannel.BFF);
    }

    @Test
    void distinctRuntimesDoNotShareState() {
        TestCurrentChannel channelA = new TestCurrentChannel();
        TestCurrentChannel channelB = new TestCurrentChannel();
        ChannelRuntime<TestChannel> runtimeA = module.newRuntime(channelA);
        ChannelRuntime<TestChannel> runtimeB = module.newRuntime(channelB);

        runtimeA.initializeScenario(List.of("@channel:Given=BFF,When=WEB,Then=WEB"));
        runtimeB.initializeScenario(List.of("@channel:Given=BFF,When=BFF,Then=BFF"));

        runtimeA.initializeStep("When ");
        runtimeB.initializeStep("And ");

        assertThat(channelA.getCurrentChannel()).isEqualTo(TestChannel.WEB_BROWSER);
        assertThat(channelB.getCurrentChannel()).isEqualTo(TestChannel.BFF);
    }

    @Test
    void initializeScenarioFallsBackToDefaultConfigWhenMissingChannelTag() {
        ChannelConfig<TestChannel> config = runtime.initializeScenario(List.of("@smoke"));
        assertThat(config).isEqualTo(DEFAULT_CONFIG);
        assertThat(currentChannel.getCurrentChannel()).isEqualTo(TestChannel.BFF);
    }

    @Test
    void initializeScenarioParsesChannelTag() {
        ChannelConfig<TestChannel> config =
                runtime.initializeScenario(List.of("@channel:Given=BFF,When=WEB,Then=WEB"));
        assertThat(config).isEqualTo(
                new ChannelConfig<>(
                        TestChannel.BFF,
                        TestChannel.WEB_BROWSER,
                        TestChannel.WEB_BROWSER
                )
        );
    }

    private enum TestChannel implements ChannelKind {
        BFF,
        WEB_BROWSER
    }

    private static final class TestCurrentChannel implements CurrentChannel<TestChannel> {

        private TestChannel currentChannel;

        @Override
        public TestChannel getCurrentChannel() {
            return currentChannel;
        }

        @Override
        public void setCurrentChannel(TestChannel currentChannel) {
            this.currentChannel = currentChannel;
        }
    }
}
