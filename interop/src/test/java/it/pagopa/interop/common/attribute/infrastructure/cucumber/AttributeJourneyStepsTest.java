package it.pagopa.interop.common.attribute.infrastructure.cucumber;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.attribute.domain.AttributeKind;
import it.pagopa.interop.common.attribute.infrastructure.cucumber.dto.AttributeAliasConfiguration;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.common.kernel.domain.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AttributeJourneyStepsTest {
    private final EntityStore entityStore = mock(EntityStore.class);
    private final CertifiedAttributeFixtures fixtures = new CertifiedAttributeFixtures();
    private final InteropJourney journey = mock(InteropJourney.class, RETURNS_SELF);
    private final AttributeJourneySteps steps = new AttributeJourneySteps(journey, entityStore, fixtures);

    @Test
    void createsUniqueFixturesAndRegistersEachScenarioAlias() {
        Attribute first = attribute("00000000-0000-0000-0000-000000000021");
        Attribute second = attribute("00000000-0000-0000-0000-000000000022");
        when(journey.createCertifiedAttribute(anyString())).thenReturn(first, second);

        steps.createCertifiedAttributes(UserRole.ADMIN, Tenant.COMUNE_DI_MILANO,
                List.of(alias("CERT-1"), alias("CERT-2")));

        var names = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(journey, times(2)).createCertifiedAttribute(names.capture());
        assertThat(names.getAllValues()).allSatisfy(name -> assertThat(name).contains("-CERTIFIED"));
        assertThat(names.getAllValues()).doesNotHaveDuplicates();
        verify(entityStore).upsert("CERT-1", first);
        verify(entityStore).upsert("CERT-2", second);
        fixtures.requireCreated(TenantRef.of(Tenant.COMUNE_DI_MILANO.getOrganizationId()), first.getRef());
    }

    @Test
    void assignsOnlyTheResolvedScenarioAttributeReferenceAndTracksItsTarget() {
        Attribute created = attribute("00000000-0000-0000-0000-000000000023");
        TenantRef issuer = TenantRef.of(Tenant.COMUNE_DI_MILANO.getOrganizationId());
        TenantRef consumer = TenantRef.of(Tenant.PAGO_PA.getOrganizationId());
        fixtures.registerCreated(created, issuer);
        when(entityStore.getByAliasOrThrow("CERT-1", Attribute.class)).thenReturn(created);

        steps.assignCertifiedAttributes(UserRole.ADMIN, Tenant.COMUNE_DI_MILANO, Tenant.PAGO_PA,
                List.of(alias("CERT-1")));

        verify(journey).withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN);
        verify(journey).assignCertifiedAttribute(consumer, created.getRef());
        assertThat(fixtures.assignments()).containsExactly(
                new CertifiedAttributeFixtures.Assignment(issuer, consumer, created.getRef()));
    }

    @Test
    void refusesToAssignAnAliasThatWasNotCreatedByThisScenario() {
        Attribute existing = attribute("00000000-0000-0000-0000-000000000024");
        when(entityStore.getByAliasOrThrow("CERT-1", Attribute.class)).thenReturn(existing);

        assertThatThrownBy(() -> steps.assignCertifiedAttributes(UserRole.ADMIN, Tenant.COMUNE_DI_MILANO,
                Tenant.PAGO_PA, List.of(alias("CERT-1"))))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("scenario-created");
        verifyNoInteractions(journey);
    }

    @ParameterizedTest
    @CsvSource({"dichiarati,DECLARED,DECL-1", "verificati,VERIFIED,VER-1",
            "discreti,CERTIFIED_DISCRETE,DISC-1"})
    void createsExplicitUnassignedFixturesForOtherSupportedKinds(String type, AttributeKind kind, String alias) {
        Attribute created = Attribute.builder().id(UUID.randomUUID()).kind(kind).build();
        when(journey.createAttribute(eq(kind), anyString())).thenReturn(created);

        steps.createAttributes(UserRole.ADMIN, Tenant.COMUNE_DI_MILANO, type, List.of(alias(alias)));

        verify(journey).withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN);
        verify(journey).createAttribute(eq(kind), contains(alias));
        verify(entityStore).upsert(alias, created);
        verifyNoMoreInteractions(journey);
        assertThat(fixtures.assignments()).isEmpty();
    }

    @Test
    void rejectsUnsupportedTypesAndDuplicateOrAlreadyRegisteredAliasesBeforeCreation() {
        assertThatThrownBy(() -> steps.createAttributes(UserRole.ADMIN, Tenant.COMUNE_DI_MILANO, "ignoti",
                List.of(alias("DECL-1")))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> steps.createAttributes(UserRole.ADMIN, Tenant.COMUNE_DI_MILANO, "dichiarati",
                List.of(alias("DECL-1"), alias("DECL-1")))).isInstanceOf(IllegalArgumentException.class);
        when(entityStore.getByAlias("DECL-1", Attribute.class))
                .thenReturn(java.util.Optional.of(attribute("00000000-0000-0000-0000-000000000025")));
        assertThatThrownBy(() -> steps.createAttributes(UserRole.ADMIN, Tenant.COMUNE_DI_MILANO, "dichiarati",
                List.of(alias("DECL-1")))).isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(journey);
    }

    private static AttributeAliasConfiguration alias(String value) {
        var configuration = new AttributeAliasConfiguration();
        configuration.setAlias(value);
        return configuration;
    }

    private static Attribute attribute(String id) {
        return Attribute.builder().id(UUID.fromString(id)).kind(AttributeKind.CERTIFIED).build();
    }
}
