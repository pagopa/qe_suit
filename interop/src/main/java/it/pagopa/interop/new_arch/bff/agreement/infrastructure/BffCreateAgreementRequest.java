package it.pagopa.interop.new_arch.bff.agreement.infrastructure;

import it.pagopa.interop.common.contract.model.shared.DelegationRef;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementPayload;
import it.pagopa.interop.new_arch.common.agreement.application.request.CreateAgreementRequest;
import it.pagopa.interop.new_arch.common.eservice.domain.EService;
import it.pagopa.interop.new_arch.common.eservice.domain.EServiceDescriptor;

public class BffCreateAgreementRequest implements CreateAgreementRequest {

    private final AgreementPayload realPayload = new AgreementPayload();

    @Override
    public CreateAgreementRequest eService(EService eService) {
        realPayload.eserviceId(eService.getId());
        return this;
    }

    @Override
    public CreateAgreementRequest eServiceDescriptor(EServiceDescriptor eServiceDescriptor) {
        realPayload.descriptorId(eServiceDescriptor.getId());
        return this;
    }

    @Override
    public CreateAgreementRequest delegation(DelegationRef delegationRef) {
        realPayload.delegationId(delegationRef.getId());
        return null;
    }
}