package it.pagopa.infrastructure.cucumber.channel;

import it.pagopa.application.ChannelKind;
import it.pagopa.infrastructure.channel.CurrentChannel;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ChannelRuntime<C extends Enum<C> & ChannelKind> {

    private final ChannelTagParser<C> tagParser;
    private final ChannelConfig<C> defaultConfig;
    private final CurrentChannel<C> currentChannel;

    private ChannelConfig<C> scenarioConfig;
    private SemanticStepType lastSemanticType;

    ChannelRuntime(
            ChannelTagParser<C> tagParser,
            ChannelConfig<C> defaultConfig,
            CurrentChannel<C> currentChannel
    ) {
        this.tagParser = Objects.requireNonNull(tagParser, "tagParser must not be null");
        this.defaultConfig = Objects.requireNonNull(defaultConfig, "defaultConfig must not be null");
        this.currentChannel = Objects.requireNonNull(currentChannel, "currentChannel must not be null");
        this.lastSemanticType = SemanticStepType.GIVEN;
    }

    public ChannelConfig<C> initializeScenario(Collection<String> tags) {
        List<ChannelConfig<C>> configs = tagParser.parse(tags);
        ChannelConfig<C> config = ChannelConfigResolver.resolve(configs).orElse(defaultConfig);
        this.scenarioConfig = config;
        this.lastSemanticType = SemanticStepType.GIVEN;
        currentChannel.setCurrentChannel(config.given());
        return config;
    }

    public C initializeStep(String keyword) {
        if (scenarioConfig == null) {
            return null;
        }
        SemanticStepType semantic = resolveSemanticType(keyword);
        lastSemanticType = semantic;
        C channel = switch (semantic) {
            case GIVEN -> scenarioConfig.given();
            case WHEN -> scenarioConfig.when();
            case THEN -> scenarioConfig.then();
        };
        currentChannel.setCurrentChannel(channel);
        return channel;
    }

    private SemanticStepType resolveSemanticType(String keyword) {
        if (keyword == null) {
            return lastSemanticType;
        }
        return switch (keyword.trim().toLowerCase(Locale.ROOT)) {
            case "given", "dato", "data", "dati", "date", "dado", "dada", "dados", "dadas" -> SemanticStepType.GIVEN;
            case "when", "quando" -> SemanticStepType.WHEN;
            case "then", "allora", "então", "entao" -> SemanticStepType.THEN;
            case "and", "but", "e", "ma" -> lastSemanticType;
            default -> lastSemanticType;
        };
    }

    private enum SemanticStepType {
        GIVEN,
        WHEN,
        THEN
    }
}
