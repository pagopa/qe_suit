package it.pagopa.infrastructure.cucumber.hook.channel;

import it.pagopa.application.ChannelKind;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

final class ChannelConfigResolver {

    private ChannelConfigResolver() {}

    public static <C extends Enum<C> & ChannelKind> Optional<ChannelConfig<C>> resolve(
            List<ChannelConfig<C>> configs
    ) {
        Objects.requireNonNull(configs, "configs must not be null");
        return configs.isEmpty() ? Optional.empty() : Optional.of(configs.get(0));
    }
}
