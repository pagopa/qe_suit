package it.pagopa.interop.common.contract.service;

import it.pagopa.interop.common.contract.model.agreement.Agreement;
import it.pagopa.interop.common.contract.template.action.TestChain;

import java.util.UUID;

public interface IAgreementTestService {
    TestChain<?, Agreement> createAgreement(UUID eserviceId, UUID descriptorId, UUID delegationId);

    default TestChain<?, Agreement> createAgreement(UUID eserviceId, UUID descriptorId) {
        return createAgreement(eserviceId, descriptorId, null);
    }

    TestChain<?, Agreement> read(UUID agreementId);

    TestChain<?, Agreement> submitAgreement(UUID agreementId);

    TestChain<?, Agreement> activateAgreement(UUID agreementId);
}
