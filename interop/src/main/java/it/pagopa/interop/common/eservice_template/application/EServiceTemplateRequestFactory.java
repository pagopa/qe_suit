package it.pagopa.interop.common.eservice_template.application;

import it.pagopa.interop.common.eservice_template.application.command.*;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.TenantKind;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;
import org.springframework.plugin.core.Plugin;

public interface EServiceTemplateRequestFactory extends Plugin<Channel> {
    EServiceTemplateCreationCommand defaultCreationEServiceTemplateCommand();

    EServiceTemplateInstanceCreationCommand defaultCreationEServiceTemplateInstanceCommand();

    UpdateEServiceTemplateCommand defaultUpdateEServiceTemplateCommand(Boolean empty);

    UpdateEServiceTemplateNameCommand defaultUpdateTemplateNameCommand();

    UpdateEServiceTemplateIntendedTargetCommand defaultUpdateTemplateIntendedTargetCommand();

    UpdateEServiceTemplateDescriptionCommand defaultUpdateTemplateDescriptionCommand();

    UpdateEServiceTemplateVersionCommand defaultUpdateTemplateVersionCommand();

    CreateEServiceTemplateRiskAnalysisCommand createRiskAnalysisCommand(
            TenantKind tenantKind,
            RiskAnalysisForm riskAnalysisForm
    );
}
