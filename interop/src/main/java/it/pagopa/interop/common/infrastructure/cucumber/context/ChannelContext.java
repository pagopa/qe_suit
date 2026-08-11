package it.pagopa.interop.common.infrastructure.cucumber.context;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.common.infrastructure.context.CurrentChannel;
import it.pagopa.interop.common.kernel.domain.Channel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ScenarioScope
public class ChannelContext implements CurrentChannel {
    private Channel currentChannel;
}
