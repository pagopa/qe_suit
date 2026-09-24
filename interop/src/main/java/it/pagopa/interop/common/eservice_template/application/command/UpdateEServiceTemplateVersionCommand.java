package it.pagopa.interop.common.eservice_template.application.command;

import it.pagopa.interop.common.attribute.domain.Attribute;

public interface UpdateEServiceTemplateVersionCommand {
    UpdateEServiceTemplateVersionCommand voucherLifespan(Integer voucherLifespan);
    UpdateEServiceTemplateVersionCommand dailyCallsPerConsumer(Integer dailyCallsPerConsumer);
    UpdateEServiceTemplateVersionCommand dailyCallsTotal(Integer dailyCallsTotal);
    UpdateEServiceTemplateVersionCommand description(String description);
    UpdateEServiceTemplateVersionCommand declaredAttribute(Attribute attribute);
}

