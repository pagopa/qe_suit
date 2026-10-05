package it.pagopa.interop.bff.tenant.infrastructure;

import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.bff.tenant.application.*;
import it.pagopa.interop.common.agreement.domain.AgreementRef;
import it.pagopa.interop.common.attribute.domain.AttributeRef;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.common.tenant.application.TenantGateway;
import it.pagopa.interop.common.tenant.application.TenantRequestFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BffTenantGateway implements TenantGateway {

    private final BffTenantRestClient restClient;
    private final BffTenantRequestFactory requestFactory;

    @Override
    public void assignCertifiedAttribute(@NonNull TenantRef tenantRef, @NonNull AttributeRef attributeRef) {
        var command = bffCommand(requestFactory.assignCertifiedAttributeCommand(attributeRef), BffAssignCertifiedAttributeCommand.class);
        executeWithPolling(restClient.addCertifiedAttribute(tenantRef.id(), command.getBffPayload()));
    }

    @Override
    public void assignCertifiedDiscreteAttribute(@NonNull TenantRef tenantRef, @NonNull AttributeRef attributeRef, int value) {
        var command = bffCommand(requestFactory.assignCertifiedDiscreteAttributeCommand(attributeRef, value), BffAssignCertifiedDiscreteAttributeCommand.class);
        executeWithPolling(restClient.addCertifiedDiscreteAttribute(tenantRef.id(), command.getBffPayload()));
    }

    @Override
    public void assignDeclaredAttribute(@NonNull AttributeRef attributeRef) {
        var command = bffCommand(requestFactory.assignDeclaredAttributeCommand(attributeRef), BffAssignDeclaredAttributeCommand.class);
        executeWithPolling(restClient.addDeclaredAttribute(command.getBffPayload()));
    }

    @Override
    public void assignVerifiedAttribute(@NonNull TenantRef tenantRef, @NonNull AttributeRef attributeRef, @NonNull AgreementRef agreementRef) {
        var command = bffCommand(requestFactory.assignVerifiedAttributeCommand(attributeRef, agreementRef), BffAssignVerifiedAttributeCommand.class);
        executeWithPolling(restClient.verifyVerifiedAttribute(tenantRef.id(), command.getBffPayload()));
    }

    @Override
    public void revokeDeclaredAttribute(@NonNull AttributeRef attributeRef) {
        executeWithPolling(restClient.revokeDeclaredAttribute(attributeRef.id()));
    }

    @Override
    public void revokeCertifiedAttribute(@NonNull TenantRef tenantRef, @NonNull AttributeRef attributeRef) {
        executeWithPolling(restClient.revokeCertifiedAttribute(tenantRef.id(), attributeRef.id()));
    }

    @Override
    public void revokeCertifiedDiscreteAttribute(@NonNull TenantRef tenantRef, @NonNull AttributeRef attributeRef) {
        executeWithPolling(restClient.revokeCertifiedDiscreteAttribute(tenantRef.id(), attributeRef.id()));
    }

    @Override
    public void revokeVerifiedAttribute(@NonNull TenantRef tenantRef, @NonNull AttributeRef attributeRef, @NonNull AgreementRef agreementRef) {
        var command = bffCommand(requestFactory.revokeVerifiedAttributeCommand(agreementRef), BffRevokeVerifiedAttributeCommand.class);
        executeWithPolling(restClient.revokeVerifiedAttribute(tenantRef.id(), attributeRef.id(), command.getBffPayload()));
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }


    private static <T> T bffCommand(Object command, Class<T> type) {
        if (!type.isInstance(command)) {
            throw new IllegalArgumentException("Command must be an instance of " + type.getSimpleName());
        }
        return type.cast(command);
    }

    private static void executeWithPolling(TestChain<Void> chain) {
        chain.withPolling(PollingStrategy.UNTIL_SUCCESS).get();
    }
}



