package it.pagopa.interop.common.infrastructure.cucumber.channel;

import java.util.List;
import java.util.Optional;

/**
 * Strategy for choosing which {@link ChannelConfig} to use for a single scenario execution,
 * given the list of all configs declared via {@code @channel:…} tags.
 * <p>
 * Rules (in order):
 * <ul>
 *   <li>0 configs → {@link Optional#empty()} (legacy / default behaviour).</li>
 *   <li>1 config  → that config is used directly.</li>
 *   <li>N configs → the <em>first</em> declared config is used (deterministic default,
 *       useful when running via IntelliJ green-button on the original feature source).</li>
 * </ul>
 *
 * <p>This class is isolated so the strategy can be changed in the future without touching
 * hook or listener code.
 */
public final class ChannelConfigResolver {

    private ChannelConfigResolver() {}

    /**
     * Resolves a single config from the list of all declared configs.
     *
     * @param configs ordered list returned by {@link ChannelTagParser#parse(java.util.Collection)}
     * @return the resolved config, or empty for legacy behaviour
     */
    public static Optional<ChannelConfig> resolve(List<ChannelConfig> configs) {
        if (configs.isEmpty()) {
            return Optional.empty();
        }
        // 1 config: exact match. N configs: use the first (deterministic default).
        return Optional.of(configs.get(0));
    }
}
