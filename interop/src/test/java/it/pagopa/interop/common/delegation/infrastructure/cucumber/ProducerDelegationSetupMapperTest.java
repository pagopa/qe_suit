package it.pagopa.interop.common.delegation.infrastructure.cucumber;

import it.pagopa.infrastructure.context.CucumberEntityStore;
import it.pagopa.interop.common.delegation.domain.ProducerDelegationState;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.domain.Tenant;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProducerDelegationSetupMapperTest {

    @Test
    void resolvesAliasAndMapsValidatedBusinessValues() {
        var store = new CucumberEntityStore();
        EService eService = eService(Tenant.COMUNE_DI_MILANO);
        store.upsert("ES-1", eService);
        var mapper = new ProducerDelegationSetupMapper(store);

        var command = mapper.toCommand(
                "ES-1",
                Tenant.COMUNE_DI_MILANO,
                Tenant.COMUNE_DI_POZZALLO,
                ProducerDelegationState.REVOKED
        );

        assertThat(command.eService()).isSameAs(eService);
        assertThat(command.delegator()).isEqualTo(Tenant.COMUNE_DI_MILANO);
        assertThat(command.delegate()).isEqualTo(Tenant.COMUNE_DI_POZZALLO);
        assertThat(command.state()).isEqualTo(ProducerDelegationState.REVOKED);
    }

    @Test
    void reportsUnknownAliasInsteadOfSelectingAnotherEService() {
        var mapper = new ProducerDelegationSetupMapper(new CucumberEntityStore());

        assertThatThrownBy(() -> mapper.toCommand(
                "ES-missing",
                Tenant.COMUNE_DI_MILANO,
                Tenant.COMUNE_DI_POZZALLO,
                ProducerDelegationState.ACTIVE
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ES-missing");
    }

    private static EService eService(Tenant producer) {
        return EService.builder()
                .id(UUID.randomUUID())
                .producerId(producer.getOrganizationId().toString())
                .descriptors(new ArrayList<>())
                .build();
    }
}
