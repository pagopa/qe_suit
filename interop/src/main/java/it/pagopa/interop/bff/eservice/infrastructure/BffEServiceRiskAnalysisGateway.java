package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.eservice.application.EServiceRiskAnalysisGateway;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysisSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormSeed;
import it.pagopa.utils.RandomUtils;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class BffEServiceRiskAnalysisGateway implements EServiceRiskAnalysisGateway {

    private final BffEServiceRestClient restClient;

    @Override
    public EServiceRiskAnalysis getRiskAnalysis(EServiceRef eServiceRef) {
        return restClient.readEService(eServiceRef.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(details -> details.getRiskAnalysis().stream()
                        .findFirst()
                        .map(this::toDomain)
                        .orElse(null))
                .get();
    }

    @Override
    public EServiceRiskAnalysis addRiskAnalysis(EServiceRef eServiceRef, RiskAnalysisForm riskAnalysisForm) {
        EServiceRiskAnalysisSeed payload = new EServiceRiskAnalysisSeed()
                .name(RandomUtils.randomAlphanumericName("risk-analysis"))
                .riskAnalysisForm(new RiskAnalysisFormSeed()
                        .version(riskAnalysisForm.getVersion())
                        .answers(new HashMap<>(riskAnalysisForm.getAnswers())));

        restClient.addRiskAnalysis(eServiceRef.id(), payload)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();

        return PollingUtils.pollUntil(
                () -> restClient.readEService(eServiceRef.id())
                        .withPolling(PollingStrategy.UNTIL_SUCCESS)
                        .map(details -> details.getRiskAnalysis().stream()
                                .filter(analysis -> payload.getName().equals(analysis.getName()))
                                .findFirst()
                                .map(this::toDomain))
                        .get(),
                java.util.Optional::isPresent
        ).orElseThrow();
    }

    private EServiceRiskAnalysis toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysis source) {
        return EServiceRiskAnalysis.builder()
                .id(source.getId())
                .name(source.getName())
                .riskAnalysisForm(RiskAnalysisForm.builder()
                        .version(source.getRiskAnalysisForm().getVersion())
                        .answers(source.getRiskAnalysisForm().getAnswers())
                        .build())
                .build();
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}

