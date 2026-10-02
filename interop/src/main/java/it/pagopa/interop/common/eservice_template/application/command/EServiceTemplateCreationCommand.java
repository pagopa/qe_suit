package it.pagopa.interop.common.eservice_template.application.command;

import it.pagopa.interop.common.agreement.domain.AgreementApprovalPolicy;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;

public interface EServiceTemplateCreationCommand {
    EServiceTemplateCreationCommand name(String name);

    EServiceTemplateCreationCommand intendedTarget(String intendedTarget);

    EServiceTemplateCreationCommand description(String description);

    EServiceTemplateCreationCommand technology(EServiceTechnology technology);

    EServiceTemplateCreationCommand mode(EServiceMode mode);

    EServiceTemplateCreationCommand isSignalHubEnabled(Boolean isSignalHubEnabled);

    EServiceTemplateCreationCommand handlePersonalData(Boolean handlePersonalData);

    EServiceTemplateCreationCommand isAsync(Boolean isAsync);

    EServiceTemplateCreationCommand versionDescription(String description);

    EServiceTemplateCreationCommand voucherLifespan(Integer voucherLifespan);

    EServiceTemplateCreationCommand dailyCallsPerConsumer(Integer dailyCallsPerConsumer);

    EServiceTemplateCreationCommand dailyCallsTotal(Integer dailyCallsTotal);

    EServiceTemplateCreationCommand agreementApprovalPolicy(AgreementApprovalPolicy agreementApprovalPolicy);
}

