package it.pagopa.infrastructure.context;

import it.pagopa.application.ChannelKind;
import it.pagopa.infrastructure.channel.CurrentChannel;

import java.util.Locale;
import java.util.Objects;

public class CucumberCurrentChannel<C extends ChannelKind> implements CurrentChannel<C> {
    private C currentChannel;
    private Locale currentLanguage = Locale.ITALIAN;

    @Override
    public C getCurrentChannel() {
        return currentChannel;
    }

    @Override
    public void setCurrentChannel(C currentChannel) {
        this.currentChannel = currentChannel;
    }

    @Override
    public Locale getCurrentLanguage() {
        return currentLanguage;
    }

    @Override
    public void setCurrentLanguage(Locale currentLanguage) {
        this.currentLanguage = Objects.requireNonNull(currentLanguage, "currentLanguage must not be null");
    }
}
