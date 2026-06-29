package it.pagopa.interop.common.contract.service;

import it.pagopa.interop.common.contract.model.eservice.EService;
import it.pagopa.interop.common.contract.model.shared.enums.Channel;
import it.pagopa.interop.common.contract.template.action.TestChain;
import org.springframework.plugin.core.Plugin;

import java.util.UUID;

public interface EServiceService extends Plugin<Channel> {
    TestChain<?, EService> create();
    TestChain<?, EService> read(UUID eserviceId);
}
