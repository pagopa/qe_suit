package it.pagopa.infrastructure.cucumber.hook.channel;

import it.pagopa.application.ChannelKind;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class ChannelGherkinMapping<C extends Enum<C> & ChannelKind> {

    private final Map<String, C> fromGherkin;
    private final Map<C, String> toGherkin;

    public ChannelGherkinMapping(Map<String, C> fromGherkin, Map<C, String> toGherkin) {
        Objects.requireNonNull(fromGherkin, "fromGherkin must not be null");
        Objects.requireNonNull(toGherkin, "toGherkin must not be null");

        Map<String, C> normalized = new LinkedHashMap<>();
        fromGherkin.forEach((name, channel) -> {
            String key = normalize(Objects.requireNonNull(name, "Gherkin name must not be null"));
            C previous = normalized.putIfAbsent(key, Objects.requireNonNull(channel, "channel must not be null"));
            if (previous != null && previous != channel) {
                throw new IllegalArgumentException("Conflicting Gherkin mapping for: " + key);
            }
        });

        this.fromGherkin = Map.copyOf(normalized);
        this.toGherkin = Map.copyOf(toGherkin);
    }

    public Optional<C> fromGherkin(String value) {
        if (value == null) return Optional.empty();
        return Optional.ofNullable(fromGherkin.get(normalize(value)));
    }

    public String toGherkin(C channel) {
        String value = toGherkin.get(Objects.requireNonNull(channel, "channel must not be null"));
        if (value == null) {
            throw new IllegalArgumentException("No Gherkin mapping configured for channel: " + channel);
        }
        return value;
    }

    private static String normalize(String value) {
        return value.trim().toUpperCase(Locale.ROOT);
    }
}
