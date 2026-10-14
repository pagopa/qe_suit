package it.pagopa.interop.bff.tenant.infrastructure;

import io.restassured.builder.RequestSpecBuilder;
import it.pagopa.application.context.EntityStore;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.TenantsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedDiscreteTenantAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedTenantAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.DeclaredTenantAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RevokeVerifiedAttributeRequest;
import it.pagopa.interop.generated.openapi.clients.bff.model.VerifiedTenantAttributeSeed;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BffTenantRestClientTest {

    @Mock
    private LastApiResponseStore lastApiResponseStore;

    @Mock
    private EntityStore entityStore;

    @Mock
    private ApiClient apiClient;

    private BffTenantRestClient buildClient() {
        when(apiClient.tenants()).thenReturn(TenantsApi.tenants(RequestSpecBuilder::new));
        TestChainFactory chainFactory = new TestChainFactory(lastApiResponseStore, entityStore);
        return new BffTenantRestClient(chainFactory, apiClient);
    }

    @Test
    @DisplayName("Tutti gli 8 metodi producono una TestChain non nulla senza invocare chiamate HTTP prima del terminale")
    void shouldProduceNonNullTestChainsWithoutEagerHttpExecution() {
        BffTenantRestClient client = buildClient();

        UUID tenantId = UUID.randomUUID();
        UUID attributeId = UUID.randomUUID();
        UUID agreementId = UUID.randomUUID();

        TestChain<Void> c1 = client.addCertifiedAttribute(
                tenantId,
                new CertifiedTenantAttributeSeed().id(attributeId)
        );
        TestChain<Void> c2 = client.addCertifiedDiscreteAttribute(
                tenantId,
                new CertifiedDiscreteTenantAttributeSeed().id(attributeId).certifiedDiscreteValue(5)
        );
        TestChain<Void> c3 = client.addDeclaredAttribute(
                new DeclaredTenantAttributeSeed().id(attributeId)
        );
        TestChain<Void> c4 = client.verifyVerifiedAttribute(
                tenantId,
                new VerifiedTenantAttributeSeed().id(attributeId).agreementId(agreementId)
        );
        TestChain<Void> c5 = client.revokeDeclaredAttribute(attributeId);
        TestChain<Void> c6 = client.revokeCertifiedAttribute(tenantId, attributeId);
        TestChain<Void> c7 = client.revokeCertifiedDiscreteAttribute(tenantId, attributeId);
        TestChain<Void> c8 = client.revokeVerifiedAttribute(
                tenantId,
                attributeId,
                new RevokeVerifiedAttributeRequest().agreementId(agreementId)
        );

        assertThat(c1).isNotNull();
        assertThat(c2).isNotNull();
        assertThat(c3).isNotNull();
        assertThat(c4).isNotNull();
        assertThat(c5).isNotNull();
        assertThat(c6).isNotNull();
        assertThat(c7).isNotNull();
        assertThat(c8).isNotNull();

        verifyNoInteractions(lastApiResponseStore);
        verifyNoInteractions(entityStore);
    }
}

