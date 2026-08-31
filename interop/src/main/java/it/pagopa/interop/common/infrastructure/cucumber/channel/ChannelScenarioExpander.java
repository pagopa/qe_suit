package it.pagopa.interop.common.infrastructure.cucumber.channel;

import io.cucumber.gherkin.GherkinParser;
import io.cucumber.messages.types.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Expands a single {@code .feature} file into a new textual representation where each
 * Scenario or Scenario Outline with N {@code @channel:} tags is duplicated N times,
 * each copy retaining exactly one {@code @channel:} tag and all functional tags.
 * <p>
 * Scenarios / Outlines with 0 or 1 {@code @channel:} tags are emitted unchanged.
 * <p>
 * Strategy: use the Gherkin AST to discover <em>where</em> scenarios are and which
 * tags they carry, then use the raw source text to duplicate the blocks verbatim.
 * This preserves formatting, comments, DataTables, DocStrings, etc.
 */
public final class ChannelScenarioExpander {

    private static final GherkinParser PARSER = GherkinParser.builder()
            .includeGherkinDocument(true)
            .includeSource(false)
            .includePickles(false)
            .build();

    private ChannelScenarioExpander() {}

    /**
     * Expands the given feature file source text.
     *
     * @param sourcePath path used for Gherkin parser URI (display only)
     * @param sourceText full UTF-8 content of the source {@code .feature} file
     * @return the expanded content; identical to input when no multi-channel scenario exists
     */
    public static String expand(Path sourcePath, String sourceText) {
        GherkinDocument doc = parseDocument(sourcePath, sourceText);

        Optional<Feature> featureOpt = doc.getFeature();
        if (featureOpt.isEmpty()) {
            return sourceText;
        }

        Feature feature = featureOpt.get();
        List<ScenarioBlock> blocks = extractScenarioBlocks(feature, sourceText);

        if (blocks.stream().noneMatch(b -> b.channelConfigs().size() > 1)) {
            return sourceText; // nothing to expand
        }

        return rebuildFeature(sourceText, blocks);
    }

    // -------------------------------------------------------------------------
    // Gherkin parsing
    // -------------------------------------------------------------------------

    private static GherkinDocument parseDocument(Path sourcePath, String sourceText) {
        try {
            return PARSER.parse(sourcePath.toString(), sourceText.getBytes())
                    .filter(e -> e.getGherkinDocument().isPresent())
                    .findFirst()
                    .flatMap(Envelope::getGherkinDocument)
                    .orElseThrow(() -> new IllegalStateException(
                            "Gherkin parser produced no document for: " + sourcePath));
        } catch (RuntimeException e) {
            throw new FeatureExpansionException(
                    "Failed to parse feature file: " + sourcePath, e);
        }
    }

    // -------------------------------------------------------------------------
    // Block extraction (AST → line ranges)
    // -------------------------------------------------------------------------

    private record ScenarioBlock(
            int startLine,   // 1-based, inclusive – first tag or keyword line
            int endLine,     // 1-based, inclusive – last line of this scenario block
            List<ChannelConfig> channelConfigs,
            List<String> allTagNames,
            String name,
            boolean isOutline
    ) {}

    /**
     * Returns one {@link ScenarioBlock} per Scenario/Outline in declaration order,
     * with start/end line numbers computed from AST location data.
     */
    private static List<ScenarioBlock> extractScenarioBlocks(Feature feature, String sourceText) {
        String[] lines = sourceText.split("\n", -1);
        int totalLines = lines.length;

        List<FeatureChild> children = feature.getChildren();
        List<ScenarioBlock> blocks = new ArrayList<>();

        for (int i = 0; i < children.size(); i++) {
            FeatureChild child = children.get(i);
            Optional<Scenario> scenarioOpt = child.getScenario();
            if (scenarioOpt.isEmpty()) continue;

            Scenario scenario = scenarioOpt.get();
            List<String> tagNames = scenario.getTags().stream().map(Tag::getName).toList();
            List<ChannelConfig> configs = ChannelTagParser.parse(tagNames);

            // Start line: first tag line or scenario keyword line (1-based)
            int scenarioKeywordLine = scenario.getLocation().getLine().intValue();
            int startLine = scenario.getTags().isEmpty()
                    ? scenarioKeywordLine
                    : scenario.getTags().get(0).getLocation().getLine().intValue();

            // End line: line before the next scenario/outline starts, or last line of file
            int endLine = totalLines;
            for (int j = i + 1; j < children.size(); j++) {
                Optional<Scenario> next = children.get(j).getScenario();
                if (next.isPresent()) {
                    List<Tag> nextTags = next.get().getTags();
                    int nextStart = nextTags.isEmpty()
                            ? next.get().getLocation().getLine().intValue()
                            : nextTags.get(0).getLocation().getLine().intValue();
                    endLine = nextStart - 1;
                    break;
                }
            }

            // Trim trailing blank lines
            while (endLine > startLine && lines[endLine - 1].isBlank()) {
                endLine--;
            }

            blocks.add(new ScenarioBlock(
                    startLine, endLine, configs, tagNames,
                    scenario.getName(), !scenario.getExamples().isEmpty()));
        }
        return blocks;
    }

    // -------------------------------------------------------------------------
    // Feature rebuilding
    // -------------------------------------------------------------------------

    private static String rebuildFeature(String sourceText, List<ScenarioBlock> blocks) {
        String[] lines = sourceText.split("\n", -1);
        StringBuilder sb = new StringBuilder();
        int currentLine = 1; // 1-based cursor

        for (ScenarioBlock block : blocks) {
            // Emit everything before this block (header, background, gaps)
            appendLines(sb, lines, currentLine, block.startLine() - 1);
            currentLine = block.endLine() + 1;

            List<ChannelConfig> configs = block.channelConfigs();

            if (configs.size() <= 1) {
                // No expansion needed; emit verbatim
                appendLines(sb, lines, block.startLine(), block.endLine());
            } else {
                // Emit one copy per channel config
                for (int ci = 0; ci < configs.size(); ci++) {
                    if (ci > 0) sb.append("\n");
                    ChannelConfig config = configs.get(ci);
                    String suffix = buildSuffix(config);
                    emitExpandedBlock(sb, lines, block, config, suffix);
                }
            }
        }

        // Emit remaining lines after the last block
        appendLines(sb, lines, currentLine, lines.length);

        return sb.toString();
    }

    /**
     * Emits a single expanded copy of a scenario block, replacing its channel tags with
     * a single {@code @channel:} tag matching {@code config} and appending a suffix to
     * the scenario name.
     */
    private static void emitExpandedBlock(
            StringBuilder sb,
            String[] lines,
            ScenarioBlock block,
            ChannelConfig config,
            String suffix) {

        String singleChannelTag = buildChannelTag(config);
        String channelTagToInject = null; // will be injected before the scenario keyword
        boolean channelTagInjected = false;

        for (int l = block.startLine(); l <= block.endLine(); l++) {
            String line = lines[l - 1];
            String trimmed = line.trim();

            // Skip all @channel: tags; we'll inject the single one before the keyword line
            if (trimmed.startsWith("@channel:")) {
                if (!channelTagInjected) {
                    channelTagToInject = singleChannelTag;
                }
                continue;
            }

            // Before the scenario keyword line, inject the single channel tag
            boolean isKeywordLine = trimmed.startsWith("Scenario:") ||
                                    trimmed.startsWith("Scenario Outline:") ||
                                    trimmed.startsWith("Esquema do Cenário:");
            if (isKeywordLine && channelTagToInject != null && !channelTagInjected) {
                String indent = leadingWhitespace(line);
                sb.append(indent).append(channelTagToInject).append("\n");
                channelTagInjected = true;
            }

            // Rename scenario: append suffix to the name
            if (isKeywordLine) {
                sb.append(appendSuffixToScenarioLine(line, suffix)).append("\n");
            } else {
                sb.append(line).append("\n");
            }
        }
    }

    // -------------------------------------------------------------------------
    // Text utilities
    // -------------------------------------------------------------------------

    private static void appendLines(StringBuilder sb, String[] lines, int from, int to) {
        for (int i = from; i <= to && i <= lines.length; i++) {
            sb.append(lines[i - 1]).append("\n");
        }
    }

    private static String leadingWhitespace(String line) {
        int i = 0;
        while (i < line.length() && Character.isWhitespace(line.charAt(i))) i++;
        return line.substring(0, i);
    }

    private static String appendSuffixToScenarioLine(String line, String suffix) {
        // Find the colon and everything after it is the scenario name
        int colon = line.indexOf(':');
        if (colon < 0) return line + " " + suffix;
        String rest = line.substring(colon + 1).stripTrailing();
        return line.substring(0, colon + 1) + " " + rest.strip() + " " + suffix;
    }

    private static String buildChannelTag(ChannelConfig config) {
        return "@channel:Given=" + ChannelGherkinMapping.toGherkin(config.given())
                + ",When=" + ChannelGherkinMapping.toGherkin(config.when())
                + ",Then=" + ChannelGherkinMapping.toGherkin(config.then());
    }

    private static String buildSuffix(ChannelConfig config) {
        return "[Given=" + ChannelGherkinMapping.toGherkin(config.given())
                + ",When=" + ChannelGherkinMapping.toGherkin(config.when())
                + ",Then=" + ChannelGherkinMapping.toGherkin(config.then()) + "]";
    }

    // -------------------------------------------------------------------------
    // Exception
    // -------------------------------------------------------------------------

    public static final class FeatureExpansionException extends RuntimeException {
        public FeatureExpansionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
