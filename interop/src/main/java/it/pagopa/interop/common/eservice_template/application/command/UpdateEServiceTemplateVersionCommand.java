package it.pagopa.interop.common.eservice_template.application.command;

import it.pagopa.interop.common.agreement.domain.AgreementApprovalPolicy;
import it.pagopa.interop.common.attribute.domain.Attributes;

import java.util.function.UnaryOperator;

public interface UpdateEServiceTemplateVersionCommand {
    UpdateEServiceTemplateVersionCommand description(String description);

    UpdateEServiceTemplateVersionCommand voucherLifespan(Integer voucherLifespan);

    UpdateEServiceTemplateVersionCommand dailyCallsPerConsumer(Integer dailyCallsPerConsumer);

    UpdateEServiceTemplateVersionCommand dailyCallsTotal(Integer dailyCallsTotal);

    UpdateEServiceTemplateVersionCommand agreementApprovalPolicy(AgreementApprovalPolicy agreementApprovalPolicy);

    UpdateEServiceTemplateVersionCommand attributes(Attributes attributes);

    UpdateEServiceTemplateVersionCommand attributes(UnaryOperator<Attributes> attributes);
}

