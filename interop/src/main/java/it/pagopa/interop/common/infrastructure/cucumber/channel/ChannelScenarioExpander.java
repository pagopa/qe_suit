package it.pagopa.interop.common.infrastructure.cucumber.channel;

import io.cucumber.gherkin.GherkinParser;
import io.cucumber.messages.types.Envelope;
import io.cucumber.messages.types.Feature;
import io.cucumber.messages.types.GherkinDocument;
import io.cucumber.messages.types.Tag;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Expands a single {@code .feature} file into a new textual representation where each
 * Scenario or Scenario Outline is materialised with exactly one {@code @channel:} tag per
 * effective {@link ChannelConfig}, inheriting Feature-level {@code @channel:} tags unless
 * overridden at Scenario level (see {@link EffectiveChannelConfigResolver}).
 * <p>
 * Orchestrates: parsing ({@link GherkinParser}), block extraction
 * ({@link ScenarioBlockExtractor}) and rendering ({@link ScenarioBlockRenderer}).
 */
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
                feature.getTags().stream().map(Tag::getName).toList());

        String[] lines = sourceText.split("\n", -1);
        List<ScenarioBlock> blocks = ScenarioBlockExtractor.extract(feature, lines, featureConfigs);

        return blocks.isEmpty() ? sourceText : ScenarioBlockRenderer.render(lines, blocks);
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

    public static final class FeatureExpansionException extends RuntimeException {
        public FeatureExpansionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
