package it.pagopa.send.common.kernel.context;

import it.pagopa.send.common.kernel.domain.Channel;

public interface CurrentChannel {
    Channel getCurrentChannel();

    void setCurrentChannel(Channel currentChannel);
}
