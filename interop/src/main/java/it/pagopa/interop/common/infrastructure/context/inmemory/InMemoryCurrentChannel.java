package it.pagopa.interop.common.infrastructure.context.inmemory;

import it.pagopa.interop.common.infrastructure.context.CurrentChannel;
import it.pagopa.interop.common.kernel.domain.Channel;

public class InMemoryCurrentChannel implements CurrentChannel {
    private Channel currentChannel;

    @Override
    public Channel getCurrentChannel() {
        return currentChannel;
    }

    @Override
    public void setCurrentChannel(Channel currentChannel) {
        this.currentChannel = currentChannel;
    }
}
