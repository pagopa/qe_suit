package it.pagopa.interop.common.contract.service;

import it.pagopa.interop.common.contract.model.eservice.EService;
import it.pagopa.interop.common.contract.model.shared.enums.Channel;
import it.pagopa.interop.common.contract.template.action.TestChain;
import org.springframework.plugin.core.Plugin;

import java.util.Map;
import java.util.UUID;

public interface IEServiceDescriptorTestService extends Plugin<Channel> {

    TestChain<?, EService> read(UUID eserviceId, UUID descriptorId);

    TestChain<?, EService> updateDraftDescriptorWith(UUID eserviceId, UUID descriptorId, Map<String, String> rawUpdateSeed);

    TestChain<?, EService> updateDraftDescriptorWithFullData(UUID eserviceId, UUID descriptorId);

    TestChain<?, EService> publish(UUID eserviceId, UUID descriptorId);
}