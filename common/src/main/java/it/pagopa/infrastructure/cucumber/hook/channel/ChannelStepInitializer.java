package it.pagopa.infrastructure.cucumber.hook.channel;

import it.pagopa.application.ChannelKind;
import it.pagopa.infrastructure.channel.CurrentChannel;

import java.util.Locale;
import java.util.Objects;

public class ChannelStepInitializer<
        C extends Enum<C> & ChannelKind> {

    private final ChannelScenarioContext<C> scenarioChannelContext;
    private final CurrentChannel<C> currentChannel;
    private SemanticStepType lastSemanticType = SemanticStepType.GIVEN;

    public ChannelStepInitializer(
            ChannelScenarioContext<C> scenarioChannelContext,
            CurrentChannel<C> currentChannel
    ) {
        this.scenarioChannelContext =
                Objects.requireNonNull(scenarioChannelContext);
        this.currentChannel =
                Objects.requireNonNull(currentChannel);
    }

    public C initialize(String keyword) {
        ChannelConfig<C> config = scenarioChannelContext.getConfig();

        if (config == null) {
            return null;
        }

        SemanticStepType semantic = resolveSemanticType(keyword);
        lastSemanticType = semantic;

        C channel = switch (semantic) {
            case GIVEN -> config.given();
            case WHEN -> config.when();
            case THEN -> config.then();
        };

        currentChannel.setCurrentChannel(channel);
        return channel;
    }

    private SemanticStepType resolveSemanticType(String keyword) {
        if (keyword == null) {
            return lastSemanticType;
        }

        return switch (keyword.trim().toLowerCase(Locale.ROOT)) {
            case "given", "dato", "data", "dati", "date",
                 "dado", "dada", "dados", "dadas" ->
                    SemanticStepType.GIVEN;

            case "when", "quando" ->
                    SemanticStepType.WHEN;

            case "then", "allora", "então", "entao" ->
                    SemanticStepType.THEN;

            default ->
                    lastSemanticType;
        };
    }

    private enum SemanticStepType {
        GIVEN,
        WHEN,
        THEN
    }
}