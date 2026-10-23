package it.pagopa.interop.bff.purpose_template.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.template.action.TestChain;
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
    private final BffPurposeTemplateGateway gateway = new BffPurposeTemplateGateway(
            rest, mock(BffPurposeTemplateMapper.class), mock(EntityStore.class));
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
        var adapter = new BffPurposeTemplateGateway(rest, mapper, store);
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

    private static RiskAnalysisFormConfig.Question question(String id, String type) {
        return RiskAnalysisFormConfig.Question.builder().id(id).dataType(type).build();
    }
}
