package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.attribute.application.AttributeGateway;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.attribute.domain.AttributeKind;
import it.pagopa.interop.common.attribute.domain.AttributeOwnershipSelection;
import it.pagopa.interop.common.eservice.application.command.RequiredAttributeCommand;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.common.tenant.application.TenantGateway;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class RequiredAttributeUseCaseTest {
    private static final TenantRef PRODUCER = TenantRef.of(Tenant.COMUNE_DI_MILANO.getOrganizationId());
    private static final TenantRef PAGOPA = TenantRef.of(Tenant.PAGO_PA.getOrganizationId());

    @Test
    void resolvesEverySupportedKindFromCurrentAssignmentsInStableOrderWithoutChangingTenants() {
        UUID ownedFirst = id("00000000-0000-0000-0000-000000000001");
        UUID ownedSecond = id("00000000-0000-0000-0000-000000000002");
        UUID notOwnedFirst = id("00000000-0000-0000-0000-000000000003");
        UUID notOwnedSecond = id("00000000-0000-0000-0000-000000000004");
        UUID declared = id("00000000-0000-0000-0000-000000000005");
        UUID verified = id("00000000-0000-0000-0000-000000000006");
        UUID discrete = id("00000000-0000-0000-0000-000000000007");
        var tenantGateway = mock(TenantGateway.class);
        var attributeGateway = mock(AttributeGateway.class);

        when(tenantGateway.getActiveAttributeIds(PAGOPA, AttributeKind.CERTIFIED))
                .thenReturn(Set.of(ownedSecond, ownedFirst));
        when(tenantGateway.getActiveAttributeIds(PRODUCER, AttributeKind.CERTIFIED))
                .thenReturn(Set.of(ownedFirst, ownedSecond, notOwnedSecond, notOwnedFirst));
        when(tenantGateway.getActiveAttributeIds(PAGOPA, AttributeKind.DECLARED)).thenReturn(Set.of());
        when(tenantGateway.getActiveAttributeIds(PRODUCER, AttributeKind.DECLARED)).thenReturn(Set.of(declared));
        when(tenantGateway.getActiveAttributeIds(PAGOPA, AttributeKind.VERIFIED)).thenReturn(Set.of(verified));
        when(tenantGateway.getActiveAttributeIds(PRODUCER, AttributeKind.VERIFIED)).thenReturn(Set.of(verified));
        when(tenantGateway.getActiveAttributeIds(PAGOPA, AttributeKind.CERTIFIED_DISCRETE)).thenReturn(Set.of());
        when(tenantGateway.getActiveAttributeIds(PRODUCER, AttributeKind.CERTIFIED_DISCRETE))
                .thenReturn(Set.of(discrete));
        Map<UUID, AttributeKind> kinds = Map.of(
                ownedFirst, AttributeKind.CERTIFIED,
                notOwnedFirst, AttributeKind.CERTIFIED,
                declared, AttributeKind.DECLARED,
                verified, AttributeKind.VERIFIED,
                discrete, AttributeKind.CERTIFIED_DISCRETE
        );
        when(attributeGateway.getAttribute(any())).thenAnswer(invocation -> {
            UUID id = invocation.<it.pagopa.interop.common.kernel.domain.AttributeRef>getArgument(0).id();
            return Attribute.builder().id(id).kind(kinds.get(id)).build();
        });

        var useCase = new RequiredAttributeUseCase(attributeGateway, tenantGateway);
        var attributes = useCase.resolve(PRODUCER, List.of(
                new RequiredAttributeCommand(AttributeKind.CERTIFIED, 1,
                        AttributeOwnershipSelection.NOT_OWNED_AND_OWNED_BY_PAGOPA),
                new RequiredAttributeCommand(AttributeKind.DECLARED, 1,
                        AttributeOwnershipSelection.NOT_OWNED_BY_PAGOPA),
                new RequiredAttributeCommand(AttributeKind.VERIFIED, 1,
                        AttributeOwnershipSelection.OWNED_BY_PAGOPA),
                new RequiredAttributeCommand(AttributeKind.CERTIFIED_DISCRETE, 1,
                        AttributeOwnershipSelection.NOT_OWNED_BY_PAGOPA)
        ));

        assertThat(attributes.getCertified()).extracting(Attribute::getId)
                .containsExactly(notOwnedFirst, ownedFirst, discrete);
        assertThat(attributes.getDeclared()).extracting(Attribute::getId).containsExactly(declared);
        assertThat(attributes.getVerified()).extracting(Attribute::getId).containsExactly(verified);
        assertThat(attributes.getCertified()).extracting(Attribute::getGroup).containsExactly(1, 1, 1);
        assertThat(attributes.getCertified()).last().extracting(Attribute::getKind)
                .isEqualTo(AttributeKind.CERTIFIED_DISCRETE);
        assertThat(attributes.getDeclared()).extracting(Attribute::getGroup).containsExactly(1);
        assertThat(attributes.getVerified()).extracting(Attribute::getGroup).containsExactly(1);
        verify(tenantGateway, times(1)).getActiveAttributeIds(PRODUCER, AttributeKind.CERTIFIED);
        verify(tenantGateway, times(1)).getActiveAttributeIds(PAGOPA, AttributeKind.CERTIFIED);
        verify(tenantGateway, never()).assignCertifiedAttribute(any(), any());
        verify(tenantGateway, never()).assignDeclaredAttribute(any());
        verify(tenantGateway, never()).assignVerifiedAttribute(any(), any(), any());
    }

    @Test
    void rejectsMissingCandidatesAndAssignmentsWithAnUnexpectedKind() {
        var tenantGateway = mock(TenantGateway.class);
        var attributeGateway = mock(AttributeGateway.class);
        var request = new RequiredAttributeCommand(AttributeKind.CERTIFIED, 1,
                AttributeOwnershipSelection.NOT_OWNED_BY_PAGOPA);
        when(tenantGateway.getActiveAttributeIds(PAGOPA, AttributeKind.CERTIFIED)).thenReturn(Set.of());
        when(tenantGateway.getActiveAttributeIds(PRODUCER, AttributeKind.CERTIFIED)).thenReturn(Set.of());

        assertThatThrownBy(() -> new RequiredAttributeUseCase(attributeGateway, tenantGateway)
                .resolve(PRODUCER, List.of(request)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No active CERTIFIED attribute non posseduto da PagoPA");

        UUID assigned = id("00000000-0000-0000-0000-000000000010");
        when(tenantGateway.getActiveAttributeIds(PRODUCER, AttributeKind.CERTIFIED)).thenReturn(Set.of(assigned));
        when(attributeGateway.getAttribute(any())).thenReturn(
                Attribute.builder().id(assigned).kind(AttributeKind.DECLARED).build());
        assertThatThrownBy(() -> new RequiredAttributeUseCase(attributeGateway, tenantGateway)
                .resolve(PRODUCER, List.of(request)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("expected CERTIFIED");
    }

    private static UUID id(String value) {
        return UUID.fromString(value);
    }
}
