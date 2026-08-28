package it.pagopa.interop.web.eservice.application;

import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;
import lombok.Getter;

import java.util.List;

@Getter
public class WebUpdateEServiceDescriptorCommand implements UpdateEServiceDescriptorCommand {
    private final UpdateEServiceDescriptorSeed webPayload;

    public WebUpdateEServiceDescriptorCommand(UpdateEServiceDescriptorSeed webPayload) {
        this.webPayload = webPayload;
    }

    public static WebUpdateEServiceDescriptorCommand from(UpdateEServiceDescriptorSeed payload) {
        return new WebUpdateEServiceDescriptorCommand(payload);
    }

    @Override
    public UpdateEServiceDescriptorCommand voucherLifespan(Integer voucherLifespan) {
        webPayload.setVoucherLifespan(voucherLifespan);
        return this;
    }

    @Override
    public UpdateEServiceDescriptorCommand dailyCallsPerConsumer(Integer dailyCallsPerConsumer) {
        webPayload.setDailyCallsPerConsumer(dailyCallsPerConsumer);
        return this;
    }

    @Override
    public UpdateEServiceDescriptorCommand dailyCallsTotal(Integer dailyCallsTotal) {
        webPayload.setDailyCallsTotal(dailyCallsTotal);
        return this;
    }

    @Override
    public UpdateEServiceDescriptorCommand audience(List<String> audience) {
        webPayload.setAudience(audience);
        return this;
    }

    @Override
    public UpdateEServiceDescriptorCommand description(String description) {
        webPayload.setDescription(description);
        return this;
    }
}
