package it.pagopa.interop.bff.purpose.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.bff.purpose.application.BffPurposeCreateCommand;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.*;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.purpose.application.PurposeCommandFactory;
import it.pagopa.interop.common.purpose.application.PurposeCreateCommand;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.common.risk_analysis.application.RiskAnalysisDataFactory;
import it.pagopa.interop.common.risk_analysis.application.RiskAnalysisGateway;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisFormConfig;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisFormTemplate;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.utils.RandomUtils;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.instancio.Instancio;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static it.pagopa.utils.RandomUtils.randomAlphanumericName;
import static org.instancio.Select.field;

@Component
@RequiredArgsConstructor
public class BffPurposeCommandFactory implements PurposeCommandFactory {

    private final RiskAnalysisGateway riskAnalysisGateway;
    private final RiskAnalysisDataFactory riskAnalysisDataFactory;
    private final AuthSession authSession;
    private final EntityStore entityStore;

    @Override
    public PurposeCreateCommand emptyCreateCommand() {
        return new BffPurposeCreateCommand();
    }

    @Override
    public PurposeCreateCommand validFullPopulatedCreateCommand(EServiceRef eServiceRef) {
        Tenant consumer = authSession.getTenant();
        UUID consumerId = consumer.getOrganizationId();

        RiskAnalysisFormConfig latestConfig = riskAnalysisGateway.getLatestRiskAnalysisConfig(consumer);
        Map<String, List<String>> answers =
                riskAnalysisDataFactory.getTemplateForTenant(consumer, true);

        RiskAnalysisFormSeed riskAnalysisFormSeed = Instancio.of(RiskAnalysisFormSeed.class)
                .set(field(RiskAnalysisFormSeed::getVersion), latestConfig.getVersion())
                .set(field(RiskAnalysisFormSeed::getAnswers), answers)
                .create();

        PurposeSeed purposeSeed = Instancio.of(PurposeSeed.class)
                .set(field(PurposeSeed::getTitle), RandomUtils.randomAlphanumericName("purpose"))
                .set(field(PurposeSeed::getDescription), RandomUtils.randomAlphanumericName("description"))
                .set(field(PurposeSeed::getIsFreeOfCharge), true)
                .set(field(PurposeSeed::getFreeOfChargeReason), RandomUtils.randomAlphanumericName("reason"))
                .set(field(PurposeSeed::getDailyCalls), 1)
                .set(field(PurposeSeed::getEserviceId), eServiceRef.id())
                .set(field(PurposeSeed::getConsumerId), consumerId)
                .set(field(PurposeSeed::getRiskAnalysisForm), riskAnalysisFormSeed)
                .create();

        return new BffPurposeCreateCommand(purposeSeed);
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }

    public PurposeSeed creationRequest(EServiceRef eServiceRef) {
        BffPurposeCreateCommand command =
                (BffPurposeCreateCommand) this.validFullPopulatedCreateCommand(eServiceRef);
        return command.getBffCreationPayload();
    }

    public PurposeEServiceSeed reverseCreationRequest(EServiceRef eServiceRef, EServiceRiskAnalysisRef riskAnalysisRef) {
        Tenant consumer = authSession.getTenant();
        return Instancio.of(PurposeEServiceSeed.class)
                .set(field(PurposeEServiceSeed::getTitle), randomAlphanumericName("purpose"))
                .set(field(PurposeEServiceSeed::getDescription), randomAlphanumericName("description"))
                .set(field(PurposeEServiceSeed::getIsFreeOfCharge), true)
                .set(field(PurposeEServiceSeed::getFreeOfChargeReason), randomAlphanumericName("reason"))
                .set(field(PurposeEServiceSeed::getDailyCalls), 1)
                .set(field(PurposeEServiceSeed::getEserviceId), eServiceRef.id())
                .set(field(PurposeEServiceSeed::getConsumerId), consumer.getOrganizationId())
                .set(field(PurposeEServiceSeed::getRiskAnalysisId), riskAnalysisRef.id())
                .create();
    }

    public PurposeUpdateContent updateRequest() {
        Tenant consumer = authSession.getTenant();
        return Instancio.of(PurposeUpdateContent.class)
                .set(field(PurposeUpdateContent::getTitle), randomAlphanumericName("purpose"))
                .set(field(PurposeUpdateContent::getDescription), randomAlphanumericName("description"))
                .set(field(PurposeUpdateContent::getIsFreeOfCharge), true)
                .set(field(PurposeUpdateContent::getFreeOfChargeReason), randomAlphanumericName("reason"))
                .set(field(PurposeUpdateContent::getDailyCalls), 1)
                .set(field(PurposeUpdateContent::getRiskAnalysisForm), riskAnalysisFormSeed(consumer))
                .create();
    }

    public ReversePurposeUpdateContent reverseUpdateRequest() {
        return Instancio.of(ReversePurposeUpdateContent.class)
                .set(field(ReversePurposeUpdateContent::getTitle), randomAlphanumericName("purpose"))
                .set(field(ReversePurposeUpdateContent::getDescription), randomAlphanumericName("description"))
                .set(field(ReversePurposeUpdateContent::getIsFreeOfCharge), true)
                .set(field(ReversePurposeUpdateContent::getFreeOfChargeReason), randomAlphanumericName("reason"))
                .set(field(ReversePurposeUpdateContent::getDailyCalls), 1)
                .create();
    }

    public PurposeFromTemplateSeed creationFromTemplateRequest(EServiceRef eServiceRef, PurposeTemplateRef purposeTemplateRef) {
        return creationFromTemplateRequest(
                eServiceRef.id(),
                randomAlphanumericName("purpose"),
                1,
                purposeTemplateRef
        );
    }

    public PurposeFromTemplateSeed creationFromTemplateRequest(
            PurposeCreateCommand createCommand,
            PurposeTemplateRef purposeTemplateRef
    ) {
        return creationFromTemplateRequest(
                createCommand.eserviceId(),
                createCommand.title(),
                createCommand.dailyCalls(),
                purposeTemplateRef
        );
    }

    private PurposeFromTemplateSeed creationFromTemplateRequest(
            UUID eserviceId,
            String title,
            Integer dailyCalls,
            PurposeTemplateRef purposeTemplateRef
    ) {
        if (eserviceId == null || title == null || dailyCalls == null) {
            throw new IllegalArgumentException("Purpose template creation requires an e-service, title, and daily calls");
        }
        Tenant consumer = authSession.getTenant();
        PurposeTemplate purposeTemplate = entityStore.getById(purposeTemplateRef.id(), PurposeTemplate.class)
                .orElseThrow(() -> new IllegalStateException(
                        "Selected purpose template not found in entity store: " + purposeTemplateRef.id()));

        return Instancio.of(PurposeFromTemplateSeed.class)
                .set(field(PurposeFromTemplateSeed::getTitle), title)
                .set(field(PurposeFromTemplateSeed::getDailyCalls), dailyCalls)
                .set(field(PurposeFromTemplateSeed::getEserviceId), eserviceId)
                .set(field(PurposeFromTemplateSeed::getConsumerId), consumer.getOrganizationId())
                .set(field(PurposeFromTemplateSeed::getRiskAnalysisForm),
                        riskAnalysisFormTemplateSeed(purposeTemplate.getRiskAnalysisFormTemplate()))
                .create();
    }

    public PatchPurposeUpdateFromTemplateContent patchFromTemplateRequest() {
        return Instancio.of(PatchPurposeUpdateFromTemplateContent.class)
                .set(field(PatchPurposeUpdateFromTemplateContent::getTitle), randomAlphanumericName("purpose"))
                .set(field(PatchPurposeUpdateFromTemplateContent::getDailyCalls), 1)
                .ignore(field(PatchPurposeUpdateFromTemplateContent::getRiskAnalysisForm))
                .create();
    }

    private RiskAnalysisFormSeed riskAnalysisFormSeed(Tenant consumer) {
        return new RiskAnalysisFormSeed()
                .version(riskAnalysisGateway.getLatestRiskAnalysisConfig(consumer).getVersion())
                .answers(new HashMap<>(riskAnalysisDataFactory.getTemplateForTenant(consumer, true)));
    }

    private RiskAnalysisFormSeed riskAnalysisFormTemplateSeed(@Nullable RiskAnalysisFormTemplate templateForm) {
        Map<String, List<String>> answers = new HashMap<>();
        if (templateForm == null || templateForm.getVersion() == null || templateForm.getVersion().isBlank()
                || templateForm.getAnswerTemplates() == null) {
            throw new IllegalArgumentException("Template risk analysis form must have a version and answers");
        }
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
