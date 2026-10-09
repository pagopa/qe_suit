package it.pagopa.interop.common.eservice.application.command;

import it.pagopa.interop.common.agreement.domain.AgreementApprovalPolicy;
import it.pagopa.interop.common.attribute.domain.Attributes;

import java.util.List;

public interface UpdateEServiceDescriptorCommand {
    UpdateEServiceDescriptorCommand voucherLifespan(Integer voucherLifespan);
    UpdateEServiceDescriptorCommand dailyCallsPerConsumer(Integer dailyCallsPerConsumer);
    UpdateEServiceDescriptorCommand dailyCallsTotal(Integer dailyCallsTotal);
    UpdateEServiceDescriptorCommand audience(List<String> audience);
    UpdateEServiceDescriptorCommand description(String description);
    UpdateEServiceDescriptorCommand attributes(Attributes attributes);
    UpdateEServiceDescriptorCommand agreementApprovalPolicy(AgreementApprovalPolicy policy);
}
