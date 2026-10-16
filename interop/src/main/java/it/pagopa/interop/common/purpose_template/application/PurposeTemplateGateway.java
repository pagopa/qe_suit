package it.pagopa.interop.common.purpose_template.application;

import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;
import org.springframework.plugin.core.Plugin;

public interface PurposeTemplateGateway extends Plugin<Channel> {

    PurposeTemplate createPurposeTemplate(Tenant targetTenant, RiskAnalysisForm riskAnalysisForm);

    PurposeTemplate publishPurposeTemplate(PurposeTemplate purposeTemplate);

    PurposeTemplate linkEService(PurposeTemplate purposeTemplate, EServiceRef eServiceRef);

}

