package it.pagopa.interop.common.contract.client;

import it.pagopa.interop.common.contract.model.eservice.EService;
import it.pagopa.interop.common.contract.model.shared.enums.Channel;
import it.pagopa.interop.common.contract.template.action.TestChain;
import org.springframework.plugin.core.Plugin;

import java.util.Map;
import java.util.UUID;

public interface EServiceClient extends Plugin<Channel> {

    TestChain<?, EService> createAndFillDraftEservice();

    TestChain<?, EService> createDraftWith(Map<String, String> rawCreationSeed);

    TestChain<?, EService> createDraftWithOverride(Map<String, String> rawOverrides);

    TestChain<?, EService> read(UUID eserviceId);

}
