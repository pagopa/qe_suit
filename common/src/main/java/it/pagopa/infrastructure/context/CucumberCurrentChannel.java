package it.pagopa.infrastructure.context;

import it.pagopa.application.ChannelKind;
import it.pagopa.infrastructure.channel.CurrentChannel;

public class CucumberCurrentChannel<C extends ChannelKind> implements CurrentChannel<C> {
    private C currentChannel;

    @Override
    public C getCurrentChannel() {
        return currentChannel;
    }

    @Override
    public void setCurrentChannel(C currentChannel) {
        this.currentChannel = currentChannel;
    }
}
