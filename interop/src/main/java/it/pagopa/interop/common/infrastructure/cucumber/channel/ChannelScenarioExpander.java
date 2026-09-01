package it.pagopa.interop.common.infrastructure.cucumber.channel;

import io.cucumber.gherkin.GherkinParser;
import io.cucumber.messages.types.*;
import it.pagopa.interop.common.kernel.domain.Channel;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ChannelScenarioExpander {

    private static final GherkinParser PARSER = GherkinParser.builder()
            .includeGherkinDocument(true)
            .includeSource(false)
            .includePickles(false)
            .build();

    private ChannelScenarioExpander() {}

    public static String expand(Path sourcePath, String sourceText) {
        Feature feature = parseFeature(sourcePath, sourceText).orElse(null);

        if (feature == null) return sourceText;

        List<ChannelConfig> featureConfigs = ChannelTagParser.parse(
                feature.getTags().stream()
                        .map(Tag::getName)
                        .toList()
        );

        List<ScenarioBlock> blocks = extractBlocks(feature, sourceText, featureConfigs);

        return blocks.isEmpty() ? sourceText : rebuild(sourceText, blocks);
    }

    private static Optional<Feature> parseFeature(Path sourcePath, String sourceText) {
        try {
            return PARSER.parse(sourcePath.toString(), sourceText.getBytes())
                    .filter(e -> e.getGherkinDocument().isPresent())
                    .findFirst()
                    .flatMap(Envelope::getGherkinDocument)
                    .flatMap(GherkinDocument::getFeature);
        } catch (RuntimeException e) {
            throw new FeatureExpansionException("Failed to parse feature file: " + sourcePath, e);
        }
    }

    private record ScenarioBlock(int startLine, int endLine, int keywordLine, List<ChannelConfig> configs) {
    }

    private static List<ScenarioBlock> extractBlocks(Feature feature, String sourceText, List<ChannelConfig> featureConfigs) {
        String[] lines = sourceText.split("\n", -1);
        List<FeatureChild> children = feature.getChildren();
        List<ScenarioBlock> result = new ArrayList<>();

        for (int i = 0; i < children.size(); i++) {
            Optional<Scenario> scenarioOpt = children.get(i).getScenario();

            if (scenarioOpt.isEmpty()) continue;

            Scenario scenario = scenarioOpt.get();

            List<ChannelConfig> scenarioConfigs = ChannelTagParser.parse(
                    scenario.getTags().stream()
                            .map(Tag::getName)
                            .toList()
            );

            List<ChannelConfig> effectiveConfigs =
                    EffectiveChannelConfigResolver.resolve(
                            featureConfigs,
                            scenarioConfigs
                    );

            int keywordLine = scenario.getLocation()
                    .getLine()
                    .intValue();

            int startLine = scenario.getTags().isEmpty() ? keywordLine
                    : scenario.getTags()
                    .get(0)
                    .getLocation()
                    .getLine()
                    .intValue();

            int endLine = findEndLine(children, i, lines.length);

            while (endLine > startLine && lines[endLine - 1].isBlank()) endLine--;

            result.add(new ScenarioBlock(startLine, endLine, keywordLine, effectiveConfigs));
        }

        return result;
    }

    private static int findEndLine(List<FeatureChild> children, int currentIndex, int totalLines) {
        for (int i = currentIndex + 1; i < children.size(); i++) {
            Optional<Scenario> nextOpt =
                    children.get(i).getScenario();

            if (nextOpt.isEmpty()) continue;

            Scenario next = nextOpt.get();

            return (
                    next.getTags().isEmpty()
                            ? next.getLocation().getLine()
                            : next.getTags()
                            .get(0)
                            .getLocation()
                            .getLine()
            ).intValue() - 1;
        }

        return totalLines;
    }

    private static String rebuild(
            String sourceText,
            List<ScenarioBlock> blocks
    ) {
        String[] lines = sourceText.split("\n", -1);
        StringBuilder output = new StringBuilder();

        int cursor = 1;

        for (ScenarioBlock block : blocks) {
            appendLines(
                    output,
                    lines,
                    cursor,
                    block.startLine() - 1
            );

            if (block.configs().isEmpty()) {
                appendLines(
                        output,
                        lines,
                        block.startLine(),
                        block.endLine()
                );
            } else {
                renderCopies(output, lines, block);
            }

            cursor = block.endLine() + 1;
        }

        appendLines(
                output,
                lines,
                cursor,
                lines.length
        );

        return output.toString();
    }

    private static void renderCopies(
            StringBuilder output,
            String[] lines,
            ScenarioBlock block
    ) {
        for (int i = 0; i < block.configs().size(); i++) {
            if (i > 0) {
                output.append("\n");
            }

            renderCopy(
                    output,
                    lines,
                    block,
                    block.configs().get(i)
            );
        }
    }

    private static void renderCopy(
            StringBuilder output,
            String[] lines,
            ScenarioBlock block,
            ChannelConfig config
    ) {
        for (int lineNo = block.startLine();
             lineNo <= block.endLine();
             lineNo++) {

            String line = lines[lineNo - 1];

            // Elimina i @channel presenti direttamente sullo scenario.
            if (line.trim().startsWith("@channel:")) {
                continue;
            }

            if (lineNo == block.keywordLine()) {
                String indent = leadingWhitespace(line);

                output.append(indent)
                        .append(buildChannelTag(config))
                        .append("\n");

                // Il suffisso viene aggiunto SEMPRE,
                // anche quando esiste una sola configurazione.
                line = appendSuffix(line, config);
            }

            output.append(line).append("\n");
        }
    }

    private static String appendSuffix(
            String scenarioLine,
            ChannelConfig config
    ) {
        int colon = scenarioLine.indexOf(':');

        if (colon < 0) {
            return scenarioLine + " " + buildSuffix(config);
        }

        String prefix = scenarioLine.substring(0, colon + 1);
        String name = scenarioLine.substring(colon + 1).strip();

        return prefix + " " + name + " " + buildSuffix(config);
    }

    private static String buildChannelTag(ChannelConfig config) {
        return "@channel:Given=" + toGherkin(config.given())
                + ",When=" + toGherkin(config.when())
                + ",Then=" + toGherkin(config.then());
    }

    private static String buildSuffix(ChannelConfig config) {
        return "[Given=" + toGherkin(config.given())
                + ",When=" + toGherkin(config.when())
                + ",Then=" + toGherkin(config.then())
                + "]";
    }

    private static String toGherkin(Channel channel) {
        return ChannelGherkinMapping.toGherkin(channel);
    }

    private static void appendLines(
            StringBuilder output,
            String[] lines,
            int from,
            int to
    ) {
        for (int i = from; i <= to && i <= lines.length; i++) {
            output.append(lines[i - 1]).append("\n");
        }
    }

    private static String leadingWhitespace(String line) {
        int i = 0;

        while (i < line.length()
                && Character.isWhitespace(line.charAt(i))) {
            i++;
        }

        return line.substring(0, i);
    }

    public static final class FeatureExpansionException
            extends RuntimeException {

        public FeatureExpansionException(
                String message,
                Throwable cause
        ) {
            super(message, cause);
        }
    }
}