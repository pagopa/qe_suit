package it.pagopa.interop.new_arch.common.infrastructure.cucumber.context;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.common.contract.model.shared.enums.Channel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ScenarioScope
public class ChannelContext {
    private Channel currentChannel;
}
