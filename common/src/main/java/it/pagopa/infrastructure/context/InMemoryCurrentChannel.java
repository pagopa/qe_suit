package it.pagopa.infrastructure.context;

import it.pagopa.application.ChannelKind;
import it.pagopa.infrastructure.channel.CurrentChannel;

import java.util.concurrent.atomic.AtomicReference;

public class InMemoryCurrentChannel<C extends ChannelKind> implements CurrentChannel<C> {
    private final ThreadLocal<C> currentChannel = new ThreadLocal<>();
    private final AtomicReference<C> defaultChannel = new AtomicReference<>();

    @Override
    public C getCurrentChannel() {
        C channel = currentChannel.get();
        return channel != null ? channel : defaultChannel.get();
    }

    @Override
    public void setCurrentChannel(C currentChannel) {
        defaultChannel.compareAndSet(null, currentChannel);
        this.currentChannel.set(currentChannel);
    }
}
