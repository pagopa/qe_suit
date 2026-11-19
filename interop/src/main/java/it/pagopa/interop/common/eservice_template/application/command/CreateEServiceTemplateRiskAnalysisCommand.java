package it.pagopa.interop.common.eservice_template.application.command;

import it.pagopa.interop.common.kernel.domain.TenantKind;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;

public interface CreateEServiceTemplateRiskAnalysisCommand {
    CreateEServiceTemplateRiskAnalysisCommand name(String name);

    CreateEServiceTemplateRiskAnalysisCommand tenantKind(TenantKind tenantKind);

    CreateEServiceTemplateRiskAnalysisCommand riskAnalysisForm(RiskAnalysisForm riskAnalysisForm);

    String getName();

    TenantKind getTenantKind();

    RiskAnalysisForm getRiskAnalysisForm();
}
