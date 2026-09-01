package it.pagopa.interop.common.infrastructure.cucumber.channel;

import java.util.List;

/**
 * Renders an expanded feature's textual content from the original source text and the list
 * of {@link ScenarioBlock}s computed by {@link ScenarioBlockExtractor}.
 * <p>
 * Rendering rules per block:
 * <ul>
 *   <li>{@code configs} empty → the block is copied verbatim.</li>
 *   <li>{@code configs} has 1+ entries → one copy is emitted per config; each copy has its
 *       original {@code @channel:} tag(s) removed and replaced with exactly one matching the
 *       config, and its scenario name suffixed with {@code [Given=...,When=...,Then=...]}.</li>
 * </ul>
 * All other lines (header, background, gaps, DataTables, DocStrings, Examples, comments) are
 * copied unchanged, preserving formatting.
 */
final class ScenarioBlockRenderer {

    private ScenarioBlockRenderer() {}

    static String render(String[] lines, List<ScenarioBlock> blocks) {
        StringBuilder output = new StringBuilder();
        int cursor = 1;

        for (ScenarioBlock block : blocks) {
            appendLines(output, lines, cursor, block.startLine() - 1);

            if (block.configs().isEmpty()) {
                appendLines(output, lines, block.startLine(), block.endLine());
            } else {
                renderCopies(output, lines, block);
            }

            cursor = block.endLine() + 1;
        }

        appendLines(output, lines, cursor, lines.length);
        return output.toString();
    }

    private static void renderCopies(StringBuilder output, String[] lines, ScenarioBlock block) {
        List<ChannelConfig> configs = block.configs();
        for (int i = 0; i < configs.size(); i++) {
            if (i > 0) output.append("\n");
            renderCopy(output, lines, block, configs.get(i));
        }
    }

    private static void renderCopy(StringBuilder output, String[] lines, ScenarioBlock block, ChannelConfig config) {
        for (int lineNo = block.startLine(); lineNo <= block.endLine(); lineNo++) {
            String line = lines[lineNo - 1];

            // Elimina i @channel presenti direttamente sullo scenario.
            if (line.trim().startsWith("@channel:")) {
                continue;
            }

            if (lineNo == block.keywordLine()) {
                String indent = leadingWhitespace(line);
                output.append(indent).append(ChannelConfigFormatter.toTag(config)).append("\n");

                // Il suffisso viene aggiunto SEMPRE, anche quando esiste una sola configurazione.
                line = appendSuffix(line, config);
            }

            output.append(line).append("\n");
        }
    }

    private static String appendSuffix(String scenarioLine, ChannelConfig config) {
        int colon = scenarioLine.indexOf(':');
        String suffix = ChannelConfigFormatter.toSuffix(config);

        if (colon < 0) {
            return scenarioLine + " " + suffix;
        }

        String prefix = scenarioLine.substring(0, colon + 1);
        String name = scenarioLine.substring(colon + 1).strip();
        return prefix + " " + name + " " + suffix;
    }

    private static void appendLines(StringBuilder output, String[] lines, int from, int to) {
        for (int i = from; i <= to && i <= lines.length; i++) {
            output.append(lines[i - 1]).append("\n");
        }
    }

    private static String leadingWhitespace(String line) {
        int i = 0;
        while (i < line.length() && Character.isWhitespace(line.charAt(i))) i++;
        return line.substring(0, i);
    }
}
