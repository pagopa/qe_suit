package it.pagopa.interop.common.domain.context;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.common.domain.enums.Channel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ScenarioScope
public class ChannelContext {
    private Channel channel;
}
