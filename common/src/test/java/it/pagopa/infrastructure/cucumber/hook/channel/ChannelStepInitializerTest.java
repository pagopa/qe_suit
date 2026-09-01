package it.pagopa.infrastructure.cucumber.hook.channel;

import it.pagopa.application.ChannelKind;
import it.pagopa.infrastructure.channel.CurrentChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChannelStepInitializerTest {

    private ChannelScenarioContext<TestChannel> scenarioContext;
    private TestCurrentChannel currentChannel;
    private ChannelStepInitializer<TestChannel> initializer;

    @BeforeEach
    void setUp() {
        scenarioContext = new ChannelScenarioContext<>();
        currentChannel = new TestCurrentChannel();
        initializer = new ChannelStepInitializer<>(
                scenarioContext,
                currentChannel
        );

        scenarioContext.setConfig(
                new ChannelConfig<>(
                        TestChannel.BFF,
                        TestChannel.WEB_BROWSER,
                        TestChannel.WEB_BROWSER
                )
        );
    }

    @Test
    void givenStep_setsBff() {
        initializer.initialize("Given ");

        assertThat(currentChannel.getCurrentChannel())
                .isEqualTo(TestChannel.BFF);
    }

    @Test
    void whenStep_setsWebBrowser() {
        initializer.initialize("When ");

        assertThat(currentChannel.getCurrentChannel())
                .isEqualTo(TestChannel.WEB_BROWSER);
    }

    @Test
    void thenStep_setsWebBrowser() {
        initializer.initialize("Then ");

        assertThat(currentChannel.getCurrentChannel())
                .isEqualTo(TestChannel.WEB_BROWSER);
    }

    @Test
    void andAfterGiven_keepsBff() {
        initializer.initialize("Given ");
        initializer.initialize("And ");

        assertThat(currentChannel.getCurrentChannel())
                .isEqualTo(TestChannel.BFF);
    }

    @Test
    void andAfterWhen_keepsWebBrowser() {
        initializer.initialize("When ");
        initializer.initialize("And ");

        assertThat(currentChannel.getCurrentChannel())
                .isEqualTo(TestChannel.WEB_BROWSER);
    }

    @Test
    void andAfterThen_keepsWebBrowser() {
        initializer.initialize("Then ");
        initializer.initialize("And ");

        assertThat(currentChannel.getCurrentChannel())
                .isEqualTo(TestChannel.WEB_BROWSER);
    }

    @Test
    void noConfig_channelNotChanged() {
        scenarioContext.setConfig(null);
        currentChannel.setCurrentChannel(TestChannel.BFF);

        initializer.initialize("When ");

        assertThat(currentChannel.getCurrentChannel())
                .isEqualTo(TestChannel.BFF);
    }

    @Test
    void italianAllora_setsThenChannel() {
        scenarioContext.setConfig(
                new ChannelConfig<>(
                        TestChannel.BFF,
                        TestChannel.BFF,
                        TestChannel.WEB_BROWSER
                )
        );

        initializer.initialize("Allora ");

        assertThat(currentChannel.getCurrentChannel())
                .isEqualTo(TestChannel.WEB_BROWSER);
    }

    @Test
    void italianQuando_setsWhenChannel() {
        initializer.initialize("Quando ");

        assertThat(currentChannel.getCurrentChannel())
                .isEqualTo(TestChannel.WEB_BROWSER);
    }

    @Test
    void italianDato_setsGivenChannel() {
        initializer.initialize("Dato ");

        assertThat(currentChannel.getCurrentChannel())
                .isEqualTo(TestChannel.BFF);
    }

    private enum TestChannel implements ChannelKind {
        BFF,
        WEB_BROWSER
    }

    private static final class TestCurrentChannel
            implements CurrentChannel<TestChannel> {

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
