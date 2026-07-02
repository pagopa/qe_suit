package it.pagopa.interop.new_arch.bff.agreement.infrastructure.request;

import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementPayload;
import it.pagopa.interop.new_arch.common.agreement.application.request.CreateAgreementRequest;
import it.pagopa.interop.new_arch.common.eservice.domain.EService;
import it.pagopa.interop.new_arch.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.new_arch.common.kernel.domain.DelegationRef;
import lombok.Getter;

@Getter
public class BffCreateAgreementRequest implements CreateAgreementRequest {

    private final AgreementPayload realPayload = new AgreementPayload();
    private EService eService;
    private DelegationRef delegation;
    private EServiceDescriptor eServiceDescriptor;

    @Override
    public CreateAgreementRequest eService(EService eService) {
        this.eService = eService;
        realPayload.eserviceId(eService.getId());
        return this;
    }

    @Override
    public CreateAgreementRequest eServiceDescriptor(EServiceDescriptor eServiceDescriptor) {
        this.eServiceDescriptor = eServiceDescriptor;
        realPayload.descriptorId(eServiceDescriptor.getId());
        return this;
    }

    @Override
    public CreateAgreementRequest delegation(DelegationRef delegationRef) {
        this.delegation = delegationRef;
        realPayload.delegationId(delegationRef.getId());
        return this;
    }

    public BffCreateAgreementRequest payload(AgreementPayload payload) {
        this.realPayload.eserviceId(payload.getEserviceId());
        this.realPayload.descriptorId(payload.getDescriptorId());
        this.realPayload.delegationId(payload.getDelegationId());
        return this;
    }
}