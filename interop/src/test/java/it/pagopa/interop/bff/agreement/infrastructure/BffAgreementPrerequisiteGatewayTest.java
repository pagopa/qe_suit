package it.pagopa.interop.bff.agreement.infrastructure;

import io.restassured.builder.ResponseBuilder;
import io.restassured.http.ContentType;
import it.pagopa.application.context.EntityStore;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.common.agreement.application.AgreementGateway;
import it.pagopa.interop.common.agreement.domain.Agreement;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.*;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.AgreementsApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class BffAgreementPrerequisiteGatewayTest {
    private final UUID id = UUID.randomUUID();
    private final EServiceDescriptor descriptor = EServiceDescriptor.builder().id(UUID.randomUUID()).build();
    private final EService service = EService.builder().id(UUID.randomUUID())
            .producerId(Tenant.COMUNE_DI_MILANO.getOrganizationId().toString()).build();
    private final AgreementGateway agreements = mock(AgreementGateway.class);
    private final CurrentUserSession session = mock(CurrentUserSession.class);
    private final AgreementsApi.GetProducerAgreementsOper operation =
            mock(AgreementsApi.GetProducerAgreementsOper.class);

    @Test
    void reusesAnActiveAgreementOnTheExactDescriptorWithoutCreatingAnother() {
        var gateway = fixture();
        page(entry(id, descriptor.getId()), 1);
        var active = agreement(AgreementState.ACTIVE);
        when(agreements.getAgreement(new AgreementRef(id))).thenReturn(active);

        gateway.ensureActiveAgreement(service, descriptor, agreements);

        verify(agreements).getAgreement(new AgreementRef(id));
        verifyNoMoreInteractions(agreements);
        verify(operation).eservicesIdsQuery(service.getId());
        verify(operation).statesQuery("ACTIVE");
        verify(session).set(User.getTenantAdmin(Tenant.COMUNE_DI_MILANO), Tenant.COMUNE_DI_MILANO);
        verify(session).set(User.getTenantAdmin(Tenant.PAGO_PA), Tenant.PAGO_PA);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void createsSelfAgreementAndWaitsForActiveWithOptionalManualApproval(boolean pending) {
        var gateway = fixture();
        page(entry(UUID.randomUUID(), UUID.randomUUID()), 1);
        var draft = agreement(AgreementState.DRAFT);
        var submitted = agreement(pending ? AgreementState.PENDING : AgreementState.ACTIVE);
        var active = agreement(AgreementState.ACTIVE);
        when(agreements.createAgreement(service, descriptor, null)).thenAnswer(call -> {
            verify(session).set(User.getTenantAdmin(Tenant.COMUNE_DI_MILANO), Tenant.COMUNE_DI_MILANO);
            return draft;
        });
        when(agreements.submitAgreement(draft)).thenReturn(submitted);
        when(agreements.awaitState(submitted, Set.of(AgreementState.ACTIVE, AgreementState.PENDING)))
                .thenReturn(submitted);
        when(agreements.activateAgreement(submitted, null)).thenReturn(active);
        when(agreements.awaitState(pending ? active : submitted, Set.of(AgreementState.ACTIVE))).thenReturn(active);

        gateway.ensureActiveAgreement(service, descriptor, agreements);

        verify(agreements).createAgreement(service, descriptor, null);
        verify(agreements, times(pending ? 1 : 0)).activateAgreement(submitted, null);
        verify(agreements).awaitState(pending ? active : submitted, Set.of(AgreementState.ACTIVE));
        verify(session).set(User.getTenantAdmin(Tenant.PAGO_PA), Tenant.PAGO_PA);
    }

    @Test
    void checksFurtherPagesBeforeCreatingASelfAgreement() {
        var gateway = fixture();
        String unrelated = entry(UUID.randomUUID(), UUID.randomUUID());
        String first = String.join(",", java.util.Collections.nCopies(50, unrelated));
        AtomicInteger pages = new AtomicInteger();
        when(operation.execute(any())).thenAnswer(call -> response(
                pages.getAndIncrement() == 0 ? first : entry(id, descriptor.getId()), 51));
        when(agreements.getAgreement(new AgreementRef(id))).thenReturn(agreement(AgreementState.ACTIVE));

        gateway.ensureActiveAgreement(service, descriptor, agreements);

        verify(operation).offsetQuery(0);
        verify(operation).offsetQuery(50);
        verify(agreements, never()).createAgreement(any(), any(), any());
    }

    @Test
    void propagatesSearchFailureAndRestoresSessionWithoutCreatingAgreement() {
        var gateway = fixture();
        var failure = new IllegalStateException("search failed");
        when(operation.execute(any())).thenThrow(failure);

        assertThatThrownBy(() -> gateway.ensureActiveAgreement(service, descriptor, agreements)).isSameAs(failure);

        verifyNoInteractions(agreements);
        verify(session).set(User.getTenantAdmin(Tenant.PAGO_PA), Tenant.PAGO_PA);
    }

    @Test
    void rejectsIncompletePaginationInsteadOfAssumingNoAgreementExists() {
        var gateway = fixture();
        page("", 1);

        assertThatThrownBy(() -> gateway.ensureActiveAgreement(service, descriptor, agreements))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("Incomplete producer agreement page");

        verifyNoInteractions(agreements);
        verify(session).set(User.getTenantAdmin(Tenant.PAGO_PA), Tenant.PAGO_PA);
    }

    @Test
    void rejectsAuthorizationFailureInsteadOfCreatingAgreement() {
        var gateway = fixture();
        when(operation.execute(any())).thenReturn(
                new ResponseBuilder().setStatusCode(403).setContentType(ContentType.JSON).setBody("{}").build());

        assertThatThrownBy(() -> gateway.ensureActiveAgreement(service, descriptor, agreements))
                .isInstanceOf(AssertionError.class).hasMessageContaining("403");

        verifyNoInteractions(agreements);
        verify(session).set(User.getTenantAdmin(Tenant.PAGO_PA), Tenant.PAGO_PA);
    }

    @Test
    void preservesActivationFailureWhenRestoringSessionAlsoFails() {
        var gateway = fixture();
        page("", 0);
        var failure = new IllegalStateException("requirements not satisfied");
        when(agreements.createAgreement(service, descriptor, null)).thenThrow(failure);
        var restorationFailure = new IllegalStateException("restoration failed");
        doThrow(restorationFailure).when(session).set(User.getTenantAdmin(Tenant.PAGO_PA), Tenant.PAGO_PA);

        assertThatThrownBy(() -> gateway.ensureActiveAgreement(service, descriptor, agreements))
                .isSameAs(failure).satisfies(error -> assertThat(error.getSuppressed()).containsExactly(restorationFailure));
    }

    private BffAgreementPrerequisiteGateway fixture() {
        var apiClient = mock(ApiClient.class);
        var api = mock(AgreementsApi.class);
        when(apiClient.agreements()).thenReturn(api);
        when(api.getProducerAgreements()).thenReturn(operation);
        when(operation.eservicesIdsQuery(any())).thenReturn(operation);
        when(operation.statesQuery(any())).thenReturn(operation);
        when(operation.offsetQuery(any())).thenReturn(operation);
        when(operation.limitQuery(any())).thenReturn(operation);
        when(session.getTenant()).thenReturn(Tenant.PAGO_PA);
        when(session.getUser()).thenReturn(User.getTenantAdmin(Tenant.PAGO_PA));
        var rest = new BffAgreementRestClient(
                new TestChainFactory(mock(LastApiResponseStore.class), mock(EntityStore.class)), apiClient);
        return new BffAgreementPrerequisiteGateway(rest, session);
    }

    private void page(String entries, int total) {
        when(operation.execute(any())).thenReturn(response(entries, total));
    }

    private io.restassured.response.Response response(String entries, int total) {
        return new ResponseBuilder().setStatusCode(200).setContentType(ContentType.JSON)
                .setBody("{\"results\":[" + entries + "],\"pagination\":{\"totalCount\":" + total + "}}").build();
    }

    private String entry(UUID agreementId, UUID descriptorId) {
        return "{\"id\":\"" + agreementId + "\",\"descriptor\":{\"id\":\"" + descriptorId + "\"}}";
    }

    private Agreement agreement(AgreementState state) {
        return Agreement.builder().id(id).eserviceId(service.getId()).descriptorId(descriptor.getId())
                .producerId(Tenant.COMUNE_DI_MILANO.getOrganizationId())
                .consumerId(Tenant.COMUNE_DI_MILANO.getOrganizationId()).state(state).build();
    }
}
