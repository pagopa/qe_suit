package it.pagopa.interop.bff.purpose.application;

import it.pagopa.interop.common.purpose.application.SubmitRiskAnalysisCommand;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisSubmissionSeed;
import lombok.Getter;

import java.util.HashMap;

public class BffSubmitRiskAnalysisCommand implements SubmitRiskAnalysisCommand {

    @Getter
    private final RiskAnalysisSubmissionSeed bffSubmissionPayload;

    public BffSubmitRiskAnalysisCommand() {
        this.bffSubmissionPayload = new RiskAnalysisSubmissionSeed();
    }

    public BffSubmitRiskAnalysisCommand(RiskAnalysisSubmissionSeed bffSubmissionPayload) {
        this.bffSubmissionPayload = bffSubmissionPayload;
    }

    @Override
    public SubmitRiskAnalysisCommand riskAnalysisForm(RiskAnalysisForm riskAnalysisForm) {
        if (riskAnalysisForm == null) {
            bffSubmissionPayload.setRiskAnalysisForm(null);
        } else {
            bffSubmissionPayload.setRiskAnalysisForm(
                    new RiskAnalysisFormSeed()
                            .version(riskAnalysisForm.getVersion())
                            .answers(riskAnalysisForm.getAnswers() != null ? new HashMap<>(riskAnalysisForm.getAnswers()) : null)
            );
        }
        return this;
    }

    @Override
    public RiskAnalysisForm getRiskAnalysisForm() {
        var form = bffSubmissionPayload.getRiskAnalysisForm();
        if (form == null) {
            return null;
        }
        return RiskAnalysisForm.builder()
                .version(form.getVersion())
                .answers(form.getAnswers())
                .build();
    }
}
