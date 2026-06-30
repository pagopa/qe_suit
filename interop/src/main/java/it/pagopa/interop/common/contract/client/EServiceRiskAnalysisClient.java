package it.pagopa.interop.common.contract.client;

import it.pagopa.interop.common.contract.model.eservice.EService;
import it.pagopa.interop.common.contract.model.shared.enums.Channel;
import it.pagopa.interop.common.contract.template.action.TestChain;
import org.springframework.plugin.core.Plugin;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface EServiceRiskAnalysisClient extends Plugin<Channel> {

    TestChain<?, EService> addRiskAnalysis(UUID eserviceId, String version, Map<String, List<String>> answers);

    TestChain<?, EService> addLatestRiskAnalysis(UUID eserviceId, boolean completed);
}