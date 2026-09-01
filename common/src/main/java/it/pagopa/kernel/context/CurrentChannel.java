package it.pagopa.kernel.context;

import it.pagopa.kernel.ChannelKind;

public interface CurrentChannel<C extends ChannelKind> {
    C getCurrentChannel();

    void setCurrentChannel(C currentChannel);
}
