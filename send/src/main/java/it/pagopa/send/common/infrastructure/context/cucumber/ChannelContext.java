package it.pagopa.send.common.infrastructure.context.cucumber;

import it.pagopa.send.common.kernel.context.CurrentChannel;
import it.pagopa.send.common.kernel.domain.Channel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChannelContext implements CurrentChannel {
    private Channel currentChannel;
}
