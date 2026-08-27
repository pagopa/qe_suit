package it.pagopa.interop.bff.purpose.infrastructure;

import it.pagopa.interop.bff.purpose.application.BffPurposeCreateCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.utils.RandomUtils;
import it.pagopa.interop.common.purpose.application.PurposeCommandFactory;
import it.pagopa.interop.common.purpose.application.PurposeCreateCommand;
import it.pagopa.interop.common.risk_analysis.application.RiskAnalysisDataFactory;
import it.pagopa.interop.common.risk_analysis.application.RiskAnalysisGateway;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisFormConfig;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormSeed;
import lombok.RequiredArgsConstructor;
import org.instancio.Instancio;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    public PurposeCreateCommand validFullPopulatedCreateCommand(EService eService) {
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
                .set(field(PurposeSeed::getEserviceId), eService.getId())
                .set(field(PurposeSeed::getConsumerId), consumerId)
                .set(field(PurposeSeed::getRiskAnalysisForm), riskAnalysisFormSeed)
                .create();

        return new BffPurposeCreateCommand(purposeSeed);
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}
