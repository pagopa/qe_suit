package it.pagopa.interop.common.eservice_template.application.command;

import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.interop.common.kernel.domain.Tenant;

public record PrepareEServiceTemplateForPublishingCommand(
        EServiceTemplate eServiceTemplate,
        EServiceTemplateVersion version,
        String interfacePath,
        Tenant tenant,
        EServiceRiskAnalysis eServiceRiskAnalysis
) {
    public PrepareEServiceTemplateForPublishingCommand(
            EServiceTemplate eServiceTemplate,
            EServiceTemplateVersion version
    ) {
        this(eServiceTemplate, version, null, null, null);
    }

    public PrepareEServiceTemplateForPublishingCommand(
            EServiceTemplate eServiceTemplate,
            EServiceTemplateVersion version,
            String interfacePath
    ) {
        this(eServiceTemplate, version, interfacePath, null, null);
    }
}
