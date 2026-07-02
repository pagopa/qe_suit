package it.pagopa.interop.new_arch.common.agreement.application.request;

import it.pagopa.interop.common.contract.model.shared.DelegationRef;
import it.pagopa.interop.new_arch.common.eservice.domain.EService;
import it.pagopa.interop.new_arch.common.eservice.domain.EServiceDescriptor;

public interface CreateAgreementRequest {
    CreateAgreementRequest eService(EService eService);
    CreateAgreementRequest eServiceDescriptor(EServiceDescriptor eServiceDescriptor);
    CreateAgreementRequest delegation(DelegationRef delegationRef);
    EService getEService();
    EServiceDescriptor getEServiceDescriptor();
    DelegationRef getDelegation();
}
