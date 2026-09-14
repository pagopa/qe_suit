package it.pagopa.interop.common.purpose_template.application;

import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import org.springframework.plugin.core.Plugin;

public interface PurposeTemplateGateway extends Plugin<Channel> {

    PurposeTemplate createPurposeTemplate(Tenant creator);

    void assertGeneralInformationPageDisplayed(Tenant creator);
}

