package it.pagopa.interop.common.service;

import it.pagopa.interop.common.contract.model.eservice.EService;
import it.pagopa.interop.common.contract.model.shared.enums.Channel;
import it.pagopa.interop.common.contract.template.action.TestChain;
import it.pagopa.interop.common.utils.DeepMerger;
import org.springframework.plugin.core.Plugin;

import java.util.Map;
import java.util.UUID;

public interface EServiceService<CreationSeed> extends Plugin<Channel> {
    TestChain<?, EService> createWith(CreationSeed creationSeed);

    default TestChain<?, EService> create() {
        return createWith(buildFullCreationRequest());
    }

    default TestChain<?, EService> createWithOverride(CreationSeed override) {
        return createWith(DeepMerger.merge(override, buildFullCreationRequest()));
    }

    default TestChain<?, EService> createWith(Map<String, String> rawSeed) {
        return createWith(mapRawCreationSeed(rawSeed));
    }

    default TestChain<?, EService> createWithOverride(Map<String, String> rawOverride) {
        return createWithOverride(mapRawCreationSeed(rawOverride));
    }

    TestChain<?, EService> read(UUID eserviceId);

    TestChain<?, EService> read(UUID eserviceId, UUID descriptionId);

    CreationSeed buildFullCreationRequest();

    CreationSeed mapRawCreationSeed(Map<String, String> rawSeed);
}
