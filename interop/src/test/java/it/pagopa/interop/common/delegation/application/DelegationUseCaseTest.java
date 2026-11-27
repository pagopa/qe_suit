package it.pagopa.interop.common.delegation.application;

import it.pagopa.interop.common.delegation.application.command.ProducerDelegationCreationCommand;
import it.pagopa.interop.common.delegation.domain.Delegation;
import it.pagopa.interop.common.delegation.domain.DelegationState;
import it.pagopa.interop.common.delegation.domain.ProducerDelegationState;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.tenant.application.TenantUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class DelegationUseCaseTest {

    @Test
    void rejectsWhenRequestedDelegatorDoesNotOwnTheEService() {
        var gateway = mock(DelegationGateway.class);
        var tenantUseCase = mock(TenantUseCase.class);
        var currentUserSession = mock(AuthSession.class);
        var useCase = new DelegationUseCase(gateway, tenantUseCase, currentUserSession);
        EService eService = EService.builder()
                .id(UUID.randomUUID())
                .producerId(Tenant.COMUNE_DI_POZZALLO.getOrganizationId().toString())
                .descriptors(new ArrayList<>())
                .build();

        assertThatThrownBy(() -> useCase.createProducerDelegation(
                it.pagopa.interop.common.kernel.domain.TenantRef.of(
                        Tenant.COMUNE_DI_MILANO.getOrganizationId()
                ),
                it.pagopa.interop.common.kernel.domain.TenantRef.of(
                        Tenant.COMUNE_DI_POZZALLO.getOrganizationId()
                ),
                eService
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("non a " + Tenant.COMUNE_DI_MILANO.getOrganizationId());
        verifyNoInteractions(gateway, tenantUseCase, currentUserSession);
    }

    @ParameterizedTest
    @EnumSource(DelegationState.class)
    void waitsForActivationOnlyAfterApprovingAnActiveDelegation(DelegationState targetState) {
        var gateway = mock(DelegationGateway.class);
        var tenants = mock(TenantUseCase.class);
        var session = mock(AuthSession.class);
        var useCase = new DelegationUseCase(gateway, tenants, session);
        var delegator = Tenant.COMUNE_DI_MILANO;
        var delegate = Tenant.COMUNE_DI_POZZALLO;
        var delegateRef = TenantRef.of(delegate.getOrganizationId());
        var serviceRef = EServiceRef.of(UUID.randomUUID());
        var delegation = Delegation.builder().id(UUID.randomUUID()).build();
        when(gateway.createProducerDelegation(delegateRef, serviceRef)).thenReturn(delegation);
        var command = ProducerDelegationCreationCommand.builder()
                .delegator(delegator).delegate(delegate).eServiceRef(serviceRef).targetState(targetState).build();

        assertThat(useCase.createProducerDelegation(command)).isSameAs(delegation);

        var order = inOrder(session, tenants, gateway);
        order.verify(session).set(User.getTenantAdmin(delegate), delegate);
        order.verify(tenants).enableProducerDelegationAvailability(delegateRef);
        order.verify(session).set(User.getTenantAdmin(delegator), delegator);
        order.verify(gateway).createProducerDelegation(delegateRef, serviceRef);
        if (targetState == DelegationState.ACTIVE) {
            order.verify(session).set(User.getTenantAdmin(delegate), delegate);
            order.verify(gateway).approveProducerDelegation(delegation.getRef());
            order.verify(gateway).waitForProducerDelegationState(delegation.getRef(),
                    ProducerDelegationState.ACTIVE);
        }
        verifyNoMoreInteractions(session, tenants, gateway);
    }

    @Test
    void propagatesActivationWaitFailureInsteadOfReturningACompletedSetup() {
        var gateway = mock(DelegationGateway.class);
        var useCase = new DelegationUseCase(gateway, mock(TenantUseCase.class), mock(AuthSession.class));
        var serviceRef = EServiceRef.of(UUID.randomUUID());
        var delegation = Delegation.builder().id(UUID.randomUUID()).build();
        when(gateway.createProducerDelegation(
                TenantRef.of(Tenant.COMUNE_DI_POZZALLO.getOrganizationId()),
                serviceRef)).thenReturn(delegation);
        var failure = new IllegalStateException("Delegation did not become ACTIVE");
        when(gateway.waitForProducerDelegationState(delegation.getRef(),
                ProducerDelegationState.ACTIVE)).thenThrow(failure);
        var command = ProducerDelegationCreationCommand.builder()
                .delegator(Tenant.COMUNE_DI_MILANO).delegate(Tenant.COMUNE_DI_POZZALLO).eServiceRef(serviceRef)
                .targetState(DelegationState.ACTIVE).build();

        assertThatThrownBy(() -> useCase.createProducerDelegation(command)).isSameAs(failure);
    }
}
