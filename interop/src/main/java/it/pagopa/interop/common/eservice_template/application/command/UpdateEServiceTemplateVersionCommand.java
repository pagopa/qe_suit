package it.pagopa.interop.common.eservice_template.application.command;

import it.pagopa.interop.common.agreement.domain.AgreementApprovalPolicy;

public interface UpdateEServiceTemplateVersionCommand {
    UpdateEServiceTemplateVersionCommand description(String description);

    UpdateEServiceTemplateVersionCommand voucherLifespan(Integer voucherLifespan);

    UpdateEServiceTemplateVersionCommand dailyCallsPerConsumer(Integer dailyCallsPerConsumer);

    UpdateEServiceTemplateVersionCommand dailyCallsTotal(Integer dailyCallsTotal);

    UpdateEServiceTemplateVersionCommand agreementApprovalPolicy(AgreementApprovalPolicy agreementApprovalPolicy);
}

