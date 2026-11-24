package it.pagopa.interop.m2m.purpose_template.infrastructure;

import io.restassured.builder.ResponseBuilder;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.builder.RequestSpecBuilder;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.context.InMemoryEntityStore;
import it.pagopa.infrastructure.response.ApiResponse;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.api.PurposeTemplatesApi;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.PurposeTemplateDraftUpdateSeed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class M2MPurposeTemplateGatewayTest {
    private final InMemoryEntityStore store = new InMemoryEntityStore();
    private final LastApiResponseStore lastApiResponseStore = mock(LastApiResponseStore.class);
    private final ApiClient apiClient = mock(ApiClient.class);
    private final PurposeTemplatesApi api = mock(PurposeTemplatesApi.class);
    private final PurposeTemplatesApi.UpdateDraftPurposeTemplateOper update =
            mock(PurposeTemplatesApi.UpdateDraftPurposeTemplateOper.class);
    private final PurposeTemplatesApi.GetPurposeTemplateOper read =
            mock(PurposeTemplatesApi.GetPurposeTemplateOper.class);
    private final M2MPurposeTemplateMapper mapper = mock(M2MPurposeTemplateMapper.class);
    private final RequestSpecBuilder requestSpec = mock(RequestSpecBuilder.class);
    private final UUID templateId = UUID.randomUUID();
    private final PurposeTemplate original = PurposeTemplate.builder()
            .id(templateId)
            .title("old title")
            .description("old description")
            .targetDescription("old target")
            .build();
    private final PurposeTemplate updated = original.toBuilder()
            .title("new title")
            .description("new description")
            .targetDescription("new target")
            .purposeIsFreeOfCharge(false)
            .purposeDailyCalls(500)
            .handlesPersonalData(true)
            .build();
    private M2MPurposeTemplateGateway gateway;

    @BeforeEach
    void setUp() {
        when(apiClient.purposeTemplates()).thenReturn(api);
        when(api.updateDraftPurposeTemplate()).thenReturn(update);
        when(update.purposeTemplateIdPath(templateId)).thenReturn(update);
        when(update.reqSpec(any())).thenAnswer(invocation -> {
            Consumer<RequestSpecBuilder> consumer = invocation.getArgument(0);
            consumer.accept(requestSpec);
            return update;
        });
        when(update.body(any())).thenReturn(update);
        when(api.getPurposeTemplate()).thenReturn(read);
        when(read.purposeTemplateIdPath(templateId)).thenReturn(read);
        gateway = new M2MPurposeTemplateGateway(
                new M2MPurposeTemplateRestClient(
                        new TestChainFactory(lastApiResponseStore, store), apiClient),
                mapper,
                store,
                lastApiResponseStore);
        store.upsert(original);
    }

    @Test
    void patchesDraftMetadataAndRefreshesTheStoredTemplate() {
        String responseBody = """
                {
                  "id": "%s",
                  "targetDescription": "new target",
                  "targetTenantKind": "PA",
                  "creatorId": "%s",
                  "state": "DRAFT",
                  "createdAt": "2026-01-01T00:00:00Z",
                  "purposeTitle": "new title",
                  "purposeDescription": "new description",
                  "purposeIsFreeOfCharge": false,
                  "purposeDailyCalls": 500,
                  "handlesPersonalData": true
                }
                """.formatted(templateId, UUID.randomUUID());
        when(update.execute(any())).thenAnswer(invocation -> {
            Function<Response, Response> handler = invocation.getArgument(0);
            return handler.apply(response(200, responseBody));
        });
        when(read.execute(any())).thenAnswer(invocation -> {
            Function<Response, Response> handler = invocation.getArgument(0);
            return handler.apply(response(200, responseBody));
        });
        when(mapper.toPurposeTemplate(any(), eq(original))).thenReturn(updated);

        assertThat(gateway.updateMetadata(
                original, "new title", "new description", "new target", false, 500, true))
                .isSameAs(updated);

        var order = inOrder(api, update, read);
        order.verify(api).updateDraftPurposeTemplate();
        order.verify(update).purposeTemplateIdPath(templateId);
        order.verify(update).reqSpec(any());
        verify(requestSpec).setContentType("application/merge-patch+json; charset=UTF-8");
        order.verify(update).body(new PurposeTemplateDraftUpdateSeed()
                .purposeTitle("new title")
                .purposeDescription("new description")
                .targetDescription("new target")
                .purposeIsFreeOfCharge(false)
                .purposeDailyCalls(500)
                .handlesPersonalData(true));
        order.verify(update).execute(any());
        order.verify(api).getPurposeTemplate();
        order.verify(read).purposeTemplateIdPath(templateId);
        order.verify(read).execute(any());
        assertThat(store.getById(templateId, PurposeTemplate.class)).containsSame(updated);
    }

    @Test
    void assertsNotFoundWhenUpdatingMetadataForAnUnknownResource() {
        var response = mock(ApiResponse.class);
        when(lastApiResponseStore.getLastResponse()).thenReturn(response);
        when(response.getStatusCode()).thenReturn(404);

        gateway.shouldFailToUpdateMetadataAsNotFound();

        verify(lastApiResponseStore).getLastResponse();
    }

    private Response response(int status, String body) {
        return new ResponseBuilder().setStatusCode(status).setContentType(ContentType.JSON).setBody(body).build();
    }
}
