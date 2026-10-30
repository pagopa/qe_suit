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
import java.util.Set;
import java.util.stream.Collectors;

import static it.pagopa.utils.RandomUtils.randomAlphanumericName;

@Component
@RequiredArgsConstructor
public class BffPurposeTemplateRequestFactory {

    private static final String DEFAULT_ANSWER_KEY = "institutionalPurpose";
    private static final String FREETEXT_DATA_TYPE = "FREETEXT";

    private final RiskAnalysisGateway riskAnalysisGateway;
    private final RiskAnalysisDataFactory riskAnalysisDataFactory;

    public PurposeTemplateSeed creationRequest(Tenant targetTenant) {
        RiskAnalysisFormConfig latestConfig = riskAnalysisGateway.getLatestRiskAnalysisConfig(targetTenant);
        RiskAnalysisForm form = RiskAnalysisForm.builder()
                .version(latestConfig.getVersion())
                .answers(riskAnalysisDataFactory.getTemplateForTenant(targetTenant, true))
                .build();

        return buildCreationRequest(targetTenant, form, latestConfig);
    }

    public PurposeTemplateSeed creationRequest(Tenant targetTenant, RiskAnalysisForm riskAnalysisForm) {
        RiskAnalysisFormConfig latestConfig = riskAnalysisGateway.getLatestRiskAnalysisConfig(targetTenant);
        return buildCreationRequest(targetTenant, riskAnalysisForm, latestConfig);
    }

    private PurposeTemplateSeed buildCreationRequest(Tenant targetTenant,
                                                     RiskAnalysisForm riskAnalysisForm,
                                                     RiskAnalysisFormConfig config) {
        Set<String> freeTextQuestions = freeTextQuestionIds(config);
        Map<String, RiskAnalysisTemplateAnswerSeed> answers = new LinkedHashMap<>();
        riskAnalysisForm.getAnswers().forEach((question, values) ->
                answers.put(question, toAnswerSeed(List.copyOf(values), freeTextQuestions.contains(question))));

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
                // institutionalPurpose è FREETEXT: il testo va in suggestedValues, values vuoto
                .answerData(toAnswerSeed(List.of(randomAlphanumericName("answer-value", 15)), true)
                        .annotation(addRiskAnalysisAnswerAnnotationRequest()));
    }

    /**
     * Per le domande FREETEXT il backend accetta solo values=[] con il testo in suggestedValues
     * (stessa configurazione prodotta dalla web app); per SINGLE/MULTI il valore va in values.
     */
    private static RiskAnalysisTemplateAnswerSeed toAnswerSeed(List<String> values, boolean freeText) {
        return new RiskAnalysisTemplateAnswerSeed()
                .values(freeText ? List.of() : values)
                .editable(false)
                .suggestedValues(freeText ? values : List.of());
    }

    private static Set<String> freeTextQuestionIds(RiskAnalysisFormConfig config) {
        if (config == null || config.getQuestions() == null) {
            return Set.of();
        }
        return config.getQuestions().stream()
                .filter(q -> FREETEXT_DATA_TYPE.equals(q.getDataType()))
                .map(RiskAnalysisFormConfig.Question::getId)
                .collect(Collectors.toUnmodifiableSet());
    }

    public RiskAnalysisTemplateAnswerAnnotationSeed addRiskAnalysisAnswerAnnotationRequest() {
        return new RiskAnalysisTemplateAnswerAnnotationSeed()
                .text(randomAlphanumericName("annotation", 20));
    }
}

