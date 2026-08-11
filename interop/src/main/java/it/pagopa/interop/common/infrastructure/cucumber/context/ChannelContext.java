package it.pagopa.interop.common.infrastructure.cucumber.context;

import it.pagopa.interop.common.infrastructure.context.CurrentChannel;
import it.pagopa.interop.common.kernel.domain.Channel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChannelContext implements CurrentChannel {
    private Channel currentChannel;
}
