package it.pagopa.interop.common.kernel.context;

import it.pagopa.interop.common.kernel.domain.Channel;

public interface CurrentChannel {
    Channel getCurrentChannel();

    void setCurrentChannel(Channel currentChannel);
}
