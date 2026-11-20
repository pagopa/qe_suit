package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.risk_analysis.application.RiskAnalysisDataFactory;
import it.pagopa.interop.common.risk_analysis.application.RiskAnalysisGateway;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisFormConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EServiceRiskAnalysisUseCase {

    private final RiskAnalysisGateway riskAnalysisGateway;
    private final EServiceRiskAnalysisGateway eServiceRiskAnalysisGateway;
    private final RiskAnalysisDataFactory riskAnalysisDataFactory;

    public void ensureForPublication(EService eService) {
        if (eService.getMode() != EServiceMode.RECEIVE) return;
        if (getRiskAnalysis(eService) != null) return;

        Tenant producer = Tenant.fromOrganizationId(java.util.UUID.fromString(eService.getProducerId()));
        addLatestRiskAnalysis(producer, eService, true);
    }

    public EServiceRiskAnalysis addLatestRiskAnalysis(Tenant tenant, EService eService, boolean completed) {
        RiskAnalysisForm form = buildRiskAnalysisForm(tenant, completed);
        return eServiceRiskAnalysisGateway.addRiskAnalysis(eService.getRef(), form);
    }

    public EServiceRiskAnalysis addLatestRiskAnalysis(
            Tenant tenant,
            EService eService,
            boolean completed,
            @jakarta.annotation.Nullable String name
    ) {
        RiskAnalysisForm form = buildRiskAnalysisForm(tenant, completed);
        return name == null || name.isBlank()
                ? eServiceRiskAnalysisGateway.addRiskAnalysis(eService.getRef(), form)
                : eServiceRiskAnalysisGateway.addRiskAnalysis(eService.getRef(), form, name);
    }

    private RiskAnalysisForm buildRiskAnalysisForm(Tenant tenant, boolean completed) {
        RiskAnalysisFormConfig latestConfig = riskAnalysisGateway.getLatestRiskAnalysisConfig(tenant);
        Map<String, List<String>> answers =
                riskAnalysisDataFactory.getTemplateForTenant(tenant, completed);

        return RiskAnalysisForm.builder()
                .version(latestConfig.getVersion())
                .answers(answers)
                .build();
    }

    public EServiceRiskAnalysis getRiskAnalysis(EService eService) {
        return eServiceRiskAnalysisGateway.getRiskAnalysis(eService.getRef());
    }

    public void attemptAddRiskAnalysisWithFreeTextAnswer(
            Tenant tenant,
            EService eService,
            String freeTextAnswer
    ) {
        RiskAnalysisFormConfig latestConfig = riskAnalysisGateway.getLatestRiskAnalysisConfig(tenant);
        Map<String, List<String>> answers = new java.util.LinkedHashMap<>(
                riskAnalysisDataFactory.getTemplateForTenant(tenant, true)
        );
        String freeTextKey = resolveFreeTextQuestionKey(latestConfig, answers);
        answers.put(freeTextKey, List.of(freeTextAnswer));

        RiskAnalysisForm form = RiskAnalysisForm.builder()
                .version(latestConfig.getVersion())
                .answers(answers)
                .build();

        eServiceRiskAnalysisGateway.attemptAddRiskAnalysis(eService.getRef(), form, null);
    }

    public void attemptRenameRiskAnalysis(
            Tenant tenant,
            EService eService,
            EServiceRiskAnalysis riskAnalysis,
            String newName
    ) {
        RiskAnalysisForm form = riskAnalysis.getRiskAnalysisForm() != null
                ? riskAnalysis.getRiskAnalysisForm()
                : buildRiskAnalysisForm(tenant, true);
        eServiceRiskAnalysisGateway.attemptUpdateRiskAnalysis(
                eService.getRef(),
                riskAnalysis.getRef(),
                form,
                newName
        );
    }

    private static String resolveFreeTextQuestionKey(
            RiskAnalysisFormConfig config,
            Map<String, List<String>> answers
    ) {
        if (config != null && config.getQuestions() != null) {
            for (RiskAnalysisFormConfig.Question question : config.getQuestions()) {
                if ("FREETEXT".equals(question.getDataType()) && answers.containsKey(question.getId())) {
                    return question.getId();
                }
            }
            for (RiskAnalysisFormConfig.Question question : config.getQuestions()) {
                if ("FREETEXT".equals(question.getDataType())) {
                    return question.getId();
                }
            }
        }
        return "institutionalPurpose";
    }
}
