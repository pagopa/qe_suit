package it.pagopa.interop.bff.purpose_template.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.purpose_template.application.PurposeTemplateGateway;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplateState;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;
import it.pagopa.interop.generated.openapi.clients.bff.model.LinkableResourceRequest;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisTemplateAnswerSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.TargetTenantKind;
import it.pagopa.utils.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BffPurposeTemplateGateway implements PurposeTemplateGateway {

    private final BffPurposeTemplateRestClient restClient;
    private final CurrentUserSession currentUserSession;
    private final EntityStore entityStore;

    @Override
    public PurposeTemplate createPurposeTemplate(Tenant targetTenant, RiskAnalysisForm riskAnalysisForm) {
        Map<String, RiskAnalysisTemplateAnswerSeed> answers = new HashMap<>();
        riskAnalysisForm.getAnswers().forEach((question, values) ->
                answers.put(question, new RiskAnalysisTemplateAnswerSeed()
                        .values(List.copyOf(values))
                        .editable(false)
                        .suggestedValues(List.of())));

        String title = RandomUtils.randomAlphanumericName("purpose-template");

        PurposeTemplateSeed payload = new PurposeTemplateSeed()
                .targetDescription(RandomUtils.randomAlphanumericName("target-description"))
                .targetTenantKind(TargetTenantKind.fromValue(targetTenant.getTenantType().name()))
                .purposeTitle(title)
                .purposeDescription(RandomUtils.randomAlphanumericName("purpose-description"))
                .purposeIsFreeOfCharge(false)
                .purposeDailyCalls(1)
                .handlesPersonalData(false)
                .purposeRiskAnalysisForm(new RiskAnalysisFormTemplateSeed()
                        .version(riskAnalysisForm.getVersion())
                        .answers(answers));

        return restClient.createPurposeTemplate(payload)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(created -> PurposeTemplate.builder()
                        .id(created.getId())
                        .creatorId(currentUserSession.getTenant().getOrganizationId())
                        .title(title)
                        .state(PurposeTemplateState.DRAFT)
                        .build())
                .updateContext()
                .get();
    }

    @Override
    public PurposeTemplate publishPurposeTemplate(PurposeTemplate purposeTemplate) {
        restClient.publishPurposeTemplate(purposeTemplate.getId())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();

        return store(purposeTemplate.toBuilder().state(PurposeTemplateState.PUBLISHED).build());
    }

    @Override
    public PurposeTemplate linkEService(PurposeTemplate purposeTemplate, EServiceRef eServiceRef) {
        LinkableResourceRequest payload = new LinkableResourceRequest()
                .resourceKind(LinkableResourceRequest.ResourceKindEnum.ESERVICE)
                .eserviceId(eServiceRef.id());

        restClient.linkResource(purposeTemplate.getId(), payload)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();

        return store(purposeTemplate.toBuilder().eserviceId(eServiceRef.id()).build());
    }

    private PurposeTemplate store(PurposeTemplate purposeTemplate) {
        entityStore.upsert(purposeTemplate);
        return purposeTemplate;
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}

