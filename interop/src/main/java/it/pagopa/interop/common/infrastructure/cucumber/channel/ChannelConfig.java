package it.pagopa.interop.common.infrastructure.cucumber.channel;

import it.pagopa.interop.common.kernel.domain.Channel;

/**
 * Immutable configuration for a multi-channel scenario execution.
 * Defines which {@link Channel} to use for each Gherkin phase.
 */
public record ChannelConfig(
        Channel given,
        Channel when,
        Channel then
) {}
