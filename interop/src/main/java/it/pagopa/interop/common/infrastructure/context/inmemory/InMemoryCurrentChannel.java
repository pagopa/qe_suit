package it.pagopa.interop.common.infrastructure.context.inmemory;

import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.kernel.domain.Channel;

import java.util.concurrent.atomic.AtomicReference;

public class InMemoryCurrentChannel implements CurrentChannel<Channel> {
    private final ThreadLocal<Channel> currentChannel = new ThreadLocal<>();
    private final AtomicReference<Channel> defaultChannel = new AtomicReference<>();

    @Override
    public Channel getCurrentChannel() {
        Channel channel = currentChannel.get();
        return channel != null ? channel : defaultChannel.get();
    }

    @Override
    public void setCurrentChannel(Channel currentChannel) {
        defaultChannel.compareAndSet(null, currentChannel);
        this.currentChannel.set(currentChannel);
    }
}
