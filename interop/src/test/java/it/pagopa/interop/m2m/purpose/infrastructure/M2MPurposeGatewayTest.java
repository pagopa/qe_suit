package it.pagopa.interop.m2m.purpose.infrastructure;

import io.restassured.builder.ResponseBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.context.InMemoryEntityStore;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.PurposeTemplateRef;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeVersion;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisAnswerTemplate;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisFormTemplate;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.api.PurposesApi;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.PurposeFromTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.ReversePurposeDraftUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.UpdateDraftPurposeRequest;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.PurposeVersionSeed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class M2MPurposeGatewayTest {
    private final InMemoryEntityStore store = new InMemoryEntityStore();
    private final ApiClient apiClient = mock(ApiClient.class);
    private final PurposesApi api = mock(PurposesApi.class);
    private final PurposesApi.CreatePurposeVersionOper create = mock(PurposesApi.CreatePurposeVersionOper.class);
    private final PurposesApi.ArchivePurposeOper archive = mock(PurposesApi.ArchivePurposeOper.class);
    private final PurposesApi.GetPurposeOper read = mock(PurposesApi.GetPurposeOper.class);
    private final PurposesApi.CreatePurposeFromTemplateOper createFromTemplate =
            mock(PurposesApi.CreatePurposeFromTemplateOper.class);
    private final PurposesApi.UpdateDraftPurposeOper updatePurpose =
            mock(PurposesApi.UpdateDraftPurposeOper.class);
    private final PurposesApi.UpdateDraftReversePurposeOper updateReversePurpose =
            mock(PurposesApi.UpdateDraftReversePurposeOper.class);
    private final M2MPurposeMapper mapper = mock(M2MPurposeMapper.class);
    private final M2MPurposeRequestFactory requestFactory = new M2MPurposeRequestFactory(store);
    private final UUID newVersionId = UUID.randomUUID();
    private final PurposeVersion active = PurposeVersion.builder().id(UUID.randomUUID())
            .createdAt(Instant.EPOCH).purposeVersionState(PurposeVersionState.ACTIVE).dailyCalls(10).build();
    private final Purpose original = Purpose.builder().id(UUID.randomUUID()).version(active).build();
    private final Purpose updated = original.toBuilder().version(active.toBuilder().id(newVersionId)
            .purposeVersionState(PurposeVersionState.WAITING_FOR_APPROVAL).dailyCalls(51).build()).build();
    private M2MPurposeGateway gateway;

    @BeforeEach
    void setUp() {
        when(apiClient.purposes()).thenReturn(api);
        when(api.createPurposeVersion()).thenReturn(create);
        when(create.purposeIdPath(original.getId())).thenReturn(create);
        when(create.body(any())).thenReturn(create);
        when(api.archivePurpose()).thenReturn(archive);
        when(archive.purposeIdPath(original.getId())).thenReturn(archive);
        when(api.getPurpose()).thenReturn(read);
        when(read.purposeIdPath(original.getId())).thenReturn(read);
        when(api.createPurposeFromTemplate()).thenReturn(createFromTemplate);
        when(createFromTemplate.purposeTemplateIdPath(any())).thenReturn(createFromTemplate);
        when(createFromTemplate.body(any())).thenReturn(createFromTemplate);
        when(api.updateDraftPurpose()).thenReturn(updatePurpose);
        when(updatePurpose.purposeIdPath(original.getId())).thenReturn(updatePurpose);
        when(updatePurpose.reqSpec(any())).thenReturn(updatePurpose);
        when(updatePurpose.body(any())).thenReturn(updatePurpose);
        when(api.updateDraftReversePurpose()).thenReturn(updateReversePurpose);
        when(updateReversePurpose.purposeIdPath(original.getId())).thenReturn(updateReversePurpose);
        when(updateReversePurpose.reqSpec(any())).thenReturn(updateReversePurpose);
        when(updateReversePurpose.body(any())).thenReturn(updateReversePurpose);
        gateway = new M2MPurposeGateway(
                new M2MPurposeRestClient(new TestChainFactory(mock(LastApiResponseStore.class), store), apiClient),
                mapper,
                requestFactory, mock(LastApiResponseStore.class));
        store.upsert("purpose", original);
    }

    @Test
    void createsTheVersionOnceAndWaitsForItsExactIdBeforeRefreshingTheAliasedContext() {
        when(create.execute(any())).thenAnswer(invocation -> {
            Function<Response, Response> handler = invocation.getArgument(0);
            return handler.apply(response(201, "{\"id\":\"" + newVersionId + "\",\"dailyCalls\":51}"));
        });
        when(read.execute(any())).thenAnswer(invocation -> {
            Function<Response, Response> handler = invocation.getArgument(0);
            return handler.apply(response(200, "{}"));
        });
        Purpose unrelatedWaiting = original.toBuilder().version(updated.getLastWaitingForApprovalVersion().toBuilder()
                .id(UUID.randomUUID()).build()).build();
        when(mapper.toPurpose(any())).thenReturn(unrelatedWaiting, updated);

        assertThat(gateway.updatePurposeDailyCalls(original.getRef(), 51)).isSameAs(updated);

        verify(create).purposeIdPath(original.getId());
        verify(create).body(new PurposeVersionSeed().dailyCalls(51));
        verify(create).execute(any());
        verify(read, times(2)).execute(any());
        assertThat(store.getByAliasOrThrow("purpose", Purpose.class)).isSameAs(updated);
        assertThat(updated.getLastActiveVersion()).isSameAs(active);
    }

    @Test
    void attemptsPurposeMetadataUpdateOnceWithoutRefreshingContext() {
        when(updatePurpose.execute(any())).thenAnswer(invocation -> {
            Function<Response, Response> handler = invocation.getArgument(0);
            return handler.apply(response(403, "{}"));
        });

        gateway.tryToUpdatePurposeMetadata(
                original.getRef(), "new title", "new description", false, "fee exemption", 123);

        verify(updatePurpose).body(
                new UpdateDraftPurposeRequest().title("new title").description("new description")
                        .isFreeOfCharge(false).freeOfChargeReason("fee exemption").dailyCalls(123));
        verify(updatePurpose).execute(any());
        verifyNoInteractions(read, updateReversePurpose, mapper);
        assertThat(store.getByAliasOrThrow("purpose", Purpose.class)).isSameAs(original);
    }

    @Test
    void attemptsReversePurposeMetadataUpdateOnceWithoutRefreshingContext() {
        when(updateReversePurpose.execute(any())).thenAnswer(invocation -> {
            Function<Response, Response> handler = invocation.getArgument(0);
            return handler.apply(response(403, "{}"));
        });

        gateway.tryToUpdateReversePurposeMetadata(
                original.getRef(), "new title", "new description", null, null, null);

        verify(updateReversePurpose).body(
                new ReversePurposeDraftUpdateSeed().title("new title").description("new description"));
        verify(updateReversePurpose).execute(any());
        verifyNoInteractions(read, updatePurpose, mapper);
        assertThat(store.getByAliasOrThrow("purpose", Purpose.class)).isSameAs(original);
    }

    @Test
    void patchesPurposeMetadataAndRefreshesTheAliasedContext() {
        Purpose renamed = original.toBuilder().title("new title").description("new description").build();
        when(updatePurpose.execute(any())).thenAnswer(invocation -> {
            Function<Response, Response> handler = invocation.getArgument(0);
            return handler.apply(response(200, "{}"));
        });
        when(read.execute(any())).thenAnswer(invocation -> {
            Function<Response, Response> handler = invocation.getArgument(0);
            return handler.apply(response(200, "{}"));
        });
        when(mapper.toPurpose(any())).thenReturn(renamed);

        assertThat(gateway.updatePurposeMetadata(
                original.getRef(), "new title", "new description", null, null, null))
                .isSameAs(renamed);

        var order = inOrder(api, updatePurpose, read);
        order.verify(api).updateDraftPurpose();
        order.verify(updatePurpose).purposeIdPath(original.getId());
        order.verify(updatePurpose).body(
                new UpdateDraftPurposeRequest().title("new title").description("new description"));
        order.verify(updatePurpose).execute(any());
        order.verify(api).getPurpose();
        order.verify(read).purposeIdPath(original.getId());
        order.verify(read).execute(any());
        verifyNoInteractions(updateReversePurpose);
        assertThat(store.getByAliasOrThrow("purpose", Purpose.class)).isSameAs(renamed);
    }

    @Test
    void patchesReversePurposeMetadataAndRefreshesTheAliasedContext() {
        Purpose renamed = original.toBuilder().title("new title").description("new description").build();
        when(updateReversePurpose.execute(any())).thenAnswer(invocation -> {
            Function<Response, Response> handler = invocation.getArgument(0);
            return handler.apply(response(200, "{}"));
        });
        when(read.execute(any())).thenAnswer(invocation -> {
            Function<Response, Response> handler = invocation.getArgument(0);
            return handler.apply(response(200, "{}"));
        });
        when(mapper.toPurpose(any())).thenReturn(renamed);

        assertThat(gateway.updateReversePurposeMetadata(
                original.getRef(), "new title", "new description", false, "fee exemption", 123))
                .isSameAs(renamed);

        verify(api).updateDraftReversePurpose();
        verify(updateReversePurpose).purposeIdPath(original.getId());
        verify(updateReversePurpose).body(
                new ReversePurposeDraftUpdateSeed().title("new title").description("new description")
                        .isFreeOfCharge(false).freeOfChargeReason("fee exemption").dailyCalls(123));
        verify(updateReversePurpose).execute(any());
        verify(read).execute(any());
        assertThat(store.getByAliasOrThrow("purpose", Purpose.class)).isSameAs(renamed);
    }

    @Test
    void createsPurposeFromTemplateAndRefreshesContext() {
        UUID templateId = UUID.randomUUID();
        UUID eServiceId = UUID.randomUUID();
        PurposeTemplateRef templateRef = PurposeTemplateRef.of(templateId);
        PurposeTemplate template = PurposeTemplate.builder()
                .id(templateId)
                .riskAnalysisFormTemplate(RiskAnalysisFormTemplate.builder()
                        .version("1.0")
                        .answerTemplates(Map.of(
                                "suggested", RiskAnalysisAnswerTemplate.builder()
                                        .editable(false)
                                        .suggestedValues(List.of("predefined answer"))
                                        .build(),
                                "editable", RiskAnalysisAnswerTemplate.builder()
                                        .editable(true)
                                        .build()
                        ))
                        .build())
                .build();
        store.upsert(template);
        when(createFromTemplate.execute(any())).thenAnswer(invocation -> {
            Function<Response, Response> handler = invocation.getArgument(0);
            return handler.apply(response(201, "{\"id\":\"" + original.getId() + "\"}"));
        });
        when(read.execute(any())).thenAnswer(invocation -> {
            Function<Response, Response> handler = invocation.getArgument(0);
            return handler.apply(response(200, "{}"));
        });
        when(mapper.toPurpose(any())).thenReturn(original);

        assertThat(gateway.createPurposeFromTemplate(templateRef, EServiceRef.of(eServiceId)))
                .isSameAs(original);

        var payloadCaptor = org.mockito.ArgumentCaptor.forClass(PurposeFromTemplateSeed.class);
        verify(createFromTemplate).purposeTemplateIdPath(templateId);
        verify(createFromTemplate).body(payloadCaptor.capture());
        PurposeFromTemplateSeed payload = payloadCaptor.getValue();
        assertThat(payload.getEserviceId()).isEqualTo(eServiceId);
        assertThat(payload.getTitle()).isNotBlank();
        assertThat(payload.getDailyCalls()).isEqualTo(1);
        assertThat(payload.getRiskAnalysisForm().getVersion()).isEqualTo("1.0");
        assertThat(payload.getRiskAnalysisForm().getAnswers())
                .containsEntry("suggested", java.util.List.of("predefined answer"))
                .containsKey("editable");
        assertThat(payload.getRiskAnalysisForm().getAnswers().get("editable").get(0)).isNotEmpty();
        verify(createFromTemplate).execute(any());
        verify(read).execute(any());
        assertThat(store.getByAliasOrThrow("purpose", Purpose.class)).isSameAs(original);
    }

    @Test
    void archivesThePurposeAndWaitsForTheRequestedVersionBeforeRefreshingContext() {
        Purpose archived = original.toBuilder().clearVersions().version(active.toBuilder()
                .purposeVersionState(PurposeVersionState.ARCHIVED).build()).build();
        when(archive.execute(any())).thenAnswer(invocation -> {
            Function<Response, Response> handler = invocation.getArgument(0);
            return handler.apply(response(200, "{}"));
        });
        when(read.execute(any())).thenAnswer(invocation -> {
            Function<Response, Response> handler = invocation.getArgument(0);
            return handler.apply(response(200, "{}"));
        });
        when(mapper.toPurpose(any())).thenReturn(archived);

        assertThat(gateway.archivePurpose(original.getRef(), active.getRef())).isSameAs(archived);

        verify(archive).purposeIdPath(original.getId());
        verify(archive).execute(any());
        verify(read).execute(any());
        assertThat(store.getByAliasOrThrow("purpose", Purpose.class)).isSameAs(archived);
    }

    private Response response(int status, String body) {
        return new ResponseBuilder().setStatusCode(status).setContentType(ContentType.JSON).setBody(body).build();
    }
}
