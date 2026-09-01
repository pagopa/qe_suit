package it.pagopa.infrastructure.cucumber.hook.channel;

import it.pagopa.application.ChannelKind;

import java.util.List;
import java.util.Objects;

final class ScenarioBlockRenderer<C extends Enum<C> & ChannelKind> {

    private final ChannelConfigFormatter<C> formatter;

    ScenarioBlockRenderer(ChannelConfigFormatter<C> formatter) {
        this.formatter = Objects.requireNonNull(formatter, "formatter must not be null");
    }

    String render(String[] lines, List<ScenarioBlock<C>> blocks) {
        StringBuilder output = new StringBuilder();
        int cursor = 1;
        for (ScenarioBlock<C> block : blocks) {
            appendLines(output, lines, cursor, block.startLine() - 1);
            if (block.configs().isEmpty()) appendLines(output, lines, block.startLine(), block.endLine());
            else renderCopies(output, lines, block);
            cursor = block.endLine() + 1;
        }
        appendLines(output, lines, cursor, lines.length);
        return output.toString();
    }

    private void renderCopies(StringBuilder output, String[] lines, ScenarioBlock<C> block) {
        List<ChannelConfig<C>> configs = block.configs();
        for (int i = 0; i < configs.size(); i++) {
            if (i > 0) output.append('\n');
            renderCopy(output, lines, block, configs.get(i));
        }
    }

    private void renderCopy(
            StringBuilder output,
            String[] lines,
            ScenarioBlock<C> block,
            ChannelConfig<C> config
    ) {
        for (int lineNo = block.startLine(); lineNo <= block.endLine(); lineNo++) {
            String line = lines[lineNo - 1];
            if (line.trim().startsWith(ChannelTagParser.CHANNEL_TAG_PREFIX)) continue;
            if (lineNo == block.keywordLine()) {
                output.append(leadingWhitespace(line)).append(formatter.toTag(config)).append('\n');
                line = appendSuffix(line, config);
            }
            output.append(line).append('\n');
        }
    }

    private String appendSuffix(String scenarioLine, ChannelConfig<C> config) {
        int colon = scenarioLine.indexOf(':');
        String suffix = formatter.toSuffix(config);
        if (colon < 0) return scenarioLine + " " + suffix;
        return scenarioLine.substring(0, colon + 1) + " "
                + scenarioLine.substring(colon + 1).strip() + " " + suffix;
    }

    private static void appendLines(StringBuilder output, String[] lines, int from, int to) {
        for (int i = from; i <= to && i <= lines.length; i++) output.append(lines[i - 1]).append('\n');
    }

    private static String leadingWhitespace(String line) {
        int i = 0;
        while (i < line.length() && Character.isWhitespace(line.charAt(i))) i++;
        return line.substring(0, i);
    }
}
