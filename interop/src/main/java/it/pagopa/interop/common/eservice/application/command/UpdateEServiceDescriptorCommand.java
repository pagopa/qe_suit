package it.pagopa.interop.common.eservice.application.command;

import it.pagopa.interop.common.agreement.domain.AgreementApprovalPolicy;
import it.pagopa.interop.common.attribute.domain.Attributes;

import java.util.List;
import java.util.function.UnaryOperator;

public interface UpdateEServiceDescriptorCommand {
    UpdateEServiceDescriptorCommand interfaceDocumentPath(String path);
    String getInterfaceDocumentPath();

    UpdateEServiceDescriptorCommand voucherLifespan(Integer voucherLifespan);
    UpdateEServiceDescriptorCommand dailyCallsPerConsumer(Integer dailyCallsPerConsumer);
    UpdateEServiceDescriptorCommand dailyCallsTotal(Integer dailyCallsTotal);
    UpdateEServiceDescriptorCommand audience(List<String> audience);
    UpdateEServiceDescriptorCommand description(String description);
    UpdateEServiceDescriptorCommand attributes(Attributes attributes);
    UpdateEServiceDescriptorCommand attributes(UnaryOperator<Attributes> attributes);
    UpdateEServiceDescriptorCommand agreementApprovalPolicy(AgreementApprovalPolicy policy);
}
