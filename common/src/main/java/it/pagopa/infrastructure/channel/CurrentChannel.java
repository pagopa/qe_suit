package it.pagopa.infrastructure.channel;

import it.pagopa.application.ChannelKind;

public interface CurrentChannel<C extends ChannelKind> {
    C getCurrentChannel();

    void setCurrentChannel(C currentChannel);
}
