package it.pagopa.interop.m2m.eservice_template.infrastructure;

import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateRequestFactory;
import it.pagopa.interop.common.eservice_template.application.command.*;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.TenantKind;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;
import it.pagopa.interop.m2m.eservice_template.application.M2MUpdateEServiceTemplateCommand;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import static it.pagopa.utils.RandomUtils.randomAlphanumericName;

@Component
public class M2MEServiceTemplateRequestFactory implements EServiceTemplateRequestFactory {

    @Override
    public EServiceTemplateCreationCommand defaultCreationEServiceTemplateCommand() {
        throw unsupportedOperation();
    }

    @Override
    public EServiceTemplateInstanceCreationCommand defaultCreationEServiceTemplateInstanceCommand() {
        throw unsupportedOperation();
    }

    @Override
    public UpdateEServiceTemplateCommand defaultUpdateEServiceTemplateCommand(Boolean empty) {
        if (Boolean.TRUE.equals(empty)) {
            return new M2MUpdateEServiceTemplateCommand();
        }

        return new M2MUpdateEServiceTemplateCommand()
                .name(randomAlphanumericName("eservice-template", 15))
                .intendedTarget(randomAlphanumericName("target", 30))
                .description(randomAlphanumericName("description", 50))
                .technology(EServiceTechnology.REST)
                .mode(EServiceMode.DELIVER)
                .isSignalHubEnabled(false)
                .handlePersonalData(false);
    }

    @Override
    public UpdateEServiceTemplateNameCommand defaultUpdateTemplateNameCommand() {
        throw unsupportedOperation();
    }

    @Override
    public UpdateEServiceTemplateIntendedTargetCommand defaultUpdateTemplateIntendedTargetCommand() {
        throw unsupportedOperation();
    }

    @Override
    public UpdateEServiceTemplateDescriptionCommand defaultUpdateTemplateDescriptionCommand() {
        throw unsupportedOperation();
    }

    @Override
    public UpdateEServiceTemplateVersionCommand defaultUpdateTemplateVersionCommand() {
        throw unsupportedOperation();
    }

    @Override
    public CreateEServiceTemplateRiskAnalysisCommand createRiskAnalysisCommand(
        TenantKind tenantKind, RiskAnalysisForm riskAnalysisForm) {
        throw unsupportedOperation();
    }

    @Override
    public boolean supports(@NonNull Channel channel) {
        return channel == Channel.M2M_V3;
    }

    private static UnsupportedOperationException unsupportedOperation() {
        return new UnsupportedOperationException("This e-service template operation is not supported on M2M v3");
    }
}
