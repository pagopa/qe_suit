package it.pagopa.interop.common.contract.service;

import it.pagopa.interop.common.contract.model.agreement.Agreement;
import it.pagopa.interop.common.contract.model.shared.enums.Channel;
import it.pagopa.interop.common.contract.template.action.TestChain;
import org.springframework.plugin.core.Plugin;

import java.util.UUID;

public interface IAgreementTestService extends Plugin<Channel> {
    TestChain<?, Agreement> createAgreement(UUID eserviceId, UUID descriptorId, UUID delegationId);

    default TestChain<?, Agreement> createAgreement(UUID eserviceId, UUID descriptorId) {
        return createAgreement(eserviceId, descriptorId, null);
    }

    TestChain<?, Agreement> read(UUID agreementId);

    TestChain<?, Agreement> submitAgreement(UUID agreementId);

    TestChain<?, Agreement> activateAgreement(UUID agreementId);
}
