package it.pagopa.interop.bff.attribute.infrastructure;

import io.restassured.builder.ResponseBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import it.pagopa.application.context.EntityStore;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceDescriptorGateway;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.attribute.domain.AttributeKind;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.AttributesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeSeed;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mapstruct.factory.Mappers;
import org.mockito.stubbing.Answer;

import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BffAttributeCreationTest {
    @ParameterizedTest
    @EnumSource(AttributeKind.class)
    void createsTheRequestedKindThroughItsExistingOperationAndRefreshesTheCreatedIdentity(AttributeKind kind) {
        var client = mock(ApiClient.class);
        var api = mock(AttributesApi.class, RETURNS_DEEP_STUBS);
        when(client.attributes()).thenReturn(api);
        UUID id = UUID.randomUUID();
        AttributeSeed seed = new AttributeSeed().name("scenario-fixture").description("fixture description");
        Answer<Response> created = invocation -> {
            Function<Response, Response> handler = invocation.getArgument(0);
            return handler.apply(response("{\"id\":\"" + id + "\"}"));
        };
        switch (kind) {
            case CERTIFIED -> when(api.createCertifiedAttribute().body(seed).execute(any())).thenAnswer(created);
            case DECLARED -> when(api.createDeclaredAttribute().body(seed).execute(any())).thenAnswer(created);
            case VERIFIED -> when(api.createVerifiedAttribute().body(seed).execute(any())).thenAnswer(created);
            case CERTIFIED_DISCRETE ->
                    when(api.createCertifiedDiscreteAttribute().body(seed).execute(any())).thenAnswer(created);
        }
        when(api.getAttributeById().attributeIdPath(id).execute(any())).thenAnswer(invocation -> {
            Function<Response, Response> handler = invocation.getArgument(0);
            return handler.apply(response("""
                    {"id":"%s","name":"scenario-fixture","kind":"%s"}
                    """.formatted(id, kind)));
        });
        var store = mock(EntityStore.class);
        var chains = new TestChainFactory(mock(LastApiResponseStore.class), store);
        var gateway = new BffAttributeGateway(new BffAttributeRestClient(chains, client),
                mock(BffEServiceDescriptorGateway.class),
                Mappers.getMapper(BffAttributeMapper.class));

        Attribute attribute = gateway.createAttribute(Attribute.builder()
                .name(seed.getName()).description(seed.getDescription()).kind(kind).build());

        assertThat(attribute.getId()).isEqualTo(id);
        assertThat(attribute.getKind()).isEqualTo(kind);
        verify(api.getAttributeById().attributeIdPath(id)).execute(any());
        switch (kind) {
            case CERTIFIED -> verify(api.createCertifiedAttribute().body(seed)).execute(any());
            case DECLARED -> verify(api.createDeclaredAttribute().body(seed)).execute(any());
            case VERIFIED -> verify(api.createVerifiedAttribute().body(seed)).execute(any());
            case CERTIFIED_DISCRETE -> verify(api.createCertifiedDiscreteAttribute().body(seed)).execute(any());
        }
        if (kind != AttributeKind.CERTIFIED) verify(api, never()).createCertifiedAttribute();
        if (kind != AttributeKind.DECLARED) verify(api, never()).createDeclaredAttribute();
        if (kind != AttributeKind.VERIFIED) verify(api, never()).createVerifiedAttribute();
        if (kind != AttributeKind.CERTIFIED_DISCRETE) verify(api, never()).createCertifiedDiscreteAttribute();
        verify(client).attributes();
        verifyNoMoreInteractions(client);
        verify(store, atLeastOnce()).upsert(null, attribute);
    }

    private Response response(String body) {
        return new ResponseBuilder().setStatusCode(200).setContentType(ContentType.JSON).setBody(body).build();
    }
}
