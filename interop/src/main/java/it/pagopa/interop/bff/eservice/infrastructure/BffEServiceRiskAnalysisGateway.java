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
    private final BffEServiceRiskAnalysisMapper mapper;

    /**
     * Recupera la prima {@link EServiceRiskAnalysis} associata all'e-service.
     * <p>
     * TODO (Domain gap): attualmente {@link EServiceRiskAnalysis} è gestita come entità a sé stante
     * (es. salvata separatamente nell'{@code EntityStore}) senza alcun riferimento all'{@code EService}
     * e senza che {@code EService} mantenga una collezione {@code List<EServiceRiskAnalysis> riskAnalyses}
     * (come invece avviene in {@code EServiceTemplate}), necessaria in particolare quando l'e-service
     * è in modalità {@code RECEIVE}. In futuro, senza modellare questa relazione nel dominio di {@code EService},
     * si rischia di non avere più l'informazione per associare una {@code EServiceRiskAnalysis} al relativo e-service.
     */
    @Override
    public EServiceRiskAnalysis getRiskAnalysis(EServiceRef eServiceRef) {
        return restClient.readEService(eServiceRef.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(details -> details.getRiskAnalysis().stream()
                        .findFirst()
                        .map(mapper::toDomain)
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
                                .map(mapper::toDomain))
                        .get(),
                java.util.Optional::isPresent
        ).orElseThrow();
    }


    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}

