package it.pagopa.interop.common.eservice.application.command;

public interface UpdateEServiceDescriptorCommand {
    UpdateEServiceDescriptorCommand voucherLifespan(Integer voucherLifespan);
    UpdateEServiceDescriptorCommand dailyCallsPerConsumer(Integer dailyCallsPerConsumer);
    UpdateEServiceDescriptorCommand dailyCallsTotal(Integer dailyCallsTotal);
}
