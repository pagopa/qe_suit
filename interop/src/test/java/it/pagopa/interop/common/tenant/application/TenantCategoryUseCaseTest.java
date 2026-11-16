package it.pagopa.interop.common.tenant.application;

import it.pagopa.interop.common.attribute.application.AttributeGateway;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.tenant.domain.TenantCategory;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.common.tenant.application.command.CheckTenantCategoryCommand;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TenantCategoryUseCaseTest {
    private final AttributeGateway attributes = mock(AttributeGateway.class);
    private final TenantGateway tenants = mock(TenantGateway.class);
    private final TenantCategoryUseCase useCase = new TenantCategoryUseCase(attributes, tenants);
    private final TenantRef tenant = TenantRef.of(UUID.randomUUID());
    private final UUID l6 = UUID.randomUUID();
    private final UUID l18 = UUID.randomUUID();

    private void stubComuni(Set<UUID> active) {
        when(attributes.findAttributeByOriginAndCode("IPA", "L6"))
                .thenReturn(Optional.of(Attribute.builder().id(l6).build()));
        when(attributes.findAttributeByOriginAndCode("IPA", "L18"))
                .thenReturn(Optional.of(Attribute.builder().id(l18).build()));
        when(tenants.getActiveCertifiedAttributeIds(tenant)).thenReturn(active);
    }

    @Test
    void eitherCodeIsEnoughForMembership() {
        for (UUID matching : Set.of(l6, l18)) {
            stubComuni(Set.of(matching, UUID.randomUUID()));
            useCase.check(new CheckTenantCategoryCommand(tenant, TenantCategory.COMUNI, true));
        }
    }

    @Test
    void verifiesNonMembershipIncludingTenantWithoutActiveAttributes() {
        stubComuni(Set.of(UUID.randomUUID()));
        useCase.check(new CheckTenantCategoryCommand(tenant, TenantCategory.COMUNI, false));
        stubComuni(Set.of());
        useCase.check(new CheckTenantCategoryCommand(tenant, TenantCategory.COMUNI, false));
    }

    @Test
    void reportsBothPositiveAndNegativePreconditionFailures() {
        stubComuni(Set.of());
        assertThatThrownBy(() -> useCase.check(
                new CheckTenantCategoryCommand(tenant, TenantCategory.COMUNI, true)))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining(tenant.id().toString(), "COMUNI");
        stubComuni(Set.of(l6));
        assertThatThrownBy(() -> useCase.check(
                new CheckTenantCategoryCommand(tenant, TenantCategory.COMUNI, false)))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    void l4SupportsBothOverlappingCategories() {
        UUID l4 = UUID.randomUUID();
        when(attributes.findAttributeByOriginAndCode("IPA", "L4"))
                .thenReturn(Optional.of(Attribute.builder().id(l4).build()));
        when(tenants.getActiveCertifiedAttributeIds(tenant)).thenReturn(Set.of(l4));

        useCase.check(new CheckTenantCategoryCommand(
                tenant, TenantCategory.REGIONI_PROVINCE_AUTONOME, true));
        useCase.check(new CheckTenantCategoryCommand(
                tenant, TenantCategory.CONSORZI_ASSOCIAZIONI_REGIONALI, true));
    }

    @Test
    void registryErrorsCannotPassAsNonMembership() {
        var failure = new IllegalStateException("Attribute registry unavailable");
        when(attributes.findAttributeByOriginAndCode(eq("IPA"), anyString())).thenThrow(failure);

        assertThatThrownBy(() -> useCase.check(
                new CheckTenantCategoryCommand(tenant, TenantCategory.COMUNI, false)))
                .isSameAs(failure);
        verifyNoInteractions(tenants);
    }

    @Test
    void missingAlternativeDoesNotBlockPositiveOrNegativeChecks() {
        stubComuni(Set.of(l6));
        when(attributes.findAttributeByOriginAndCode("IPA", "L18")).thenReturn(Optional.empty());

        useCase.check(new CheckTenantCategoryCommand(tenant, TenantCategory.COMUNI, true));
        when(tenants.getActiveCertifiedAttributeIds(tenant)).thenReturn(Set.of());
        useCase.check(new CheckTenantCategoryCommand(tenant, TenantCategory.COMUNI, false));
    }

    @Test
    void noResolvableCodesFailsEvenForNegativeExpectations() {
        for (boolean expected : new boolean[]{true, false}) {
            assertThatThrownBy(() -> useCase.check(
                    new CheckTenantCategoryCommand(tenant, TenantCategory.COMUNI, expected)))
                    .isInstanceOf(AssertionError.class).hasMessageContaining("no IPA attributes", "COMUNI");
        }
        verifyNoInteractions(tenants);
    }
}
