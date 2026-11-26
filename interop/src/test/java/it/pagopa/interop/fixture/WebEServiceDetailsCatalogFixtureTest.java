package it.pagopa.interop.fixture;

import it.pagopa.infrastructure.context.InMemoryEntityStore;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.attribute.domain.Attributes;
import it.pagopa.interop.common.delegation.domain.Delegation;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.AttributeRef;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.suite.contract.WebEServiceDetailsCatalogPageContractTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class WebEServiceDetailsCatalogFixtureTest {
    @ParameterizedTest
    @MethodSource("scenarios")
    void fixtureUsesItsOwnAttributesForRequirementsAndAssignments(
            boolean otherTenant, boolean async, boolean assignedToConsumer,
            boolean assignedToDelegator, boolean thresholdForConsumer,
            boolean thresholdForDelegator, boolean shouldSeeThresholds, boolean producerIsPortalUser) {
        var store = new InMemoryEntityStore();
        Attribute previous = attribute();
        Attribute later = attribute();
        store.upsert(previous);
        store.upsert(EService.builder().id(UUID.randomUUID()).build());
        store.upsert(Delegation.builder().id(UUID.randomUUID()).build());

        var journey = mock(InteropJourney.class, RETURNS_SELF);
        var descriptors = mock(EServiceDescriptorUseCase.class);
        var creation = mock(EServiceCreationCommand.class);
        var update = mock(UpdateEServiceDescriptorCommand.class);
        when(descriptors.getDefaultEServiceCreationCommand(async)).thenReturn(creation);
        when(descriptors.getUpdateEServiceDescriptorCommand(20, 40)).thenReturn(update);

        List<Attribute> created = new ArrayList<>();
        when(journey.createCertifiedAttribute(anyString())).thenAnswer(invocation -> {
            Attribute attribute = attribute();
            created.add(attribute);
            store.upsert(attribute);
            return attribute;
        });
        when(journey.updateDescriptor(update, EServiceDescriptorState.PUBLISHED)).thenAnswer(invocation -> {
            store.upsert(later);
            return journey;
        });

        var contract = new WebEServiceDetailsCatalogPageContractTest(
                mock(WebBrowserContractValidator.class), journey, descriptors, store);
        var params = contract.new CustomThresholdTestParams(
                async, assignedToConsumer, assignedToDelegator, thresholdForConsumer,
                thresholdForDelegator, shouldSeeThresholds, producerIsPortalUser);
        ReflectionTestUtils.invokeMethod(contract,
                otherTenant ? "prepareCustomThresholdsForOtherTenant" : "prepareCustomThresholdsForYourTenant",
                params);

        assertThat(created).hasSize(otherTenant ? 2 : assignedToConsumer ? 1 : 0);
        ArgumentCaptor<String> names = ArgumentCaptor.forClass(String.class);
        verify(journey, times(created.size())).createCertifiedAttribute(names.capture());
        assertThat(names.getAllValues()).doesNotHaveDuplicates()
                .allSatisfy(name -> assertThat(name).startsWith("attribute-").endsWith("-CERTIFIED"));
        ArgumentCaptor<Attributes> requirements = ArgumentCaptor.forClass(Attributes.class);
        verify(update).attributes(requirements.capture());
        List<Attribute> certified = requirements.getValue().getCertified();
        assertThat(certified).hasSize((thresholdForConsumer ? 1 : 0) + (thresholdForDelegator ? 1 : 0));

        if (thresholdForDelegator) {
            assertThat(certified.get(0).getId()).isEqualTo(created.get(0).getId());
            assertThat(certified.get(0).getDailyCallsPerConsumer()).isEqualTo(35);
        }
        if (thresholdForConsumer) {
            Attribute expected = created.get(otherTenant ? 1 : 0);
            Attribute requirement = certified.get(thresholdForDelegator ? 1 : 0);
            assertThat(requirement.getId()).isEqualTo(expected.getId());
            assertThat(requirement.getDailyCallsPerConsumer()).isEqualTo(30);
        }
        if (assignedToDelegator) {
            verify(journey).assignCertifiedAttribute(
                    TenantRef.of(Tenant.COMUNE_DI_COMUN_NUOVO.getOrganizationId()), created.get(0).getRef());
        }
        if (assignedToConsumer) {
            verify(journey).assignCertifiedAttribute(
                    TenantRef.of(Tenant.COMUNE_DI_POZZALLO.getOrganizationId()),
                    created.get(otherTenant ? 1 : 0).getRef());
        }
        verify(journey, times((assignedToConsumer ? 1 : 0) + (assignedToDelegator ? 1 : 0)))
                .assignCertifiedAttribute(any(TenantRef.class), any(AttributeRef.class));
        verify(journey, never()).assignCertifiedAttribute(any(TenantRef.class), anyInt());
        verify(journey, never()).assignCertifiedAttribute(any(TenantRef.class));
    }

    private static Stream<Arguments> scenarios() {
        return Stream.of(false, true).flatMap(async -> Stream.of(
                Arguments.of(false, async, true, false, true, false, true, false),
                Arguments.of(false, async, false, false, false, false, false, false),
                Arguments.of(false, async, true, false, false, false, false, false),
                Arguments.of(true, async, true, true, true, true, true, false),
                Arguments.of(true, async, true, false, true, false, true, false),
                Arguments.of(false, async, true, false, true, false, false, true)
        ));
    }

    private static Attribute attribute() {
        return Attribute.builder().id(UUID.randomUUID()).build();
    }
}
