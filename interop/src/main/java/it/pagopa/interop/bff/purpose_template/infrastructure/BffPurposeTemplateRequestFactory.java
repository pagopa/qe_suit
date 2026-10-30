package it.pagopa.interop.bff.purpose_template.infrastructure;

import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.risk_analysis.application.RiskAnalysisDataFactory;
import it.pagopa.interop.common.risk_analysis.application.RiskAnalysisGateway;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisFormConfig;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisTemplateAnswerAnnotationSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisTemplateAnswerRequest;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisTemplateAnswerSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.TargetTenantKind;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static it.pagopa.utils.RandomUtils.randomAlphanumericName;

@Component
@RequiredArgsConstructor
public class BffPurposeTemplateRequestFactory {

    private static final String DEFAULT_ANSWER_KEY = "institutionalPurpose";

    private final RiskAnalysisGateway riskAnalysisGateway;
    private final RiskAnalysisDataFactory riskAnalysisDataFactory;

    public PurposeTemplateSeed creationRequest(Tenant targetTenant) {
        RiskAnalysisFormConfig latestConfig = riskAnalysisGateway.getLatestRiskAnalysisConfig(targetTenant);
        RiskAnalysisForm form = RiskAnalysisForm.builder()
                .version(latestConfig.getVersion())
                .answers(riskAnalysisDataFactory.getTemplateForTenant(targetTenant, true))
                .build();

        return creationRequest(targetTenant, form);
    }

    public PurposeTemplateSeed creationRequest(Tenant targetTenant, RiskAnalysisForm riskAnalysisForm) {
        Map<String, RiskAnalysisTemplateAnswerSeed> answers = new LinkedHashMap<>();
        riskAnalysisForm.getAnswers().forEach((question, values) ->
                answers.put(question, new RiskAnalysisTemplateAnswerSeed()
                        .values(List.copyOf(values))
                        .editable(false)
                        .suggestedValues(List.of())
                        .annotation(addRiskAnalysisAnswerAnnotationRequest())));

        return new PurposeTemplateSeed()
                .targetDescription(randomAlphanumericName("target-description", 20))
                .targetTenantKind(TargetTenantKind.fromValue(targetTenant.getTenantType().name()))
                .purposeTitle(randomAlphanumericName("purpose-template", 15))
                .purposeDescription(randomAlphanumericName("purpose-description", 20))
                .purposeIsFreeOfCharge(true)
                .purposeFreeOfChargeReason(randomAlphanumericName("free-of-charge-reason", 20))
                .purposeDailyCalls(1)
                .handlesPersonalData(false)
                .purposeRiskAnalysisForm(new RiskAnalysisFormTemplateSeed()
                        .version(riskAnalysisForm.getVersion())
                        .answers(answers));
    }

    public PurposeTemplateSeed updateRequest(Tenant targetTenant) {
        return creationRequest(targetTenant);
    }

    public RiskAnalysisTemplateAnswerRequest addRiskAnalysisAnswerRequest() {
        return new RiskAnalysisTemplateAnswerRequest()
                .answerKey(DEFAULT_ANSWER_KEY)
                .answerData(new RiskAnalysisTemplateAnswerSeed()
                        .values(List.of(randomAlphanumericName("answer-value", 15)))
                        .editable(false)
                        .suggestedValues(List.of())
                        .annotation(addRiskAnalysisAnswerAnnotationRequest()));
    }

    public RiskAnalysisTemplateAnswerAnnotationSeed addRiskAnalysisAnswerAnnotationRequest() {
        return new RiskAnalysisTemplateAnswerAnnotationSeed()
                .text(randomAlphanumericName("annotation", 20));
    }
}

