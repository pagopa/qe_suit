package it.pagopa.infrastructure.context;

import it.pagopa.application.ChannelKind;
import it.pagopa.infrastructure.channel.CurrentChannel;

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class InMemoryCurrentChannel<C extends ChannelKind> implements CurrentChannel<C> {
    private final ThreadLocal<C> currentChannel = new ThreadLocal<>();
    private final AtomicReference<C> defaultChannel = new AtomicReference<>();
    private final ThreadLocal<Locale> currentLanguage = ThreadLocal.withInitial(() -> Locale.ITALIAN);

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

    @Override
    public Locale getCurrentLanguage() {
        return currentLanguage.get();
    }

    @Override
    public void setCurrentLanguage(Locale currentLanguage) {
        this.currentLanguage.set(Objects.requireNonNull(currentLanguage, "currentLanguage must not be null"));
    }
}
