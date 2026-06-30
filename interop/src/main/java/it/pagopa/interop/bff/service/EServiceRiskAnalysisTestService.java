package it.pagopa.interop.bff.service;

import it.pagopa.interop.bff.support.RiskAnalysisSeedFactory;
import it.pagopa.interop.common.contract.service.IEServiceRiskAnalysisTestService;
import it.pagopa.interop.common.contract.model.eservice.EService;
import it.pagopa.interop.common.contract.model.shared.enums.Channel;
import it.pagopa.interop.common.contract.template.action.TestChain;
import it.pagopa.interop.common.contract.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.contract.template.rest.RestService;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysisSeed;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EServiceRiskAnalysisTestService extends RestService implements IEServiceRiskAnalysisTestService {

    private final EservicesApi eservicesApi;
    private final RiskAnalysisSeedFactory riskAnalysisSeedFactory;
    private final EServiceTestService eserviceBffClient;

    @Override
    public TestChain<?, EService> addRiskAnalysis(UUID eserviceId, String version, Map<String, List<String>> answers) {
        EServiceRiskAnalysisSeed seed = riskAnalysisSeedFactory.from(version, answers);
        return addRiskAnalysis(eserviceId, seed);
    }

    @Override
    public TestChain<?, EService> addLatestRiskAnalysis(UUID eserviceId, boolean completed) {
        EServiceRiskAnalysisSeed seed = riskAnalysisSeedFactory.latest(completed);
        return addRiskAnalysis(eserviceId, seed);
    }

    public TestChain<?, EService> addRiskAnalysis(UUID eserviceId, EServiceRiskAnalysisSeed seed) {
        return super.update(
                () -> eservicesApi.addRiskAnalysisToEServiceWithHttpInfo(eserviceId, seed),
                id -> eserviceBffClient.read(eserviceId)
                        .withPolling(PollingStrategy.UNTIL_SUCCESS)
                        .getModel()
        );
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}