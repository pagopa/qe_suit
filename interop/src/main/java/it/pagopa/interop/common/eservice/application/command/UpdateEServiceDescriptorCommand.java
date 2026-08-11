package it.pagopa.interop.common.eservice.application.command;

import java.util.List;

public interface UpdateEServiceDescriptorCommand {
    UpdateEServiceDescriptorCommand voucherLifespan(Integer voucherLifespan);
    UpdateEServiceDescriptorCommand dailyCallsPerConsumer(Integer dailyCallsPerConsumer);
    UpdateEServiceDescriptorCommand dailyCallsTotal(Integer dailyCallsTotal);
    UpdateEServiceDescriptorCommand audience(List<String> audience);
    UpdateEServiceDescriptorCommand description(String description);
}
