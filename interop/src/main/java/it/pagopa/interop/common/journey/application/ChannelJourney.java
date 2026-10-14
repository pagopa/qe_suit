package it.pagopa.interop.common.journey.application;

import it.pagopa.interop.common.kernel.domain.Channel;

public interface ChannelJourney<SELF extends ChannelJourney<SELF>> extends JourneyModule {
    SELF switchChannel(Channel channel);

    default SELF resetChannel(Channel channel) {
        return switchChannel(channel);
    }
}
