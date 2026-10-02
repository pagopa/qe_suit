package it.pagopa.interop.bff.eservice_template.application;

import it.pagopa.interop.common.agreement.domain.AgreementApprovalPolicy;
import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateVersionCommand;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateVersionSeed;
import lombok.Getter;

@Getter
public class BffUpdateEServiceTemplateVersionCommand implements UpdateEServiceTemplateVersionCommand {
    private final UpdateEServiceTemplateVersionSeed bffPayload;

    public static BffUpdateEServiceTemplateVersionCommand from(UpdateEServiceTemplateVersionSeed payload) {
        return new BffUpdateEServiceTemplateVersionCommand(payload);
    }

    private BffUpdateEServiceTemplateVersionCommand(UpdateEServiceTemplateVersionSeed payload) {
        this.bffPayload = payload;
    }

    public BffUpdateEServiceTemplateVersionCommand() {
        this.bffPayload = new UpdateEServiceTemplateVersionSeed();
    }

    @Override
    public UpdateEServiceTemplateVersionCommand description(String description) {
        bffPayload.setDescription(description);
        return this;
    }

    @Override
    public UpdateEServiceTemplateVersionCommand voucherLifespan(Integer voucherLifespan) {
        bffPayload.setVoucherLifespan(voucherLifespan);
        return this;
    }

    @Override
    public UpdateEServiceTemplateVersionCommand dailyCallsPerConsumer(Integer dailyCallsPerConsumer) {
        bffPayload.setDailyCallsPerConsumer(dailyCallsPerConsumer);
        return this;
    }

    @Override
    public UpdateEServiceTemplateVersionCommand dailyCallsTotal(Integer dailyCallsTotal) {
        bffPayload.setDailyCallsTotal(dailyCallsTotal);
        return this;
    }

    @Override
    public UpdateEServiceTemplateVersionCommand agreementApprovalPolicy(AgreementApprovalPolicy agreementApprovalPolicy) {
        bffPayload.setAgreementApprovalPolicy(
                it.pagopa.interop.generated.openapi.clients.bff.model.AgreementApprovalPolicy.fromValue(
                        agreementApprovalPolicy.name()
                )
        );
        return this;
    }
}

