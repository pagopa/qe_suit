package it.pagopa.interop.bff.eservice.application;

import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorQuotas;
import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import lombok.Getter;

@Getter
public class BffUpdateEServiceDescriptorCommand implements UpdateEServiceDescriptorCommand {
    private final UpdateEServiceDescriptorQuotas bffPayload;

    public static BffUpdateEServiceDescriptorCommand from(UpdateEServiceDescriptorQuotas payload) {
        return new BffUpdateEServiceDescriptorCommand(payload);
    }

    private BffUpdateEServiceDescriptorCommand(UpdateEServiceDescriptorQuotas payload) {
        this.bffPayload = payload;
    }

    public BffUpdateEServiceDescriptorCommand() {
        this.bffPayload = new UpdateEServiceDescriptorQuotas();
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
}
