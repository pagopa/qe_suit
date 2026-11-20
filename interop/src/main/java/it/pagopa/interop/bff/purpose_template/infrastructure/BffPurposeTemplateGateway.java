package it.pagopa.interop.bff.purpose_template.infrastructure;

import it.pagopa.application.context.EntityStore;
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
import it.pagopa.utils.FileUtils;
import it.pagopa.utils.RandomUtils;
import it.pagopa.utils.async.PollingUtils;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
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
        LinkableResourceRequest payload = new LinkableResourceRequest()
                .resourceKind(LinkableResourceRequest.ResourceKindEnum.ESERVICE)
                .eserviceId(eServiceRef.id());
        return linkResource(purposeTemplate, payload, eServiceRef.id(), false);
    }

    @Override
    public PurposeTemplate linkEServiceTemplate(PurposeTemplate purposeTemplate, EServiceTemplateRef templateRef) {
        var payload = new LinkableResourceRequest()
                .resourceKind(LinkableResourceRequest.ResourceKindEnum.ESERVICE_TEMPLATE)
                .eserviceTemplateId(templateRef.id());
        return linkResource(purposeTemplate, payload, templateRef.id(), true);
    }

    @Override
    public PurposeTemplate addRiskAnalysisAnswerAnnotation(PurposeTemplate purposeTemplate, UUID answerId) {
        RiskAnalysisTemplateAnswerAnnotationSeed payload = requestFactory.addRiskAnalysisAnswerAnnotationRequest();
        restClient.addRiskAnalysisAnswerAnnotation(purposeTemplate.getId(), answerId, payload)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();

        return getPurposeTemplate(purposeTemplate.getId());
    }

    @Override
    public PurposeTemplate addRiskAnalysisAnswerAnnotationDocument(
            PurposeTemplate purposeTemplate,
            UUID answerId,
            String prettyName,
            String fixturePath
    ) {
        File documentFile = resolveFixtureFile(fixturePath);
        var created = restClient.addRiskAnalysisAnswerAnnotationDocument(
                        purposeTemplate.getId(),
                        answerId,
                        prettyName,
                        documentFile
                )
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();

        return PollingUtils.pollUntil(
                () -> getPurposeTemplate(purposeTemplate.getId()),
                pt -> pt.getAnnotationDocuments() != null
                        && pt.getAnnotationDocuments().stream()
                        .anyMatch(doc -> created.getId().equals(doc.getId()) || prettyName.equals(doc.getPrettyName()))
        );
    }

    @Override
    public void awaitSignedRiskAnalysisDocumentAvailable(PurposeTemplate purposeTemplate) {
        restClient.getRiskAnalysisTemplateSignedDocument(purposeTemplate.getId())
                .withPolling(
                    PollingStrategy.UNTIL_SUCCESS,
                    Duration.ofSeconds(90),
                    Duration.ofSeconds(5))
                .get();
    }

    private File resolveFixtureFile(String fixturePath) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = BffPurposeTemplateGateway.class.getClassLoader();
        }
        for (String candidate : List.of(fixturePath, "assets/" + fixturePath, "assets/purpose-template/" + fixturePath)) {
            String normalized = candidate.startsWith("/") ? candidate.substring(1) : candidate;
            if (classLoader.getResource(normalized) != null) {
                return FileUtils.loadClasspathResourceAsTempFile(normalized);
            }
        }
        throw new IllegalStateException("Risorsa non trovata nel classpath: " + fixturePath);
    }

    private PurposeTemplate linkResource(
            PurposeTemplate purposeTemplate, LinkableResourceRequest payload, UUID resourceId, boolean template) {
        restClient.linkResource(purposeTemplate.getId(), payload)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .assertThat(linked -> resourceId.equals(template ? linked.getEserviceTemplateId() : linked.getEserviceId()))
                .get();

        return PollingUtils.pollUntil(
                () -> fetchAllLinkedResources(purposeTemplate),
                pt -> pt.getLinkedResourceIds().contains(resourceId)
        );
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
    @Override
    public void attemptPurposeTemplateReactivation(PurposeTemplate purposeTemplate) {
        restClient.unsuspendPurposeTemplate(purposeTemplate.getId()).withoutPolling();
    }

    @Override
    public void attemptPurposeTemplatePublication(PurposeTemplate purposeTemplate) {
        restClient.publishPurposeTemplate(purposeTemplate.getId()).withoutPolling();
    }

    @Override
    public void attemptRiskAnalysisAnswerAnnotationDocumentUpload(
            PurposeTemplate purposeTemplate,
            UUID answerId,
            String prettyName,
            String fixturePath
    ) {
        File documentFile = resolveFixtureFile(fixturePath);
        restClient.addRiskAnalysisAnswerAnnotationDocument(
                purposeTemplate.getId(),
                answerId,
                prettyName,
                documentFile
        ).withoutPolling();
    }

    @Override
    public void attemptLinkEService(PurposeTemplate purposeTemplate, EServiceRef eServiceRef) {
        LinkableResourceRequest payload = new LinkableResourceRequest()
                .resourceKind(LinkableResourceRequest.ResourceKindEnum.ESERVICE)
                .eserviceId(eServiceRef.id());
        restClient.linkResource(purposeTemplate.getId(), payload).withoutPolling();
    }

    @Override
    public void attemptUpdatePurposeTemplate(
            PurposeTemplate purposeTemplate,
            Tenant targetTenant,
            RiskAnalysisForm riskAnalysisForm
    ) {
        PurposeTemplateSeed payload = requestFactory.creationRequest(targetTenant, riskAnalysisForm);
        restClient.updatePurposeTemplate(purposeTemplate.getId(), payload).withoutPolling();
    }

    @Override
    public void attemptAddRiskAnalysisAnswerWithAnnotation(
            PurposeTemplate purposeTemplate,
            String annotationText
    ) {
        RiskAnalysisTemplateAnswerRequest payload = requestFactory.addRiskAnalysisAnswerRequest();
        payload.getAnswerData().annotation(new RiskAnalysisTemplateAnswerAnnotationSeed().text(annotationText));
        restClient.addRiskAnalysisAnswer(purposeTemplate.getId(), payload).withoutPolling();
    }

    @Override
    public void attemptUpdateRiskAnalysisAnswerAnnotation(
            PurposeTemplate purposeTemplate,
            UUID answerId,
            String annotationText
    ) {
        RiskAnalysisTemplateAnswerAnnotationSeed payload = new RiskAnalysisTemplateAnswerAnnotationSeed()
                .text(annotationText);
        restClient.addRiskAnalysisAnswerAnnotation(purposeTemplate.getId(), answerId, payload)
                .withoutPolling();
    }

    @Override
    public void attemptRenameRiskAnalysisAnswerAnnotationDocument(
            PurposeTemplate purposeTemplate,
            UUID answerId,
            UUID documentId,
            String newPrettyName
    ) {
        UpdateRiskAnalysisTemplateAnswerAnnotationDocumentSeed payload =
                new UpdateRiskAnalysisTemplateAnswerAnnotationDocumentSeed().prettyName(newPrettyName);
        restClient.updateRiskAnalysisAnswerAnnotationDocument(
                purposeTemplate.getId(),
                answerId,
                documentId,
                payload
        ).withoutPolling();
    }

    @Override
    public void attemptSignedRiskAnalysisDocumentDownload(PurposeTemplate purposeTemplate) {
        restClient.getRiskAnalysisTemplateSignedDocument(purposeTemplate.getId()).withoutPolling();
    }
}
