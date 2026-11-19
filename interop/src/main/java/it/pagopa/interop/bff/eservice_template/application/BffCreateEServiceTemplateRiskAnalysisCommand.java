package it.pagopa.interop.bff.eservice_template.application;

import it.pagopa.interop.common.eservice_template.application.command.CreateEServiceTemplateRiskAnalysisCommand;
import it.pagopa.interop.common.kernel.domain.TenantKind;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateRiskAnalysisSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormSeed;
import lombok.Getter;

import java.util.HashMap;
import lombok.NonNull;

public class BffCreateEServiceTemplateRiskAnalysisCommand implements CreateEServiceTemplateRiskAnalysisCommand {

    @Getter
    private final EServiceTemplateRiskAnalysisSeed bffPayload;

    public BffCreateEServiceTemplateRiskAnalysisCommand() {
        this.bffPayload = new EServiceTemplateRiskAnalysisSeed();
    }

    public BffCreateEServiceTemplateRiskAnalysisCommand(EServiceTemplateRiskAnalysisSeed bffPayload) {
        this.bffPayload = bffPayload;
    }

    @Override
    public CreateEServiceTemplateRiskAnalysisCommand name(String name) {
        bffPayload.setName(name);
        return this;
    }

    @Override
    public CreateEServiceTemplateRiskAnalysisCommand tenantKind(@NonNull TenantKind tenantKind) {
        bffPayload.setTenantKind(
            it.pagopa.interop.generated.openapi.clients.bff.model.TenantKind.fromValue(tenantKind.name())
        );
        return this;
    }

    @Override
    public CreateEServiceTemplateRiskAnalysisCommand riskAnalysisForm(@NonNull RiskAnalysisForm riskAnalysisForm) {
        bffPayload.setRiskAnalysisForm(
                new RiskAnalysisFormSeed()
                        .version(riskAnalysisForm.getVersion())
                        .answers(new HashMap<>(riskAnalysisForm.getAnswers()))
        );
        return this;
    }

    @Override
    public String getName() {
        return bffPayload.getName();
    }

    @Override
    public TenantKind getTenantKind() {
        var kind = bffPayload.getTenantKind();
        return kind != null ? TenantKind.valueOf(kind.getValue()) : null;
    }

    @Override
    public RiskAnalysisForm getRiskAnalysisForm() {
        var form = bffPayload.getRiskAnalysisForm();
        if (form == null) {
            return null;
        }
        return RiskAnalysisForm.builder()
                .version(form.getVersion())
                .answers(form.getAnswers())
                .build();
    }
}
