package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.journey.application.ChannelJourney;
import it.pagopa.interop.common.kernel.domain.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChannelJourneyImpl implements ChannelJourney<ChannelJourneyImpl> {

    private final CurrentChannel<Channel> currentChannel;

    @Override
    public ChannelJourneyImpl switchChannel(Channel channel) {
        currentChannel.setCurrentChannel(channel);
        return this;
    }
}
