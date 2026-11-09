package it.pagopa.interop.common.attribute.infrastructure.cucumber;

import it.pagopa.interop.common.attribute.domain.AttributeKind;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.RequiredAttributeConfiguration;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.SelectedEServiceDescriptor;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper.RequiredAttributeConfigurationMapper;
import it.pagopa.interop.common.eservice.application.command.RequiredAttributeCommand;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.common.kernel.domain.UserRole;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

class RequiredAttributeJourneyStepsTest {

    @Test
    void validatesProducerMapsTableAndDelegatesToTheJourney() {
        var service = EService.builder().id(UUID.randomUUID())
                .producerId(Tenant.COMUNE_DI_MILANO.getOrganizationId().toString()).build();
        var selected = new SelectedEServiceDescriptor(service, 1);
        var row = new RequiredAttributeConfiguration();
        row.setKind("verificato");
        row.setGroup(1);
        row.setAttribute("VER-1");
        var attribute = Attribute.builder().id(UUID.randomUUID()).kind(AttributeKind.VERIFIED).build();
        var expected = List.of(new RequiredAttributeCommand(AttributeKind.VERIFIED, 1,
                attribute.getRef()));
        var journey = mock(InteropJourney.class, RETURNS_SELF);
        var store = mock(EntityStore.class);
        when(store.getByAliasOrThrow("VER-1", Attribute.class)).thenReturn(attribute);
        var steps = new RequiredAttributeJourneySteps(journey,
                new RequiredAttributeConfigurationMapper(store));

        steps.configureRequiredAttributes(selected, Tenant.COMUNE_DI_MILANO, List.of(row));

        var order = inOrder(journey);
        order.verify(journey).withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN);
        order.verify(journey).configureRequiredAttributes(service, 1,
                TenantRef.of(Tenant.COMUNE_DI_MILANO.getOrganizationId()), expected);
        verifyNoMoreInteractions(journey);
    }

    @Test
    void rejectsProducerDifferentFromTheSelectedServiceBeforeDelegation() {
        var service = EService.builder().id(UUID.randomUUID())
                .producerId(Tenant.COMUNE_DI_MILANO.getOrganizationId().toString()).build();
        var journey = mock(InteropJourney.class, RETURNS_SELF);
        var steps = new RequiredAttributeJourneySteps(journey,
                new RequiredAttributeConfigurationMapper(mock(EntityStore.class)));
        var row = new RequiredAttributeConfiguration();
        row.setKind("certificato");
        row.setGroup(1);
        row.setAttribute("CERT-1");

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> steps.configureRequiredAttributes(
                        new SelectedEServiceDescriptor(service, 1), Tenant.PAGO_PA, List.of(row)))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("does not own");
        verifyNoInteractions(journey);
    }
}
