package it.pagopa.interop.bff.purpose.infrastructure;

import it.pagopa.interop.bff.purpose.application.BffPurposeCreateCommand;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysisRef;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.purpose.application.PurposeCommandFactory;
import it.pagopa.interop.common.purpose.application.PurposeCreateCommand;
import it.pagopa.interop.common.risk_analysis.application.RiskAnalysisDataFactory;
import it.pagopa.interop.common.risk_analysis.application.RiskAnalysisGateway;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisFormConfig;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.utils.RandomUtils;
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
    private final CurrentUserSession currentUserSession;

    @Override
    public PurposeCreateCommand emptyCreateCommand() {
        return new BffPurposeCreateCommand();
    }

    @Override
    public PurposeCreateCommand validFullPopulatedCreateCommand(EServiceRef eServiceRef) {
        Tenant consumer = currentUserSession.getTenant();
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
        Tenant consumer = currentUserSession.getTenant();
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
        Tenant consumer = currentUserSession.getTenant();
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

    public PurposeFromTemplateSeed creationFromTemplateRequest(EServiceRef eServiceRef) {
        Tenant consumer = currentUserSession.getTenant();
        return Instancio.of(PurposeFromTemplateSeed.class)
                .set(field(PurposeFromTemplateSeed::getTitle), randomAlphanumericName("purpose"))
                .set(field(PurposeFromTemplateSeed::getDailyCalls), 1)
                .set(field(PurposeFromTemplateSeed::getEserviceId), eServiceRef.id())
                .set(field(PurposeFromTemplateSeed::getConsumerId), consumer.getOrganizationId())
                .ignore(field(PurposeFromTemplateSeed::getRiskAnalysisForm))
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
}
