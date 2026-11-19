package it.pagopa.interop.bff.tenant.infrastructure;

import io.restassured.builder.ResponseBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import it.pagopa.application.context.EntityStore;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.bff.attribute.infrastructure.BffAttributeGateway;
import it.pagopa.interop.bff.attribute.infrastructure.BffAttributeMapper;
import it.pagopa.interop.bff.attribute.infrastructure.BffAttributeRestClient;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceDescriptorGateway;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.common.attribute.domain.AttributeKind;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.AttributesApi;
import it.pagopa.interop.generated.openapi.clients.bff.api.TenantsApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;

import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class BffTenantCategoryReadTest {
    private final ApiClient apiClient = mock(ApiClient.class);
    private final TestChainFactory chains =
            new TestChainFactory(mock(LastApiResponseStore.class), mock(EntityStore.class));

    @Test
    void readsOnlyActiveCertifiedAssignmentsThroughTheRealChain() {
        var api = mock(TenantsApi.class);
        var operation = mock(TenantsApi.GetTenantOper.class);
        UUID tenantId = UUID.randomUUID();
        UUID active = UUID.randomUUID();
        UUID revoked = UUID.randomUUID();
        when(apiClient.tenants()).thenReturn(api);
        when(api.getTenant()).thenReturn(operation);
        when(operation.tenantIdPath(tenantId)).thenReturn(operation);
        when(operation.execute(any())).thenAnswer(invocation -> {
            Function<Response, Response> handler = invocation.getArgument(0);
            return handler.apply(response("""
                    {"attributes":{"certified":[
                      {"id":"%s","kind":"CERTIFIED"},
                      {"id":"%s","kind":"CERTIFIED","revocationTimestamp":"2026-10-20T10:00:00Z"}
                    ]}}
                    """.formatted(active, revoked)));
        });
        var gateway = new BffTenantGateway(
                new BffTenantRestClient(chains, apiClient), mock(BffTenantRequestFactory.class));

        assertThat(gateway.getActiveCertifiedAttributeIds(TenantRef.of(tenantId))).containsExactly(active);
        verify(operation).tenantIdPath(tenantId);
        verify(operation).execute(any());
    }

    @Test
    void readsActiveAssignmentsForEachSupportedAttributeKindWithoutWritingTenantState() {
        var api = mock(TenantsApi.class);
        var operation = mock(TenantsApi.GetTenantOper.class);
        UUID tenantId = UUID.randomUUID();
        UUID certified = UUID.randomUUID();
        UUID discrete = UUID.randomUUID();
        UUID revokedCertified = UUID.randomUUID();
        UUID declared = UUID.randomUUID();
        UUID revokedDeclared = UUID.randomUUID();
        UUID verified = UUID.randomUUID();
        UUID revokedVerified = UUID.randomUUID();
        UUID expiredVerified = UUID.randomUUID();
        UUID activeAgreement = UUID.randomUUID();
        UUID revokedAgreement = UUID.randomUUID();
        String verificationDate = java.time.Instant.now().minusSeconds(3600).toString();
        String expirationDate = java.time.Instant.now().plusSeconds(3600).toString();
        when(apiClient.tenants()).thenReturn(api);
        when(api.getTenant()).thenReturn(operation);
        when(operation.tenantIdPath(tenantId)).thenReturn(operation);
        when(operation.execute(any())).thenAnswer(invocation -> {
            Function<Response, Response> handler = invocation.getArgument(0);
            return handler.apply(response("""
                    {"attributes":{
                      "certified":[
                        {"id":"%s","kind":"CERTIFIED"},
                        {"id":"%s","kind":"CERTIFIED_DISCRETE"},
                        {"id":"%s","kind":"CERTIFIED","revocationTimestamp":"2026-10-20T10:00:00Z"}
                      ],
                      "declared":[
                        {"id":"%s","kind":"DECLARED"},
                        {"id":"%s","kind":"DECLARED","revocationTimestamp":"2026-10-20T10:00:00Z"}
                      ],
                      "verified":[
                        {"id":"%s","kind":"VERIFIED","verifiedBy":[{"id":"%s","verificationDate":"%s","expirationDate":"%s"}],"revokedBy":[]},
                        {"id":"%s","kind":"VERIFIED","verifiedBy":[{"id":"%s","verificationDate":"%s","expirationDate":"%s"}],"revokedBy":[{"id":"%s","revocationDate":"%s"}]},
                        {"id":"%s","kind":"VERIFIED","verifiedBy":[{"id":"%s","verificationDate":"2020-01-01T00:00:00Z","expirationDate":"2021-01-01T00:00:00Z"}],"revokedBy":[]}
                      ]
                    }}
                    """.formatted(certified, discrete, revokedCertified, declared, revokedDeclared,
                    verified, activeAgreement, verificationDate, expirationDate,
                    revokedVerified, revokedAgreement, verificationDate, expirationDate,
                    revokedAgreement, java.time.Instant.now().toString(),
                    expiredVerified, UUID.randomUUID())));
        });
        var gateway = new BffTenantGateway(
                new BffTenantRestClient(chains, apiClient), mock(BffTenantRequestFactory.class));

        TenantRef ref = TenantRef.of(tenantId);
        assertThat(gateway.getActiveAttributeIds(ref, AttributeKind.CERTIFIED)).containsExactly(certified);
        assertThat(gateway.getActiveAttributeIds(ref, AttributeKind.CERTIFIED_DISCRETE)).containsExactly(discrete);
        assertThat(gateway.getActiveAttributeIds(ref, AttributeKind.DECLARED)).containsExactly(declared);
        assertThat(gateway.getActiveAttributeIds(ref, AttributeKind.VERIFIED)).containsExactly(verified);
        verify(operation, times(4)).execute(any());
    }

    @Test
    void resolvesAttributeUsingExactOriginAndCodeThroughTheRealChain() {
        var api = mock(AttributesApi.class);
        var operation = mock(AttributesApi.GetAttributeByOriginAndCodeOper.class);
        UUID attributeId = UUID.randomUUID();
        when(apiClient.attributes()).thenReturn(api);
        when(api.getAttributeByOriginAndCode()).thenReturn(operation);
        when(operation.originPath("IPA")).thenReturn(operation);
        when(operation.codePath("L6")).thenReturn(operation);
        when(operation.execute(any())).thenAnswer(invocation -> {
            Function<Response, Response> handler = invocation.getArgument(0);
            return handler.apply(response("""
                    {"id":"%s","code":"L6","name":"Comuni","kind":"CERTIFIED"}
                    """.formatted(attributeId)));
        });
        var gateway = new BffAttributeGateway(new BffAttributeRestClient(chains, apiClient),
                mock(BffEServiceDescriptorGateway.class),
                Mappers.getMapper(BffAttributeMapper.class));

        assertThat(gateway.findAttributeByOriginAndCode("IPA", "L6")).get()
                .extracting(it.pagopa.interop.common.attribute.domain.Attribute::getId).isEqualTo(attributeId);
        verify(operation).originPath("IPA");
        verify(operation).codePath("L6");
        verify(operation).execute(any());
    }

    @ParameterizedTest
    @ValueSource(ints = {404, 500})
    void notFoundStopsPollingWithoutDeserializingAnAttributeAndServerErrorsAreRetried(int firstStatus) {
        var api = mock(AttributesApi.class);
        var operation = mock(AttributesApi.GetAttributeByOriginAndCodeOper.class);
        when(apiClient.attributes()).thenReturn(api);
        when(api.getAttributeByOriginAndCode()).thenReturn(operation);
        when(operation.originPath("IPA")).thenReturn(operation);
        when(operation.codePath("L18")).thenReturn(operation);
        var attempts = new java.util.concurrent.atomic.AtomicInteger();
        when(operation.execute(any())).thenAnswer(invocation -> {
            Function<Response, Response> handler = invocation.getArgument(0);
            int status = attempts.getAndIncrement() == 0 ? firstStatus : 404;
            return handler.apply(new ResponseBuilder().setStatusCode(status)
                    .setContentType(ContentType.JSON)
                    .setBody("{\"status\":" + status + ",\"detail\":\"Attribute IPA/L18 not found\"}").build());
        });
        var gateway = new BffAttributeGateway(new BffAttributeRestClient(chains, apiClient),
                mock(BffEServiceDescriptorGateway.class),
                Mappers.getMapper(BffAttributeMapper.class));

        assertThat(gateway.findAttributeByOriginAndCode("IPA", "L18")).isEmpty();
        verify(operation, times(firstStatus == 404 ? 1 : 2)).execute(any());
    }

    private Response response(String body) {
        return new ResponseBuilder().setStatusCode(200).setContentType(ContentType.JSON).setBody(body).build();
    }
}
