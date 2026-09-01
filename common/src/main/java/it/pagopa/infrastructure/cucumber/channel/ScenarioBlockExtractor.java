package it.pagopa.infrastructure.cucumber.channel;

import io.cucumber.messages.types.Feature;
import io.cucumber.messages.types.FeatureChild;
import io.cucumber.messages.types.Scenario;
import io.cucumber.messages.types.Tag;
import it.pagopa.application.ChannelKind;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

final class ScenarioBlockExtractor<C extends Enum<C> & ChannelKind> {

    private final ChannelTagParser<C> channelTagParser;
    private final ChannelConfig<C> defaultConfig;

    ScenarioBlockExtractor(ChannelTagParser<C> channelTagParser, ChannelConfig<C> defaultConfig) {
        this.channelTagParser = Objects.requireNonNull(channelTagParser, "channelTagParser must not be null");
        this.defaultConfig = Objects.requireNonNull(defaultConfig, "defaultConfig must not be null");
    }

    List<ScenarioBlock<C>> extract(
            Feature feature,
            String[] lines,
            List<ChannelConfig<C>> featureConfigs
    ) {
        List<FeatureChild> children = feature.getChildren();
        List<ScenarioBlock<C>> result = new ArrayList<>();

        for (int i = 0; i < children.size(); i++) {
            Optional<Scenario> scenario = children.get(i).getScenario();
            if (scenario.isPresent()) result.add(toBlock(scenario.get(), children, i, lines, featureConfigs));
        }
        return result;
    }

    private ScenarioBlock<C> toBlock(
            Scenario scenario,
            List<FeatureChild> children,
            int index,
            String[] lines,
            List<ChannelConfig<C>> featureConfigs
    ) {
        List<ChannelConfig<C>> scenarioConfigs = channelTagParser.parse(
                scenario.getTags().stream().map(Tag::getName).toList());
        List<ChannelConfig<C>> resolved = EffectiveChannelConfigResolver.resolve(featureConfigs, scenarioConfigs);
        List<ChannelConfig<C>> effective = resolved.isEmpty() ? List.of(defaultConfig) : resolved;

        int keywordLine = scenario.getLocation().getLine().intValue();
        int startLine = scenario.getTags().isEmpty()
                ? keywordLine
                : scenario.getTags().get(0).getLocation().getLine().intValue();
        int endLine = findEndLine(children, index, lines.length);
        while (endLine > startLine && lines[endLine - 1].isBlank()) endLine--;

        return new ScenarioBlock<>(startLine, endLine, keywordLine, effective);
    }

    private static int findEndLine(List<FeatureChild> children, int currentIndex, int totalLines) {
        for (int i = currentIndex + 1; i < children.size(); i++) {
            Optional<Scenario> next = children.get(i).getScenario();
            if (next.isEmpty()) continue;
            Scenario scenario = next.get();
            return (scenario.getTags().isEmpty()
                    ? scenario.getLocation().getLine()
                    : scenario.getTags().get(0).getLocation().getLine()).intValue() - 1;
        }
        return totalLines;
    }
}
