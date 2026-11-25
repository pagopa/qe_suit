package it.pagopa.interop.m2m.purpose.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.PurposeTemplateRef;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisAnswerTemplate;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisFormTemplate;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.PurposeFromTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.RiskAnalysisFormSeed;
import it.pagopa.utils.RandomUtils;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class M2MPurposeRequestFactory {

    private final EntityStore entityStore;

    public PurposeFromTemplateSeed creationFromTemplateRequest(
            EServiceRef eServiceRef,
            PurposeTemplateRef purposeTemplateRef
    ) {
        PurposeTemplate purposeTemplate = entityStore.getById(purposeTemplateRef.id(), PurposeTemplate.class)
                .orElseThrow(() -> new IllegalStateException(
                        "Selected purpose template not found in entity store: " + purposeTemplateRef.id()));

        return new PurposeFromTemplateSeed()
                .title(RandomUtils.randomAlphanumericName("purpose"))
                .dailyCalls(1)
                .eserviceId(eServiceRef.id())
                .riskAnalysisForm(riskAnalysisFormTemplateSeed(purposeTemplate.getRiskAnalysisFormTemplate()));
    }

    private RiskAnalysisFormSeed riskAnalysisFormTemplateSeed(@Nullable RiskAnalysisFormTemplate templateForm) {
        if (templateForm == null || templateForm.getVersion() == null || templateForm.getVersion().isBlank()
                || templateForm.getAnswerTemplates() == null) {
            throw new IllegalArgumentException("Template risk analysis form must have a version and answers");
        }

        Map<String, List<String>> answers = new HashMap<>();
        templateForm.getAnswerTemplates().forEach((question, answer) -> {
            if (answer == null) {
                throw new IllegalArgumentException("Missing template risk analysis answer data for: " + question);
            }
            if (answer.getEditable() == null) {
                throw new IllegalArgumentException("Missing template risk analysis editability for: " + question);
            }
            if (answer.getSuggestedValues() != null && !answer.getSuggestedValues().isEmpty()) {
                answers.put(question, List.of(answer.getSuggestedValues().get(0)));
            } else if (answer.getEditable()) {
                answers.put(question, List.of(RandomUtils.randomAlphanumericName("random-text")));
            }
        });

        return new RiskAnalysisFormSeed().version(templateForm.getVersion()).answers(answers);
    }
}
