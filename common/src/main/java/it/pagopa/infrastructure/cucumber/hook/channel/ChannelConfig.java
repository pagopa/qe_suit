package it.pagopa.infrastructure.cucumber.hook.channel;

import it.pagopa.application.ChannelKind;

import java.util.Objects;

public record ChannelConfig<C extends Enum<C> & ChannelKind>(C given, C when, C then) {
    public ChannelConfig {
        Objects.requireNonNull(given, "given must not be null");
        Objects.requireNonNull(when, "when must not be null");
        Objects.requireNonNull(then, "then must not be null");
    }
}
