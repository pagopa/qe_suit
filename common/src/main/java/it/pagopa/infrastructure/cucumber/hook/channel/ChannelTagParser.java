package it.pagopa.infrastructure.cucumber.hook.channel;

import it.pagopa.application.ChannelKind;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class ChannelTagParser<C extends Enum<C> & ChannelKind> {

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
        for (String pair : body.split(",")) {
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

        return new ChannelConfig<>(
                resolveChannel(parsed.get("Given"), "Given", tag),
                resolveChannel(parsed.get("When"), "When", tag),
                resolveChannel(parsed.get("Then"), "Then", tag)
        );
    }

    private C resolveChannel(String value, String key, String originalTag) {
        return channelMapping.fromGherkin(value)
                .orElseThrow(() -> new ChannelTagParseException(
                        "Unknown channel value '" + value + "' for key '" + key + "' in tag: " + originalTag));
    }

    public static final class ChannelTagParseException extends RuntimeException {
        public ChannelTagParseException(String message) { super(message); }
    }
}
