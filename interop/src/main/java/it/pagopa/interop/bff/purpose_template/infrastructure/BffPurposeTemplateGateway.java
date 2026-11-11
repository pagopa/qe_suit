package it.pagopa.interop.bff.purpose_template.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.template.action.PollingAction;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.purpose_template.application.PurposeTemplateGateway;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplateState;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisFormConfig;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.utils.RandomUtils;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BffPurposeTemplateGateway implements PurposeTemplateGateway {

    private static final int DEFAULT_LINKABLE_RESOURCES_OFFSET = 0;
    private static final int DEFAULT_LINKABLE_RESOURCES_LIMIT = 50;

    private final BffPurposeTemplateRestClient restClient;
    private final BffPurposeTemplateMapper mapper;
    private final BffPurposeTemplateRequestFactory requestFactory;
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
        PurposeTemplateSeed payload = requestFactory.creationRequest(targetTenant, riskAnalysisForm);

        return restClient.createPurposeTemplate(payload)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(created -> getPurposeTemplate(created.getId()))
                .updateContext()
                .get();
    }

    @Override
    public PurposeTemplate createPurposeTemplate(
            Tenant targetTenant, RiskAnalysisForm riskAnalysisForm, RiskAnalysisFormConfig config
    ) {
        if (!Objects.equals(config.getVersion(), riskAnalysisForm.getVersion())) {
            throw new IllegalArgumentException("Risk analysis form and configuration versions must match");
        }

        var questions = config.getQuestions().stream().collect(Collectors.toMap(
                RiskAnalysisFormConfig.Question::getId, Function.identity()));
        Map<String, RiskAnalysisTemplateAnswerSeed> answers = new HashMap<>();
        riskAnalysisForm.getAnswers().forEach((question, values) -> {
            var definition = questions.get(question);
            if (definition == null) {
                throw new IllegalArgumentException("Unknown risk analysis question: " + question);
            }
            boolean freeText = "FREETEXT".equals(definition.getDataType());
            answers.put(question, new RiskAnalysisTemplateAnswerSeed()
                    .values(freeText ? List.of() : List.copyOf(values))
                    .editable(false)
                    .suggestedValues(freeText ? List.copyOf(values) : List.of()));
        });

        String title = RandomUtils.randomAlphanumericName("purpose-template");

        PurposeTemplateSeed payload = new PurposeTemplateSeed()
                .targetDescription(RandomUtils.randomAlphanumericName("target-description"))
                .targetTenantKind(switch (targetTenant.getTenantType()) {
                    case PA -> TargetTenantKind.PA;
                    case PRIVATE, GSP -> TargetTenantKind.PRIVATE;
                })
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
    public PurposeTemplate suspendPurposeTemplate(PurposeTemplate purposeTemplate) {
        restClient.suspendPurposeTemplate(purposeTemplate.getId())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();

        return PollingUtils.pollUntil(
                () -> getPurposeTemplate(purposeTemplate.getId()),
                pt -> pt.getState() == PurposeTemplateState.SUSPENDED
        );
    }

    @Override
    public PurposeTemplate linkEService(PurposeTemplate purposeTemplate, EServiceRef eServiceRef) {
        return linkResource(purposeTemplate, linkEServicePayload(eServiceRef), eServiceRef.id(), false);
    }

    @Override
    public PurposeTemplate linkEServiceTemplate(PurposeTemplate purposeTemplate, EServiceTemplateRef templateRef) {
        return linkResource(purposeTemplate, linkEServiceTemplatePayload(templateRef), templateRef.id(), true);
    }

    private PurposeTemplate linkResource(
            PurposeTemplate purposeTemplate, LinkableResourceRequest payload, UUID resourceId, boolean template) {
        restClient.linkResource(purposeTemplate.getId(), payload)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .assertThat(linked -> resourceId.equals(template ? linked.getEserviceTemplateId() : linked.getEserviceId()))
                .get();

        return awaitLinkedResource(purposeTemplate, resourceId);
    }

    @Override
    public PurposeTemplate tryLinkEService(PurposeTemplate purposeTemplate, EServiceRef eServiceRef) {
        return tryLinkResource(purposeTemplate, linkEServicePayload(eServiceRef), eServiceRef.id());
    }

    @Override
    public PurposeTemplate tryLinkEServiceTemplate(PurposeTemplate purposeTemplate, EServiceTemplateRef eServiceTemplateRef) {
        return tryLinkResource(purposeTemplate, linkEServiceTemplatePayload(eServiceTemplateRef), eServiceTemplateRef.id());
    }

    private @Nullable PurposeTemplate tryLinkResource(
            PurposeTemplate purposeTemplate,
            LinkableResourceRequest payload,
            UUID resourceId
    ) {
        PollingAction<LinkedResource> response = restClient.linkResource(purposeTemplate.getId(), payload)
                .withoutPolling();

        if (!response.getRaw().isSuccess()) {
            return null;
        }

        return response
                .map(linked -> awaitLinkedResource(purposeTemplate, resourceId))
                .updateContext()
                .get();
    }

    private PurposeTemplate awaitLinkedResource(PurposeTemplate purposeTemplate, UUID resourceId) {
        return PollingUtils.pollUntil(
                () -> fetchAllLinkedResources(purposeTemplate),
                pt -> pt.getLinkedResourceIds().contains(resourceId)
        );
    }

    private static LinkableResourceRequest linkEServicePayload(EServiceRef eServiceRef) {
        return new LinkableResourceRequest()
                .resourceKind(LinkableResourceRequest.ResourceKindEnum.ESERVICE)
                .eserviceId(eServiceRef.id());
    }

    private static LinkableResourceRequest linkEServiceTemplatePayload(EServiceTemplateRef templateRef) {
        return new LinkableResourceRequest()
                .resourceKind(LinkableResourceRequest.ResourceKindEnum.ESERVICE_TEMPLATE)
                .eserviceTemplateId(templateRef.id());
    }

    private PurposeTemplate fetchAllLinkedResources(PurposeTemplate fallbackTemplate) {
        UUID purposeTemplateId = fallbackTemplate.getId();
        PurposeTemplate current = entityStore.getById(purposeTemplateId, PurposeTemplate.class)
                .orElse(fallbackTemplate);

        int offset = DEFAULT_LINKABLE_RESOURCES_OFFSET;
        while (true) {
            LinkableResources page = restClient.getLinkableResources(
                            purposeTemplateId,
                            offset,
                            DEFAULT_LINKABLE_RESOURCES_LIMIT
                    )
                    .withPolling(PollingStrategy.UNTIL_SUCCESS)
                    .get();

            if (page == null) {
                entityStore.upsert(current);
                return current;
            }

            current = mapper.toPurposeTemplateWithLinkedResources(current, page);

            List<LinkableResource> results = page.getResults();
            int totalCount = page.getPagination().getTotalCount();
            offset = Math.addExact(offset, results.size());

            if (results.isEmpty() || offset >= totalCount) {
                entityStore.upsert(current);
                return current;
            }
        }
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}
