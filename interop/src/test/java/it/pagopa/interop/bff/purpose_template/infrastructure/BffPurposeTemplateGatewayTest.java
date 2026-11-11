package it.pagopa.interop.bff.purpose_template.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.context.InMemoryEntityStore;
import it.pagopa.infrastructure.response.ApiResponse;
import it.pagopa.infrastructure.response.RawResponse;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.generated.openapi.clients.bff.model.LinkableResourceRequest;
import it.pagopa.interop.generated.openapi.clients.bff.model.LinkableResources;
import it.pagopa.interop.generated.openapi.clients.bff.model.LinkedResource;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisFormConfig;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.TargetTenantKind;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class BffPurposeTemplateGatewayTest {
    private final BffPurposeTemplateRestClient rest = mock(BffPurposeTemplateRestClient.class);
    private final LastApiResponseStore lastApiResponseStore = mock(LastApiResponseStore.class);
    private final BffPurposeTemplateGateway gateway = new BffPurposeTemplateGateway(
            rest, mock(BffPurposeTemplateMapper.class), mock(BffPurposeTemplateRequestFactory.class),
            mock(EntityStore.class), lastApiResponseStore);
    private final RiskAnalysisFormConfig config = RiskAnalysisFormConfig.builder().version("2.1")
            .questions(List.of(question("institutionalPurpose", "FREETEXT"),
                    question("purpose", "SINGLE"), question("legalBasis", "MULTI"),
                    question("otherPurpose", "FREETEXT"))).build();
    private final RiskAnalysisForm form = RiskAnalysisForm.builder().version("2.1")
            .answers(Map.of("institutionalPurpose", List.of("test purpose"),
                    "purpose", List.of("INSTITUTIONAL"),
                    "legalBasis", List.of("CONSENT", "CONTRACT"))).build();

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void linksAndAwaitsTheSelectedResourceKind(boolean template) {
        var mapper = mock(BffPurposeTemplateMapper.class);
        var store = mock(EntityStore.class);
        var adapter = new BffPurposeTemplateGateway(rest, mapper, mock(BffPurposeTemplateRequestFactory.class),
                store, lastApiResponseStore);
        var purpose = PurposeTemplate.builder().id(UUID.randomUUID()).build();
        var resourceId = UUID.randomUUID();
        var updated = purpose.toBuilder().resourceId(resourceId).build();
        TestChain<LinkedResource> link = mock(TestChain.class, RETURNS_DEEP_STUBS);
        TestChain<LinkableResources> read = mock(TestChain.class, RETURNS_DEEP_STUBS);
        var page = mock(LinkableResources.class, RETURNS_DEEP_STUBS);
        when(rest.linkResource(eq(purpose.getId()), any())).thenReturn(link);
        when(rest.getLinkableResources(purpose.getId(), 0, 50)).thenReturn(read);
        when(read.withPolling(any()).get()).thenReturn(page);
        when(page.getResults()).thenReturn(List.of());
        when(store.getById(purpose.getId(), PurposeTemplate.class)).thenReturn(Optional.empty());
        when(mapper.toPurposeTemplateWithLinkedResources(purpose, page)).thenReturn(updated);

        var result = template
                ? adapter.linkEServiceTemplate(purpose, new EServiceTemplateRef(resourceId))
                : adapter.linkEService(purpose, new EServiceRef(resourceId));

        assertThat(result).isSameAs(updated);
        var payload = ArgumentCaptor.forClass(LinkableResourceRequest.class);
        verify(rest).linkResource(eq(purpose.getId()), payload.capture());
        assertThat(payload.getValue().getResourceKind()).isEqualTo(template
                ? LinkableResourceRequest.ResourceKindEnum.ESERVICE_TEMPLATE
                : LinkableResourceRequest.ResourceKindEnum.ESERVICE);
        assertThat(payload.getValue().getEserviceId()).isEqualTo(template ? null : resourceId);
        assertThat(payload.getValue().getEserviceTemplateId()).isEqualTo(template ? resourceId : null);
        verify(store).upsert(updated);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void tryLinkRefreshesLinkedResourceIdsOnSuccessAndReturnsNullOnFailure(boolean template) {
        var mapper = mock(BffPurposeTemplateMapper.class);
        var store = new InMemoryEntityStore();
        var chains = new TestChainFactory(mock(LastApiResponseStore.class), store);
        var adapter = new BffPurposeTemplateGateway(rest, mapper, mock(BffPurposeTemplateRequestFactory.class),
                store, lastApiResponseStore);
        var purpose = PurposeTemplate.builder().id(UUID.randomUUID()).build();
        store.upsert("PT-1", purpose);
        var resourceId = UUID.randomUUID();
        var updated = purpose.toBuilder().resourceId(resourceId).build();

        RawResponse successLinkResponse = mock(RawResponse.class);
        when(successLinkResponse.isSuccess()).thenReturn(true);
        when(successLinkResponse.as(LinkedResource.class)).thenReturn(new LinkedResource());

        var page = mock(LinkableResources.class, RETURNS_DEEP_STUBS);
        when(page.getResults()).thenReturn(List.of());
        RawResponse pageResponse = mock(RawResponse.class);
        when(pageResponse.as(LinkableResources.class)).thenReturn(page);

        when(rest.linkResource(eq(purpose.getId()), any()))
                .thenAnswer(invocation -> chains.build(() -> successLinkResponse, LinkedResource.class));
        when(rest.getLinkableResources(purpose.getId(), 0, 50))
                .thenAnswer(invocation -> chains.build(() -> pageResponse, LinkableResources.class));
        when(mapper.toPurposeTemplateWithLinkedResources(purpose, page)).thenReturn(updated);

        var result = template
                ? adapter.tryLinkEServiceTemplate(purpose, new EServiceTemplateRef(resourceId))
                : adapter.tryLinkEService(purpose, new EServiceRef(resourceId));

        assertThat(result).isSameAs(updated);
        assertThat(store.getByAliasOrThrow("PT-1", PurposeTemplate.class).getLinkedResourceIds())
                .containsExactly(resourceId);

        RawResponse failedLinkResponse = mock(RawResponse.class);
        when(failedLinkResponse.isSuccess()).thenReturn(false);
        when(rest.linkResource(eq(purpose.getId()), any()))
                .thenAnswer(invocation -> chains.build(() -> failedLinkResponse, LinkedResource.class));

        var failedResult = template
                ? adapter.tryLinkEServiceTemplate(purpose, new EServiceTemplateRef(UUID.randomUUID()))
                : adapter.tryLinkEService(purpose, new EServiceRef(UUID.randomUUID()));

        assertThat(failedResult).isNull();
    }

    @ParameterizedTest
    @EnumSource(value = Tenant.class, names = {"PAGO_PA", "COMUNE_DI_MILANO", "SOGECAP"})
    void mapsFreeTextToSuggestionsAndKeepsChoiceAnswersFixed(Tenant tenant) {
        TestChain<CreatedResource> chain = mock(TestChain.class, RETURNS_DEEP_STUBS);
        when(rest.createPurposeTemplate(any())).thenReturn(chain);

        gateway.createPurposeTemplate(tenant, form, config);

        var seed = ArgumentCaptor.forClass(PurposeTemplateSeed.class);
        verify(rest).createPurposeTemplate(seed.capture());
        var payload = seed.getValue();
        assertThat(payload.getTargetTenantKind()).isEqualTo(
                tenant == Tenant.COMUNE_DI_MILANO ? TargetTenantKind.PA : TargetTenantKind.PRIVATE);
        assertThat(payload.getPurposeRiskAnalysisForm().getVersion()).isEqualTo("2.1");
        var answers = payload.getPurposeRiskAnalysisForm().getAnswers();
        assertThat(answers).containsOnlyKeys("institutionalPurpose", "purpose", "legalBasis");
        assertThat(answers.get("institutionalPurpose").getValues()).isEmpty();
        assertThat(answers.get("institutionalPurpose").getEditable()).isFalse();
        assertThat(answers.get("institutionalPurpose").getSuggestedValues()).containsExactly("test purpose");
        assertThat(answers.get("purpose").getValues()).containsExactly("INSTITUTIONAL");
        assertThat(answers.get("purpose").getEditable()).isFalse();
        assertThat(answers.get("legalBasis").getValues()).containsExactly("CONSENT", "CONTRACT");
        assertThat(answers.get("legalBasis").getEditable()).isFalse();
        assertThat(answers.get("purpose").getSuggestedValues()).isEmpty();
        assertThat(answers.get("legalBasis").getSuggestedValues()).isEmpty();
        assertThat(form.getAnswers().get("institutionalPurpose")).containsExactly("test purpose");
    }

    @Test
    void rejectsUnknownAnswersBeforeSendingARequest() {
        var unknown = form.toBuilder().answers(Map.of("unknown", List.of("test"))).build();

        assertThatThrownBy(() -> gateway.createPurposeTemplate(Tenant.PAGO_PA, unknown, config))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("unknown");

        verifyNoInteractions(rest);
    }

    @Test
    void rejectsMismatchedConfigurationVersionBeforeSendingARequest() {
        assertThatThrownBy(() -> gateway.createPurposeTemplate(
                Tenant.PAGO_PA, form.toBuilder().version("1.0").build(), config))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("versions must match");

        verifyNoInteractions(rest);
    }

    @Test
    void acceptsReceiveEServiceLinkRejectionWithConflictStatus() {
        ApiResponse response = mock(ApiResponse.class);
        when(response.getStatusCode()).thenReturn(409);
        when(lastApiResponseStore.getLastResponse()).thenReturn(response);

        assertThatCode(gateway::shouldFailLinkToReceiveEService).doesNotThrowAnyException();

        verifyNoInteractions(rest);
    }

    @ParameterizedTest
    @ValueSource(ints = {200, 201, 204, 302, 400, 401, 403, 404, 422, 500, 503})
    void rejectsUnexpectedReceiveEServiceLinkStatus(int status) {
        ApiResponse response = mock(ApiResponse.class);
        when(response.getStatusCode()).thenReturn(status);
        when(lastApiResponseStore.getLastResponse()).thenReturn(response);

        assertThatThrownBy(gateway::shouldFailLinkToReceiveEService)
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("409");
    }

    @Test
    void rejectsMissingReceiveEServiceLinkResponse() {
        assertThatThrownBy(gateway::shouldFailLinkToReceiveEService)
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Risposta");
    }

    private static RiskAnalysisFormConfig.Question question(String id, String type) {
        return RiskAnalysisFormConfig.Question.builder().id(id).dataType(type).build();
    }
}
