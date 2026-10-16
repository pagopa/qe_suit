package it.pagopa.interop.bff.purpose.infrastructure;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.risk_analysis.application.RiskAnalysisDataFactory;
import it.pagopa.interop.common.risk_analysis.application.RiskAnalysisGateway;
import it.pagopa.interop.generated.openapi.clients.bff.model.PatchPurposeUpdateFromTemplateContent;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeEServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeFromTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeUpdateContent;
import it.pagopa.interop.generated.openapi.clients.bff.model.ReversePurposeUpdateContent;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormSeed;
import lombok.RequiredArgsConstructor;
import org.instancio.Instancio;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.UUID;

import static it.pagopa.utils.RandomUtils.randomAlphanumericName;
import static org.instancio.Select.field;

/**
 * Factory dei payload validi (baseline) per le API Purpose del BFF.
 * L'identità corrente (consumer) determina il {@code consumerId} e il tipo di risk analysis.
 */
@Component
@RequiredArgsConstructor
public class BffPurposeRequestFactory {

    private final RiskAnalysisGateway riskAnalysisGateway;
    private final RiskAnalysisDataFactory riskAnalysisDataFactory;
    private final CurrentUserSession currentUserSession;

    public PurposeSeed creationRequest(EService eService) {
        Tenant consumer = currentUserSession.getTenant();
        return Instancio.of(PurposeSeed.class)
                .set(field(PurposeSeed::getTitle), randomAlphanumericName("purpose"))
                .set(field(PurposeSeed::getDescription), randomAlphanumericName("description"))
                .set(field(PurposeSeed::getIsFreeOfCharge), true)
                .set(field(PurposeSeed::getFreeOfChargeReason), randomAlphanumericName("reason"))
                .set(field(PurposeSeed::getDailyCalls), 1)
                .set(field(PurposeSeed::getEserviceId), eService.getId())
                .set(field(PurposeSeed::getConsumerId), consumer.getOrganizationId())
                .set(field(PurposeSeed::getRiskAnalysisForm), riskAnalysisFormSeed(consumer))
                .create();
    }

    public PurposeEServiceSeed reverseCreationRequest(EService eService, UUID riskAnalysisId) {
        Tenant consumer = currentUserSession.getTenant();
        return Instancio.of(PurposeEServiceSeed.class)
                .set(field(PurposeEServiceSeed::getTitle), randomAlphanumericName("purpose"))
                .set(field(PurposeEServiceSeed::getDescription), randomAlphanumericName("description"))
                .set(field(PurposeEServiceSeed::getIsFreeOfCharge), true)
                .set(field(PurposeEServiceSeed::getFreeOfChargeReason), randomAlphanumericName("reason"))
                .set(field(PurposeEServiceSeed::getDailyCalls), 1)
                .set(field(PurposeEServiceSeed::getEserviceId), eService.getId())
                .set(field(PurposeEServiceSeed::getConsumerId), consumer.getOrganizationId())
                .set(field(PurposeEServiceSeed::getRiskAnalysisId), riskAnalysisId)
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

    public PurposeFromTemplateSeed creationFromTemplateRequest(EService eService) {
        Tenant consumer = currentUserSession.getTenant();
        return Instancio.of(PurposeFromTemplateSeed.class)
                .set(field(PurposeFromTemplateSeed::getTitle), randomAlphanumericName("purpose"))
                .set(field(PurposeFromTemplateSeed::getDailyCalls), 1)
                .set(field(PurposeFromTemplateSeed::getEserviceId), eService.getId())
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

