package it.pagopa.interop.common.eservice_template.application;

import it.pagopa.interop.common.eservice_template.application.command.*;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import it.pagopa.interop.common.kernel.domain.TenantKind;
import org.springframework.plugin.core.Plugin;

public interface EServiceTemplateGateway extends Plugin<Channel> {
    EServiceTemplate createEServiceTemplate(EServiceTemplateCreationCommand command);

    EServiceTemplate getEServiceTemplate(EServiceTemplateRef templateRef);

    EServiceTemplate updateEServiceTemplate(EServiceTemplateRef templateRef, UpdateEServiceTemplateCommand command);

    EServiceTemplate updateTemplateName(EServiceTemplateRef templateRef, UpdateEServiceTemplateNameCommand command);

    EServiceTemplate updateTemplateIntendedTarget(EServiceTemplateRef templateRef, UpdateEServiceTemplateIntendedTargetCommand command);

    EServiceTemplate updateTemplateDescription(EServiceTemplateRef templateRef, UpdateEServiceTemplateDescriptionCommand command);

    EServiceTemplate createEServiceTemplateRiskAnalysis(EServiceTemplateRef templateRef, TenantKind targetKind, EServiceRiskAnalysis riskAnalysisForm);
}
