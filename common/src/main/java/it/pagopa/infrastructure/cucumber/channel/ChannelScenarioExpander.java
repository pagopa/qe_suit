package it.pagopa.infrastructure.cucumber.channel;

import io.cucumber.gherkin.GherkinParser;
import io.cucumber.messages.types.Envelope;
import io.cucumber.messages.types.Feature;
import io.cucumber.messages.types.GherkinDocument;
import io.cucumber.messages.types.Tag;
import it.pagopa.application.ChannelKind;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

public final class ChannelScenarioExpander<C extends Enum<C> & ChannelKind>
        implements ChannelScenarioExpansion {

    private static final GherkinParser PARSER = GherkinParser.builder()
            .includeGherkinDocument(true)
            .includeSource(false)
            .includePickles(false)
            .build();

    private final ChannelTagParser<C> channelTagParser;
    private final ScenarioBlockExtractor<C> extractor;
    private final ScenarioBlockRenderer<C> renderer;

    public ChannelScenarioExpander(
            ChannelGherkinMapping<C> channelMapping,
            ChannelConfig<C> defaultConfig
    ) {
        Objects.requireNonNull(channelMapping, "channelMapping must not be null");
        this.channelTagParser = new ChannelTagParser<>(channelMapping);
        this.extractor = new ScenarioBlockExtractor<>(channelTagParser, defaultConfig);
        this.renderer = new ScenarioBlockRenderer<>(new ChannelConfigFormatter<>(channelMapping));
    }

    @Override
    public String expand(Path sourcePath, String sourceText) {
        Feature feature = parseFeature(sourcePath, sourceText).orElse(null);
        if (feature == null) return sourceText;

        List<ChannelConfig<C>> featureConfigs = channelTagParser.parse(
                feature.getTags().stream().map(Tag::getName).toList());
        String[] lines = sourceText.split("\n", -1);
        List<ScenarioBlock<C>> blocks = extractor.extract(feature, lines, featureConfigs);
        if (blocks.isEmpty()) return sourceText;

        // The effective configuration is materialized on each generated scenario.
        // Remove only inherited feature-level @channel tags so Cucumber cannot
        // resolve the inherited config before the generated scenario config.
        for (Tag tag : feature.getTags()) {
            if (!tag.getName().startsWith(ChannelTagParser.CHANNEL_TAG_PREFIX)) continue;
            int lineIndex = tag.getLocation().getLine().intValue() - 1;
            lines[lineIndex] = lines[lineIndex].replaceFirst(
                    "(?<!\\S)" + Pattern.quote(tag.getName()) + "(?=\\s|$)", "");
        }
        return renderer.render(lines, blocks);
    }

    private Optional<Feature> parseFeature(Path sourcePath, String sourceText) {
        try {
            return PARSER.parse(sourcePath.toString(), sourceText.getBytes(StandardCharsets.UTF_8))
                    .filter(e -> e.getGherkinDocument().isPresent())
                    .findFirst()
                    .flatMap(Envelope::getGherkinDocument)
                    .flatMap(GherkinDocument::getFeature);
        } catch (RuntimeException e) {
            throw new FeatureExpansionException("Failed to parse feature file: " + sourcePath, e);
        }
    }

    public static final class FeatureExpansionException extends RuntimeException {
        public FeatureExpansionException(String message, Throwable cause) { super(message, cause); }
    }
}
