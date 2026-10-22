package it.pagopa.interop.common.delegation.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.domain.Tenant;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class DelegationUseCaseTest {

    @Test
    void rejectsWhenRequestedDelegatorDoesNotOwnTheEService() {
        var gateway = mock(DelegationGateway.class);
        var useCase = new DelegationUseCase(gateway);
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
        verifyNoInteractions(gateway);
    }
}
