package it.pagopa.interop.common.infrastructure.cucumber.channel;

import it.pagopa.interop.common.kernel.domain.Channel;

import java.util.*;

/**
 * Parses Gherkin {@code @channel:Given=X,When=Y,Then=Z} tags into {@link ChannelConfig} instances.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Recognise only tags with the {@code @channel:} prefix.</li>
 *   <li>Ignore all other tags.</li>
 *   <li>Require all three keys: {@code Given}, {@code When}, {@code Then}.</li>
 *   <li>Reject unknown keys, duplicate keys, missing values, and unknown channel names.</li>
 *   <li>Preserve the declaration order of the tags.</li>
 * </ul>
 *
 * All parsing logic is concentrated here; no other class should call {@code split()} on channel tags.
 */
public final class ChannelTagParser {

    static final String CHANNEL_TAG_PREFIX = "@channel:";
    private static final Set<String> KNOWN_KEYS = Set.of("Given", "When", "Then");

    private ChannelTagParser() {}

    /**
     * Parses all {@code @channel:…} tags from the provided collection and returns
     * a list of {@link ChannelConfig} in declaration order.
     * Non-channel tags are silently ignored.
     *
     * @param tags raw Gherkin tag names (including the {@code @} prefix)
     * @return an ordered list of parsed configs; empty if no channel tags are present
     * @throws ChannelTagParseException if any channel tag is malformed
     */
    public static List<ChannelConfig> parse(Collection<String> tags) {
        List<ChannelConfig> result = new ArrayList<>();
        for (String tag : tags) {
            if (tag != null && tag.startsWith(CHANNEL_TAG_PREFIX)) {
                result.add(parseSingleTag(tag));
            }
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Parses a single {@code @channel:Given=X,When=Y,Then=Z} tag.
     */
    static ChannelConfig parseSingleTag(String tag) {
        String body = tag.substring(CHANNEL_TAG_PREFIX.length());

        if (body.isBlank()) {
            throw new ChannelTagParseException("Empty channel tag body in: " + tag);
        }

        Map<String, String> parsed = new LinkedHashMap<>();
        String[] pairs = body.split(",");
        for (String pair : pairs) {
            int eq = pair.indexOf('=');
            if (eq < 0) {
                throw new ChannelTagParseException(
                        "Missing '=' in pair '" + pair + "' of tag: " + tag);
            }
            String key = pair.substring(0, eq).trim();
            String value = pair.substring(eq + 1).trim();

            if (key.isEmpty()) {
                throw new ChannelTagParseException(
                        "Empty key in pair '" + pair + "' of tag: " + tag);
            }
            if (!KNOWN_KEYS.contains(key)) {
                throw new ChannelTagParseException(
                        "Unknown key '" + key + "' in tag: " + tag
                        + ". Allowed keys: " + KNOWN_KEYS);
            }
            if (value.isEmpty()) {
                throw new ChannelTagParseException(
                        "Missing value for key '" + key + "' in tag: " + tag);
            }
            if (parsed.containsKey(key)) {
                throw new ChannelTagParseException(
                        "Duplicate key '" + key + "' in tag: " + tag);
            }
            parsed.put(key, value);
        }

        for (String required : KNOWN_KEYS) {
            if (!parsed.containsKey(required)) {
                throw new ChannelTagParseException(
                        "Missing required key '" + required + "' in tag: " + tag);
            }
        }

        Channel given = resolveChannel(parsed.get("Given"), "Given", tag);
        Channel when = resolveChannel(parsed.get("When"), "When", tag);
        Channel then = resolveChannel(parsed.get("Then"), "Then", tag);

        return new ChannelConfig(given, when, then);
    }

    private static Channel resolveChannel(String value, String key, String originalTag) {
        return ChannelGherkinMapping.fromGherkin(value)
                .orElseThrow(() -> new ChannelTagParseException(
                        "Unknown channel value '" + value + "' for key '" + key
                        + "' in tag: " + originalTag));
    }

    // -------------------------------------------------------------------------
    // Exception
    // -------------------------------------------------------------------------

    public static final class ChannelTagParseException extends RuntimeException {
        public ChannelTagParseException(String message) {
            super(message);
        }
    }
}
