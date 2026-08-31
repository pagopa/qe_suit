package it.pagopa.interop.bff.eservice.application;

import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;
import lombok.Getter;

import java.util.List;

@Getter
public class BffUpdateEServiceDescriptorCommand implements UpdateEServiceDescriptorCommand {
    private final UpdateEServiceDescriptorSeed bffPayload;

    public static BffUpdateEServiceDescriptorCommand from(UpdateEServiceDescriptorSeed payload) {
        return new BffUpdateEServiceDescriptorCommand(payload);
    }

    private BffUpdateEServiceDescriptorCommand(UpdateEServiceDescriptorSeed payload) {
        this.bffPayload = payload;
    }

    public BffUpdateEServiceDescriptorCommand() {
        this.bffPayload = new UpdateEServiceDescriptorSeed();
    }

    @Override
    public UpdateEServiceDescriptorCommand voucherLifespan(Integer voucherLifespan) {
        bffPayload.setVoucherLifespan(voucherLifespan);
        return this;
    }

    @Override
    public UpdateEServiceDescriptorCommand dailyCallsPerConsumer(Integer dailyCallsPerConsumer) {
        bffPayload.setDailyCallsPerConsumer(dailyCallsPerConsumer);
        return this;
    }

    @Override
    public UpdateEServiceDescriptorCommand dailyCallsTotal(Integer dailyCallsTotal) {
        bffPayload.setDailyCallsTotal(dailyCallsTotal);
        return this;
    }

    @Override
    public UpdateEServiceDescriptorCommand audience(List<String> audience) {
        bffPayload.setAudience(audience);
        return this;
    }

    @Override
    public UpdateEServiceDescriptorCommand description(String description) {
        bffPayload.setDescription(description);
        return this;
    }
}
