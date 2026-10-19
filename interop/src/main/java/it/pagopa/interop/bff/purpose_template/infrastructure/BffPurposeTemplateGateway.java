package it.pagopa.interop.bff.purpose_template.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
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
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BffPurposeTemplateGateway implements PurposeTemplateGateway {

    private static final int DEFAULT_LINKABLE_RESOURCES_OFFSET = 0;
    private static final int DEFAULT_LINKABLE_RESOURCES_LIMIT = 50;

    private final BffPurposeTemplateRestClient restClient;
    private final BffPurposeTemplateMapper mapper;
    private final EntityStore entityStore;

    @Override
    public PurposeTemplate getPurposeTemplate(UUID purposeTemplateId) {
        return restClient.getPurposeTemplate(purposeTemplateId)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(details -> {
                    Optional<PurposeTemplate> existing = entityStore.getById(purposeTemplateId, PurposeTemplate.class);
                    return mapper.toPurposeTemplate(details, existing.orElse(null));
                })
                .updateContext()
                .get();
    }

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
                .map(created -> getPurposeTemplate(created.getId()))
                .updateContext()
                .get();
    }

    @Override
    public PurposeTemplate publishPurposeTemplate(PurposeTemplate purposeTemplate) {
        restClient.publishPurposeTemplate(purposeTemplate.getId())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();

        return PollingUtils.pollUntil(
                () -> getPurposeTemplate(purposeTemplate.getId()),
                pt -> pt.getState() == PurposeTemplateState.PUBLISHED
        );
    }

    @Override
    public PurposeTemplate linkEService(PurposeTemplate purposeTemplate, EServiceRef eServiceRef) {
        LinkableResourceRequest payload = new LinkableResourceRequest()
                .resourceKind(LinkableResourceRequest.ResourceKindEnum.ESERVICE)
                .eserviceId(eServiceRef.id());

        restClient.linkResource(purposeTemplate.getId(), payload)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .assertThat(linked -> eServiceRef.id().equals(linked.getEserviceId()))
                .get();

        return PollingUtils.pollUntil(
                () -> restClient.getLinkableResources(
                                purposeTemplate.getId(),
                                DEFAULT_LINKABLE_RESOURCES_OFFSET,
                                DEFAULT_LINKABLE_RESOURCES_LIMIT
                        )
                        .withPolling(PollingStrategy.UNTIL_SUCCESS)
                        .map(resources -> {
                            PurposeTemplate current = entityStore.getById(purposeTemplate.getId(), PurposeTemplate.class)
                                    .orElse(purposeTemplate);
                            return mapper.toPurposeTemplateWithLinkedResources(current, resources);
                        })
                        .updateContext()
                        .get(),
                pt -> pt.getLinkedEServiceIds().contains(eServiceRef.id())
        );
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}

