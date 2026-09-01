package it.pagopa.interop.common.infrastructure.cucumber.channel;

import io.cucumber.messages.types.Feature;
import io.cucumber.messages.types.FeatureChild;
import io.cucumber.messages.types.Scenario;
import io.cucumber.messages.types.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Extracts one {@link ScenarioBlock} per Scenario/Scenario Outline found in a parsed
 * {@link Feature}, computing line ranges from the Gherkin AST location data and resolving
 * the effective {@link ChannelConfig} list for each scenario via
 * {@link EffectiveChannelConfigResolver}.
 * <p>
 * This class performs no text rendering; it only maps AST + source text to line ranges.
 */
final class ScenarioBlockExtractor {

    private ScenarioBlockExtractor() {}

    /**
     * @param featureConfigs channel configs declared at Feature level (already parsed)
     */
    static List<ScenarioBlock> extract(Feature feature, String[] lines, List<ChannelConfig> featureConfigs) {
        List<FeatureChild> children = feature.getChildren();
        List<ScenarioBlock> result = new ArrayList<>();

        for (int i = 0; i < children.size(); i++) {
            Optional<Scenario> scenarioOpt = children.get(i).getScenario();
            if (scenarioOpt.isEmpty()) continue;

            Scenario scenario = scenarioOpt.get();
            result.add(toBlock(scenario, children, i, lines, featureConfigs));
        }

        return result;
    }

    private static ScenarioBlock toBlock(
            Scenario scenario,
            List<FeatureChild> children,
            int index,
            String[] lines,
            List<ChannelConfig> featureConfigs
    ) {
        List<ChannelConfig> scenarioConfigs = ChannelTagParser.parse(
                scenario.getTags().stream().map(Tag::getName).toList());
        List<ChannelConfig> resolvedConfigs =
                EffectiveChannelConfigResolver.resolve(featureConfigs, scenarioConfigs);

        // No @channel declared at either level: fall back to the same default config used
        // at runtime by ChannelScenarioHook, materialising it explicitly so every generated
        // scenario is self-contained (its config is always readable from its own tags).
        List<ChannelConfig> effectiveConfigs = resolvedConfigs.isEmpty()
                ? List.of(ChannelConfig.DEFAULT)
                : resolvedConfigs;

        int keywordLine = scenario.getLocation().getLine().intValue();
        int startLine = scenario.getTags().isEmpty()
                ? keywordLine
                : scenario.getTags().get(0).getLocation().getLine().intValue();

        int endLine = findEndLine(children, index, lines.length);
        while (endLine > startLine && lines[endLine - 1].isBlank()) endLine--;

        return new ScenarioBlock(startLine, endLine, keywordLine, effectiveConfigs);
    }

    private static int findEndLine(List<FeatureChild> children, int currentIndex, int totalLines) {
        for (int i = currentIndex + 1; i < children.size(); i++) {
            Optional<Scenario> nextOpt = children.get(i).getScenario();
            if (nextOpt.isEmpty()) continue;

            Scenario next = nextOpt.get();
            return (next.getTags().isEmpty()
                    ? next.getLocation().getLine()
                    : next.getTags().get(0).getLocation().getLine()
            ).intValue() - 1;
        }
        return totalLines;
    }
}
