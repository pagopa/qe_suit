package it.pagopa.infrastructure.cucumber.channel;

import it.pagopa.application.ChannelKind;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

final class ChannelTagParser<C extends Enum<C> & ChannelKind> {

    static final String CHANNEL_TAG_PREFIX = "@channel:";
    private static final Set<String> KNOWN_KEYS = Set.of("Given", "When", "Then");

    private final ChannelGherkinMapping<C> channelMapping;

    public ChannelTagParser(ChannelGherkinMapping<C> channelMapping) {
        this.channelMapping = Objects.requireNonNull(channelMapping, "channelMapping must not be null");
    }

    public List<ChannelConfig<C>> parse(Collection<String> tags) {
        Objects.requireNonNull(tags, "tags must not be null");
        List<ChannelConfig<C>> result = new ArrayList<>();
        for (String tag : tags) {
            if (tag != null && tag.startsWith(CHANNEL_TAG_PREFIX)) {
                result.add(parseSingleTag(tag));
            }
        }
        return List.copyOf(result);
    }

    ChannelConfig<C> parseSingleTag(String tag) {
        Objects.requireNonNull(tag, "tag must not be null");
        String body = tag.substring(CHANNEL_TAG_PREFIX.length());
        if (body.isBlank()) throw new ChannelTagParseException("Empty channel tag body in: " + tag);

        Map<String, String> parsed = new LinkedHashMap<>();
        for (String pair : body.split(",", -1)) {
            int eq = pair.indexOf('=');
            if (eq < 0) throw new ChannelTagParseException("Missing '=' in pair '" + pair + "' of tag: " + tag);

            String key = pair.substring(0, eq).trim();
            String value = pair.substring(eq + 1).trim();
            if (key.isEmpty()) throw new ChannelTagParseException("Empty key in pair '" + pair + "' of tag: " + tag);
            if (!KNOWN_KEYS.contains(key)) throw new ChannelTagParseException("Unknown key '" + key + "' in tag: " + tag + ". Allowed keys: " + KNOWN_KEYS);
            if (value.isEmpty()) throw new ChannelTagParseException("Missing value for key '" + key + "' in tag: " + tag);
            if (parsed.putIfAbsent(key, value) != null) throw new ChannelTagParseException("Duplicate key '" + key + "' in tag: " + tag);
        }

        for (String required : KNOWN_KEYS) {
            if (!parsed.containsKey(required)) throw new ChannelTagParseException("Missing required key '" + required + "' in tag: " + tag);
        }

        ChannelSelection given = selection(parsed.get("Given"), "Given", tag);
        ChannelSelection when = selection(parsed.get("When"), "When", tag);
        ChannelSelection then = selection(parsed.get("Then"), "Then", tag);
        return new ChannelConfig<>(
                given.channel(), when.channel(), then.channel(),
                given.language(), when.language(), then.language()
        );
    }

    private ChannelSelection selection(String value, String key, String tag) {
        int colon = value.indexOf(':');
        String channelToken = colon < 0 ? value : value.substring(0, colon).trim();
        String languageToken = colon < 0 ? "it" : value.substring(colon + 1).trim();
        if (languageToken.isEmpty() || !languageToken.matches("[a-zA-Z]{2,8}(-[a-zA-Z0-9]{1,8})*")) {
            throw new ChannelTagParseException("Invalid language '" + languageToken + "' for key '" + key + "' in tag: " + tag);
        }
        C channel = channelMapping.fromGherkin(channelToken)
                .orElseThrow(() -> new ChannelTagParseException(
                        "Unknown channel value '" + channelToken + "' for key '" + key + "' in tag: " + tag));
        return new ChannelSelection(channel, Locale.forLanguageTag(languageToken));
    }

    private final class ChannelSelection {
        private final C channel;
        private final Locale language;
        ChannelSelection(C channel, Locale language) { this.channel = channel; this.language = language; }
        C channel() { return channel; }
        Locale language() { return language; }
    }

    public static final class ChannelTagParseException extends RuntimeException {
        public ChannelTagParseException(String message) { super(message); }
    }
}
