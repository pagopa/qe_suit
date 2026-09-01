package it.pagopa.interop.common.infrastructure.context.cucumber;

import it.pagopa.interop.common.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.kernel.domain.Channel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CucumberCurrentChannel implements CurrentChannel {
    private Channel currentChannel;
}
