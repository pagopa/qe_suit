package it.pagopa.interop.common.eservice.application.command;

import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementApprovalPolicy;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributesSeed;

import java.util.List;

public interface UpdateEServiceDescriptorCommand {
    UpdateEServiceDescriptorCommand voucherLifespan(Integer voucherLifespan);
    UpdateEServiceDescriptorCommand dailyCallsPerConsumer(Integer dailyCallsPerConsumer);
    UpdateEServiceDescriptorCommand dailyCallsTotal(Integer dailyCallsTotal);
    UpdateEServiceDescriptorCommand audience(List<String> audience);
    UpdateEServiceDescriptorCommand description(String description);
    UpdateEServiceDescriptorCommand attributes(DescriptorAttributesSeed seed);
    UpdateEServiceDescriptorCommand agreementApprovalPolicy(AgreementApprovalPolicy policy);
}
