package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.journey.application.ChannelJourney;
import it.pagopa.interop.common.journey.application.UserJourney;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChannelJourneyImpl implements ChannelJourney<ChannelJourneyImpl> {

    private final CurrentChannel<Channel> currentUserSession;

    @Override
    public ChannelJourneyImpl switchChannel(Channel channel) {
        currentUserSession.setCurrentChannel(channel);
        return this;
    }
}
